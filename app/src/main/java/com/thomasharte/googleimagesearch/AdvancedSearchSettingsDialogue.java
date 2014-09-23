package com.thomasharte.googleimagesearch;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class AdvancedSearchSettingsDialogue extends DialogFragment {

    public static final String IMAGE_REQUEST = "imageRequest";
    public ImageRequest imageRequest;

    private Spinner spImageSizes, spImageColours, spImageTypes;
    private EditText etSiteFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_advanced_search_settings, container);
        getDialog().setTitle("Search Options");

        imageRequest = (ImageRequest)getArguments().getSerializable(IMAGE_REQUEST);

        spImageSizes = (Spinner)view.findViewById(R.id.spImageSizes);
        spImageColours = (Spinner)view.findViewById(R.id.spImageColours);
        spImageTypes = (Spinner)view.findViewById(R.id.spImageTypes);
        etSiteFilter = (EditText)view.findViewById(R.id.etSiteFilter);

        spImageSizes.setSelection(imageRequest.imageSize);
        spImageColours.setSelection(imageRequest.imageColour);
        spImageTypes.setSelection(imageRequest.imageType);
        etSiteFilter.setText(imageRequest.siteSearch);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialogue) {

        ImageRequest newRequest = new ImageRequest(imageRequest);

        newRequest.imageSize = spImageSizes.getSelectedItemPosition();
        newRequest.imageColour = spImageColours.getSelectedItemPosition();
        newRequest.imageType = spImageTypes.getSelectedItemPosition();

        newRequest.siteSearch = etSiteFilter.getText().toString();
        if(newRequest.siteSearch.length() == 0)
            newRequest.siteSearch = null;

        if(!newRequest.equals(imageRequest)) {
            AdvancedSearchSettingsDialogueListener activity =
                    (AdvancedSearchSettingsDialogueListener) getActivity();
            activity.onRequestChanged(newRequest);
        }

        super.onDismiss(dialogue);
    }

    public interface AdvancedSearchSettingsDialogueListener {
        void onRequestChanged(ImageRequest newRequest);
    }
}
