package com.example.aiiage.jetweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    @SerializedName("sport")
    public Sport sport;

    public class Comfort {
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String info;
    }
}
