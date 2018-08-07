package com.example.aiiage.jetweather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 危伟俊 on 2018/8/1.
 */
public class SharePreUtil {
    private static SharedPreferences sp;

    /** 保存数据 **/
    public static void saveBoolean(Context ctx, String key, boolean value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /** 取出数据 **/
    public static Boolean getBoolean(Context ctx, String key, boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }
}
