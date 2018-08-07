package com.example.aiiage.jetweather.util;

import android.text.TextUtils;
import com.example.aiiage.jetweather.db.City;
import com.example.aiiage.jetweather.db.County;
import com.example.aiiage.jetweather.db.Province;
import com.example.aiiage.jetweather.gson.Version;
import com.example.aiiage.jetweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 危伟俊 on 2018/7/27.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject CountiesObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(CountiesObject.getString("name"));
                    county.setWeatherId(CountiesObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * HeWeather
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            String json = weatherContent.substring(weatherContent.indexOf("{"), weatherContent.lastIndexOf("}") + 1);
            return new Gson().fromJson(json, Weather.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HeWeather6
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeather6Response(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            String json = weatherContent.substring(weatherContent.indexOf("{"), weatherContent.lastIndexOf("}") + 1);
            return new Gson().fromJson(json, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Version
     * 将返回的JSON数据解析成Version实体类
     */
    public static Version handleVersion(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("apkVersion");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            String json = weatherContent.substring(weatherContent.indexOf("{"), weatherContent.lastIndexOf("}") + 1);
            return new Gson().fromJson(json, Version.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
