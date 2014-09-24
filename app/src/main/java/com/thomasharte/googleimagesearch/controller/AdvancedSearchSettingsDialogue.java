package com.thomasharte.googleimagesearch.controller;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.thomasharte.googleimagesearch.model.ImageRequest;
import com.thomasharte.googleimagesearch.R;


public class AdvancedSearchSettingsDialogue extends DialogFragment {

    public static final String IMAGE_REQUEST = "imageRequest";
    public ImageRequest imageRequest;

    private Spinner spImageSizes, spImageColours, spImageTypes;
    private EditText etSiteFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_advanced_search_settings, container);
        getDialog().setTitle("Search Options");

        // get the subviews
        spImageSizes = (Spinner)view.findViewById(R.id.spImageSizes);
        spImageColours = (Spinner)view.findViewById(R.id.spImageColours);
        spImageTypes = (Spinner)view.findViewById(R.id.spImageTypes);
        etSiteFilter = (EditText)view.findViewById(R.id.etSiteFilter);

        // get the current request and establish the subview state appropriate
        imageRequest = (ImageRequest)getArguments().getSerializable(IMAGE_REQUEST);
        spImageSizes.setSelection(imageRequest.imageSize);
        spImageColours.setSelection(imageRequest.imageColour);
        spImageTypes.setSelection(imageRequest.imageType);
        etSiteFilter.setText(imageRequest.siteSearch);

        // ensure all the spinners update the search immediately
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateImageRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        spImageSizes.setOnItemSelectedListener(onItemSelectedListener);
        spImageColours.setOnItemSelectedListener(onItemSelectedListener);
        spImageTypes.setOnItemSelectedListener(onItemSelectedListener);

        // ensure the text box updates the search immediately
        etSiteFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                updateImageRequest();
                return false;
            }
        });

        return view;
    }

    public void updateImageRequest () {
        // create a copy of the existing request and modify
        // it according to the settings we know about
        ImageRequest newRequest = new ImageRequest(imageRequest);

        newRequest.imageSize = spImageSizes.getSelectedItemPosition();
        newRequest.imageColour = spImageColours.getSelectedItemPosition();
        newRequest.imageType = spImageTypes.getSelectedItemPosition();

        newRequest.siteSearch = etSiteFilter.getText().toString();
        if(newRequest.siteSearch.length() == 0)
            newRequest.siteSearch = null;

        // if that effected an actual change, notify the activity
        if(!newRequest.equals(imageRequest)) {
            AdvancedSearchSettingsDialogueListener activity =
                    (AdvancedSearchSettingsDialogueListener) getActivity();
            activity.onRequestChanged(newRequest);
        }
    }

//    @Override
//    public void onDismiss(DialogInterface dialogue) {
//        updateImageRequest();
//        super.onDismiss(dialogue);
//    }

    public interface AdvancedSearchSettingsDialogueListener {
        void onRequestChanged(ImageRequest newRequest);
    }
}
