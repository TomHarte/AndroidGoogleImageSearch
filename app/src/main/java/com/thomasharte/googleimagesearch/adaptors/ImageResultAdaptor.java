package com.thomasharte.googleimagesearch.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thomasharte.googleimagesearch.models.ImageResult;
import com.thomasharte.googleimagesearch.R;

import java.util.List;

/**
 * Created by thomasharte on 18/09/2014.
 */
public class ImageResultAdaptor extends ArrayAdapter<ImageResult> {

    private class ViewHolder {
        public ImageView imgImage;
        public ImageResult imageResult;
    }

    public ImageResultAdaptor(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // the view holder pattern in effect: search out the appropriate subviews
        // only the first time the view is created; subsequently store them in
        // a suitable structure via the view's tag as apparently the search is
        // expensive
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgImage = (ImageView)convertView.findViewById(R.id.imgImage);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // push the image, if necessary
        ImageResult image = getItem(position);
        if(viewHolder.imageResult != image) {
            viewHolder.imageResult = image;
            Picasso.with(getContext()).load(image.getThumbnailUrl()).fit().centerInside().into(viewHolder.imgImage);
        }

        return convertView;
    }
}
