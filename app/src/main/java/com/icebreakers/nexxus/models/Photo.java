package com.icebreakers.nexxus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by radhikak on 4/7/17.
 */

@Parcel
public class Photo {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("highres_link")
    @Expose
    private String highresLink;
    @SerializedName("photo_link")
    @Expose
    private String photoLink;
    @SerializedName("thumb_link")
    @Expose
    private String thumbLink;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("base_url")
    @Expose
    private String baseUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHighresLink() {
        return highresLink;
    }

    public void setHighresLink(String highresLink) {
        this.highresLink = highresLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getThumbLink() {
        return thumbLink;
    }

    public void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
