package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class Forecast {
    @SerializedName("date")
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    @SerializedName("cond_txt_d")
    public String cond_txt;

    @SerializedName("tmp_max")
    public String max;
    @SerializedName("tmp_min")
    public String min;

    public class Temperature {
        @SerializedName("max")
        public String max;
        @SerializedName("min")
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
