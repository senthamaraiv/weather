package com.weather.temperatureforecast.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListDataModel implements Serializable {

    @SerializedName("speed")
    @Expose
    private String CityName;
    private List<DayDataModel> dayDataModels;

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public List<DayDataModel> getDayDataModels() {
        return dayDataModels;
    }

    public void setDayDataModels(List<DayDataModel> dayDataModels) {
        this.dayDataModels = dayDataModels;
    }

}
