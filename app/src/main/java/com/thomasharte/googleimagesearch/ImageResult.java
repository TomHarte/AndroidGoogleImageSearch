package com.thomasharte.googleimagesearch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by thomasharte on 18/09/2014.
 */
public class ImageResult implements Parcelable, Target {
    private String url, title, thumbnailUrl;
    private int thumbnailWidth, thumbnailHeight;

    private ImageView intoImageView;
    private Context context;

    public ImageResult(Context context, String url, String title, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight) {
        this.url = url;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.context = context;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;

        // warm the cache
        loadThumbnailInto(null);
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void loadThumbnailInto(ImageView imageView) {
        intoImageView = imageView;
        if(imageView != null)
            imageView.setImageResource(android.R.color.transparent);
        Picasso.with(context).load(thumbnailUrl).fit().into(this);
    }

    public void cancelLoadThumbnailInto() {
        intoImageView = null;
    }

    /**
     * @category com.squareup.picasso.Target
     */

    public void onBitmapLoaded(android.graphics.Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom loadedFrom) {
        if(intoImageView != null) {
            intoImageView.setImageBitmap(bitmap);
            intoImageView = null;
        }
    }

    public void onBitmapFailed(android.graphics.drawable.Drawable drawable) {
    }

    public void onPrepareLoad(android.graphics.drawable.Drawable drawable) {
    }


    /**
     * @category Parcelable
     */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(url);
        out.writeString(title);
        out.writeString(thumbnailUrl);
    }

    public ImageResult(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public static final Parcelable.Creator<ImageResult> CREATOR = new Parcelable.Creator<ImageResult>() {
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }

        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };
}
