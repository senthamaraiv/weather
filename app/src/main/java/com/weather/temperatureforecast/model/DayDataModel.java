package com.weather.temperatureforecast.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DayDataModel implements Serializable {

    private Date currentDate;
    private List<DayDetailDataModel> dayDetailDataModels;

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public List<DayDetailDataModel> getDayDetailDataModels() {
        return dayDetailDataModels;
    }

    public void setDayDetailDataModels(List<DayDetailDataModel> dayDetailDataModels) {
        this.dayDetailDataModels = dayDetailDataModels;
    }

}
