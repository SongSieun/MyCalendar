package com.sesong.mycalendar.Weather;

public class WeatherItem {
    private String category;
    private int fcstValue;

    public WeatherItem(String category, int fcstValue) {
        this.category = category;
        this.fcstValue = fcstValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFcstValue() {
        return fcstValue;
    }

    public void setFcstValue(int fcstValue) {
        this.fcstValue = fcstValue;
    }

    @Override
    public String toString() {
        return "WeatherItem{" +
                "category='" + category + '\'' +
                ", fcstValue='" + fcstValue + '\'' +
                '}';
    }
}
