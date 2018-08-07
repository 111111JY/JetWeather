package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/31.
 */
public class Air_Now_City {
//    @SerializedName("air_now_city")
//    public Air_Now city;
//
//    public class Air_Now{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("qlty")
        public String qlty;
        @SerializedName("pm25")
        public String pm25;
//    }


}
