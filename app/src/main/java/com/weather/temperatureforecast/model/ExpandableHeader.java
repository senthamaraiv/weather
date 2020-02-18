package com.weather.temperatureforecast.model;

public class ExpandableHeader {

    private String day;
    private String minMaxTemp;
    private String weather;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMinMaxTemp() {
        return minMaxTemp;
    }

    public void setMinMaxTemp(String minMaxTemp) {
        this.minMaxTemp = minMaxTemp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

}
