package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */

public class Hourly {
    @SerializedName("time")
    public String time;
    @SerializedName("tmp")
    public String tmp;
    @SerializedName("cond_txt")
    public String cond_txt;
    @SerializedName("wind_dir")
    public String wind_dir;
    @SerializedName("wind_sc")
    public String wind_sc;

}
