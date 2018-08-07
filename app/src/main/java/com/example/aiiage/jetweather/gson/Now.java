package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class   Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond_txt")
    public String cond_txt;
    @SerializedName("cond_code")
    public String cond_code;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
        @SerializedName("code")
        public String cond_code;
    }
}
