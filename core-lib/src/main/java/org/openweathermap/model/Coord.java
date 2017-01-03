package org.openweathermap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class Coord{

    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("lat")
    @Expose
    private Double lat;

    /**
     * No args constructor for use in serialization
     */
    public Coord() {
    }

    /**
     * @param lon
     * @param lat
     */
    public Coord(Double lon, Double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * @return The longitude
     */
    public Double getLon() {
        return lon;
    }

    /**
     * @param lon The longitude
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * @return The latitude
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat The latitude
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

}
