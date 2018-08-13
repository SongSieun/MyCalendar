package com.example.dsm2016.weather_api;

public class WeatherItem {
    private String fcstDate;
    private String category;
    private int fcstValue;

    public WeatherItem(String fcstDate, String category, int fcstValue) {
        this.fcstDate = fcstDate;
        this.category = category;
        this.fcstValue = fcstValue;
    }

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
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
                ", fcstValue=" + fcstValue + '\'' +
                ", fcstDate=" + fcstDate + '\'' +
                '}';
    }
}
