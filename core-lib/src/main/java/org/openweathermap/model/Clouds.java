package org.openweathermap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class Clouds {

    @SerializedName("all")
    @Expose
    private Integer all;

    /**
     * No args constructor for use in serialization
     */
    public Clouds() {
    }

    /**
     * @param all
     */
    public Clouds(Integer all) {
        this.all = all;
    }

    /**
     * @return The all
     */
    public Integer getAll() {
        return all;
    }

    /**
     * @param all The all
     */
    public void setAll(Integer all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "";
    }

}
