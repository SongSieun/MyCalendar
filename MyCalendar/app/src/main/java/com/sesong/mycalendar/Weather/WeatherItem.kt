package com.sesong.mycalendar.Weather

class WeatherItem(var category: String, var fcstValue: Int) {

    override fun toString(): String {
        return "WeatherItem{" +
                "category='" + category + '\'' +
                ", fcstValue='" + fcstValue + '\'' +
                '}'
    }

}