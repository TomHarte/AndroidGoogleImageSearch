package com.thomasharte.googleimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thomasharte.googleimagesearch.adaptors.ImageResultAdaptor;
import com.thomasharte.googleimagesearch.fragments.AdvancedSearchSettingsDialogue;
import com.thomasharte.googleimagesearch.models.ImageRequest;
import com.thomasharte.googleimagesearch.models.ImageResult;
import com.thomasharte.googleimagesearch.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SearchActivity extends Activity implements AdvancedSearchSettingsDialogue.AdvancedSearchSettingsDialogueListener {

    static final public String IMAGE_RESULT = "imageResult";

    private ArrayList<ImageResult> images;
    private ImageResultAdaptor imagesAdaptor;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // grab the progress bar
        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);

        // establish an HTTP cache, since we're going to be doing
        // a lot of repeat requests when scrolling
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 50 * 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        }
        catch (IOException e) {
            Log.i("HTTP", "HTTP response cache installation failed:" + e);
        }

        // create and attach an images adaptor
        images = new ArrayList<ImageResult>();
        imagesAdaptor = new ImageResultAdaptor(this, images);

        GridView gvImages = (GridView)findViewById(R.id.gvImages);
        gvImages.setAdapter(imagesAdaptor);

        // add an on-scroll listener to kick off image fetches
        gvImages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount > totalItemCount - 6)
                {
                    considerFetchingMoreItems();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
        });

        // set an on-item-click listener so that we can segue into full-screen
        // view for individual image results
        gvImages.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(SearchActivity.this, SingleImageActivity.class);
                        intent.putExtra(IMAGE_RESULT, images.get(i));
                        startActivity(intent);
                    }
                }
        );
    }

    protected void onStop() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // get rid of the application icon in the top left
        getActionBar().setDisplayShowHomeEnabled(false);

        // ensure the search view is expanded
        MenuItem searchViewItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search for...");

        // make sure we know about entered text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                submitNewQueryWithText(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() > 2) {
                    submitNewQueryWithText(s);
                }
                return true;
            }

            private void submitNewQueryWithText(String s) {
                ImageRequest newRequest = (nextRequest != null) ? new ImageRequest(nextRequest) : new ImageRequest();
                newRequest.queryText = s;
                setNextImageRequest(newRequest);
            }
        });

        return true;
    }

    public void onShowAdvancedSettings(MenuItem menuItem) {
        AdvancedSearchSettingsDialogue settingsDialogue = new AdvancedSearchSettingsDialogue();

        Bundle arguments = new Bundle();
        arguments.putSerializable(AdvancedSearchSettingsDialogue.IMAGE_REQUEST, (nextRequest != null) ? nextRequest : new ImageRequest());
        settingsDialogue.setArguments(arguments);

        settingsDialogue.show(getFragmentManager(), "Search Options");
    }

    public void onRequestChanged(ImageRequest newRequest) {
        setNextImageRequest(newRequest);
    }

    /**
     * @category requests
     */
    private ImageRequest nextRequest;

    void setNextImageRequest(ImageRequest request) {
        nextRequest = request;
        startNextImageRequest();
    }

    void startNextImageRequest() {
        cursorPosition = 0;
        images.clear();
        imagesAdaptor.notifyDataSetChanged();
        considerFetchingMoreItems();
    }

    private Boolean isFetchingMoreItems = false;
    private Integer cursorPosition = 0;
    private void considerFetchingMoreItems() {
        final ImageRequest currentRequest = nextRequest;
        if(currentRequest == null) return;

        if(isFetchingMoreItems || (currentRequest.queryText == null)) return;
        pbLoading.setVisibility(View.VISIBLE);
        isFetchingMoreItems = true;

        String nextFetchUrl;
        nextFetchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8";
        nextFetchUrl += "&start=" + cursorPosition;
        nextFetchUrl += "&q=" + Uri.encode(currentRequest.queryText);
        if (currentRequest.imageColour != 0)
           nextFetchUrl += "&imgcolor=" + currentRequest.getImageColourString();
        if (currentRequest.imageSize != 0)
            nextFetchUrl += "&imgsz=" + currentRequest.getImageSizeString();
        if (currentRequest.imageType != 0)
            nextFetchUrl += "&imgtype=" + currentRequest.getImageTypeString();
        if (currentRequest.siteSearch != null)
            nextFetchUrl += "&sitesearch=" + currentRequest.siteSearch;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(nextFetchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONObject responseData = response.optJSONObject("responseData");
                ArrayList<ImageResult> newImages = null;

                if (responseData != null) {
                    newImages = new ArrayList<ImageResult>();

                    try {
                        JSONArray resultsJSON = responseData.getJSONArray("results");
                        cursorPosition += resultsJSON.length();

                        for (int i = 0; i < resultsJSON.length(); i++) {
                            JSONObject resultJSON = resultsJSON.getJSONObject(i);

                            String title = resultJSON.optString("title");
                            String url = resultJSON.optString("url");
                            String thumbnailUrl = resultJSON.optString("url");
                            int thumbnailWidth = resultJSON.getInt("tbWidth");
                            int thumbnailHeight = resultJSON.getInt("tbHeight");

                            if (title != null && url != null && thumbnailUrl != null) {
                                ImageResult res = new ImageResult(SearchActivity.this, url, title, thumbnailUrl, thumbnailWidth, thumbnailHeight);
                                newImages.add(res);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                didGetNewImagesForRequest(newImages, currentRequest);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                didGetNewImagesForRequest(null, currentRequest);
            }
        });
    }

    private void didGetNewImagesForRequest(ArrayList<ImageResult> newImages, ImageRequest request) {
        isFetchingMoreItems = false;
        pbLoading.setVisibility(View.INVISIBLE);

        if(newImages != null)
        {
            if(request.equals(nextRequest)) {
                images.addAll(newImages);
                imagesAdaptor.notifyDataSetChanged();
            } else {
                startNextImageRequest();
            }
        }
    }
}
