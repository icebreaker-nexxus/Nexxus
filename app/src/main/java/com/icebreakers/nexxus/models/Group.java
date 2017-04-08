
package com.icebreakers.nexxus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Group {

    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("join_mode")
    @Expose
    private String joinMode;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("urlname")
    @Expose
    private String urlname;
    @SerializedName("who")
    @Expose
    private String who;
    @SerializedName("key_photo")
    @Expose
    private KeyPhoto keyPhoto;
    @SerializedName("photo")
    @Expose
    private Photo photo;
    @SerializedName("category")
    @Expose
    private Category category;

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJoinMode() {
        return joinMode;
    }

    public void setJoinMode(String joinMode) {
        this.joinMode = joinMode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getUrlname() {
        return urlname;
    }

    public void setUrlname(String urlname) {
        this.urlname = urlname;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public KeyPhoto getKeyPhoto() {
        return keyPhoto;
    }

    public void setKeyPhoto(KeyPhoto keyPhoto) {
        this.keyPhoto = keyPhoto;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return null;
    }

}
