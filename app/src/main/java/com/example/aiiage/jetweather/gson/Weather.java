package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    public Update update;
    public Forecast forecast;
    //public LifeStyle lifeStyle;
    public Air_Now_City air_now_city;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("hourly")
    public List<Hourly> hourlyList;
}
