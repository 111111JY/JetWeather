package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class Basic {
    @SerializedName("location")
    public String citylocation;
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    @SerializedName("update")
    public Update update;

    @SerializedName("cid")
    public String cityId;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
