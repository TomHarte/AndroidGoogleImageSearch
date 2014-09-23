package com.thomasharte.googleimagesearch;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by thomasharte on 20/09/2014.
 */
public class ImageRequest implements Serializable {

    public int imageSize = 0, imageColour = 0, imageType = 0;
    public String siteSearch = null, queryText = null;

    static final int IMAGESIZE_ICON = 1;
    static final int IMAGESIZE_MEDIUM = 2;
    static final int IMAGESIZE_XXLARGE = 3;
    static final int IMAGESIZE_HUGE = 4;
    public String getImageSizeString() {
        switch(imageSize) {
            case IMAGESIZE_ICON: return "icon";
            case IMAGESIZE_MEDIUM: return "medium";
            case IMAGESIZE_XXLARGE: return "xxlarge";
            case IMAGESIZE_HUGE: return "huge";
            default: return null;
        }
    }

    static final int IMAGECOLOUR_BLACK = 1;
    static final int IMAGECOLOUR_BLUE = 2;
    static final int IMAGECOLOUR_BROWN = 3;
    static final int IMAGECOLOUR_GREY = 4;
    static final int IMAGECOLOUR_GREEN = 5;
    static final int IMAGECOLOUR_ORANGE = 6;
    static final int IMAGECOLOUR_PINK = 7;
    static final int IMAGECOLOUR_PURPLE = 8;
    static final int IMAGECOLOUR_RED = 9;
    static final int IMAGECOLOUR_TEAL = 10;
    static final int IMAGECOLOUR_WHITE = 11;
    static final int IMAGECOLOUR_YELLOW = 12;
    public String getImageColourString () {
        switch(imageColour) {
            case IMAGECOLOUR_BLACK: return "black";
            case IMAGECOLOUR_BLUE: return "blue";
            case IMAGECOLOUR_BROWN: return "brown";
            case IMAGECOLOUR_GREY: return "grey";
            case IMAGECOLOUR_GREEN: return "green";
            case IMAGECOLOUR_ORANGE: return "orange";
            case IMAGECOLOUR_PINK: return "pink";
            case IMAGECOLOUR_PURPLE: return "purple";
            case IMAGECOLOUR_RED: return "red";
            case IMAGECOLOUR_TEAL: return "teal";
            case IMAGECOLOUR_WHITE: return "white";
            case IMAGECOLOUR_YELLOW: return "yellow";
            default: return null;
        }
    }

    static final int IMAGETYPE_FACE = 1;
    static final int IMAGETYPE_PHOTO = 2;
    static final int IMAGETYPE_CLIPART = 3;
    static final int IMAGETYPE_LINEART = 4;
    public String getImageTypeString () {
        switch(imageType) {
            case IMAGETYPE_FACE: return "face";
            case IMAGETYPE_PHOTO: return "photo";
            case IMAGETYPE_CLIPART: return "clipart";
            case IMAGETYPE_LINEART: return "lineart";
            default: return null;
        }
    }

    public boolean equals(ImageRequest otherRequest) {
        if(imageColour != otherRequest.imageColour) return false;
        if(imageSize != otherRequest.imageSize) return false;
        if(imageType != otherRequest.imageType) return false;
        if(!Objects.equals(siteSearch, otherRequest.siteSearch)) return false;
        if(!Objects.equals(queryText, otherRequest.queryText)) return false;

        return true;
    }

    public ImageRequest(ImageRequest original) {
        this.imageColour = original.imageColour;
        this.imageType = original.imageType;
        this.imageSize = original.imageSize;
        this.siteSearch = original.siteSearch;
        this.queryText = original.queryText;
    }

    public ImageRequest() {}
}
