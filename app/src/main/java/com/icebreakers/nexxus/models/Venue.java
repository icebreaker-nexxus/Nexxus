
package com.icebreakers.nexxus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Venue {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("repinned")
    @Expose
    private Boolean repinned;
    @SerializedName("address_1")
    @Expose
    private String address1;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("localized_country_name")
    @Expose
    private String localizedCountryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getRepinned() {
        return repinned;
    }

    public void setRepinned(Boolean repinned) {
        this.repinned = repinned;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocalizedCountryName() {
        return localizedCountryName;
    }

    public void setLocalizedCountryName(String localizedCountryName) {
        this.localizedCountryName = localizedCountryName;
    }

    @Override
    public String toString() {
        return null;
    }

}
