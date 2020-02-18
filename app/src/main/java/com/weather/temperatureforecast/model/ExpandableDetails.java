package com.weather.temperatureforecast.model;

public class ExpandableDetails {

    private String dateAndTime;
    private String temperature;
    private String weather;
    private String wind;
    private String minmaxTemp;
    private String cityName;

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getMinmaxTemp() {
        return minmaxTemp;
    }

    public void setMinmaxTemp(String minmaxTemp) {
        this.minmaxTemp = minmaxTemp;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
