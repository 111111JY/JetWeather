package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class AQI {
    @SerializedName("city")
    public AQICity city;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("qlty")
        public String qlty;
        @SerializedName("pm25")
        public String pm25;
    }
}
