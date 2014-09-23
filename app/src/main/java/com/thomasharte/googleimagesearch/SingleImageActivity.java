package com.thomasharte.googleimagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class SingleImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        // grab the supplied image...
        ImageResult image = (ImageResult)getIntent().getParcelableExtra(SearchActivity.IMAGE_RESULT);

        // ... ask Picasso nicely to fetch the graphic...
        ImageView imgImage = (ImageView)findViewById(R.id.imgImage);
        Picasso.with(this).load(image.getUrl()).into(imgImage);

        // TODO: add resize

        // ... and set the title
        getActionBar().setTitle(Html.fromHtml(image.getTitle()));
    }
}
