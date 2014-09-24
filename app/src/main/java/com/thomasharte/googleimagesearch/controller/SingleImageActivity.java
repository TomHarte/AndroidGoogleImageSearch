package com.thomasharte.googleimagesearch.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thomasharte.googleimagesearch.model.ImageResult;
import com.thomasharte.googleimagesearch.R;


public class SingleImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        // grab the supplied image...
        ImageResult image = (ImageResult)getIntent().getParcelableExtra(SearchActivity.IMAGE_RESULT);

        // ... ask Picasso nicely to fetch the graphic...
        ImageView imgImage = (ImageView)findViewById(R.id.imgImage);
        Picasso.with(this).load(image.getUrl()).fit().centerInside().into(imgImage);

        // ... and set the title
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setTitle(Html.fromHtml(image.getTitle()));
    }
}
