package com.franklinho.ridecell.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;



public class ParkingLocation implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cost_per_minute")
    @Expose
    private String costPerMinute;
    @SerializedName("max_reserve_time_mins")
    @Expose
    private Integer maxReserveTimeMins;
    @SerializedName("min_reserve_time_mins")
    @Expose
    private Integer minReserveTimeMins;
    @SerializedName("is_reserved")
    @Expose
    private Boolean isReserved;
    @SerializedName("reserved_until")
    @Expose
    private String reservedUntil;

    /**
     *
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     *     The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     *     The lng
     */
    public String getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     *     The lng
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The costPerMinute
     */
    public String getCostPerMinute() {
        return costPerMinute;
    }

    /**
     *
     * @param costPerMinute
     *     The cost_per_minute
     */
    public void setCostPerMinute(String costPerMinute) {
        this.costPerMinute = costPerMinute;
    }

    /**
     *
     * @return
     *     The maxReserveTimeMins
     */
    public Integer getMaxReserveTimeMins() {
        return maxReserveTimeMins;
    }

    /**
     *
     * @param maxReserveTimeMins
     *     The max_reserve_time_mins
     */
    public void setMaxReserveTimeMins(Integer maxReserveTimeMins) {
        this.maxReserveTimeMins = maxReserveTimeMins;
    }

    /**
     *
     * @return
     *     The minReserveTimeMins
     */
    public Integer getMinReserveTimeMins() {
        return minReserveTimeMins;
    }

    /**
     *
     * @param minReserveTimeMins
     *     The min_reserve_time_mins
     */
    public void setMinReserveTimeMins(Integer minReserveTimeMins) {
        this.minReserveTimeMins = minReserveTimeMins;
    }

    /**
     *
     * @return
     *     The isReserved
     */
    public Boolean getIsReserved() {
        return isReserved;
    }

    /**
     *
     * @param isReserved
     *     The is_reserved
     */
    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    /**
     *
     * @return
     *     The reservedUntil
     */
    public String getReservedUntil() {
        return reservedUntil;
    }

    /**
     *
     * @param reservedUntil
     *     The reserved_until
     */
    public void setReservedUntil(String reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public static List<ParkingLocation> fromJSONArray(JSONArray json) {
        Type listType = new TypeToken<List<ParkingLocation>>() {}.getType();
        Gson gson = new GsonBuilder().create();

        return (List<ParkingLocation>) gson.fromJson(json.toString(), listType);

    }

    public static ParkingLocation fromJSONObject(JSONObject json) {
        Gson gson = new GsonBuilder().create();

        return (ParkingLocation) gson.fromJson(json.toString(), ParkingLocation.class);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.name);
        dest.writeString(this.costPerMinute);
        dest.writeValue(this.maxReserveTimeMins);
        dest.writeValue(this.minReserveTimeMins);
        dest.writeValue(this.isReserved);
        dest.writeString(this.reservedUntil);
    }

    public ParkingLocation() {
    }

    protected ParkingLocation(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lat = in.readString();
        this.lng = in.readString();
        this.name = in.readString();
        this.costPerMinute = in.readString();
        this.maxReserveTimeMins = (Integer) in.readValue(Integer.class.getClassLoader());
        this.minReserveTimeMins = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isReserved = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.reservedUntil = in.readString();
    }

    public static final Parcelable.Creator<ParkingLocation> CREATOR = new Parcelable.Creator<ParkingLocation>() {
        @Override
        public ParkingLocation createFromParcel(Parcel source) {
            return new ParkingLocation(source);
        }

        @Override
        public ParkingLocation[] newArray(int size) {
            return new ParkingLocation[size];
        }
    };
}
