package com.example.aiiage.jetweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 危伟俊 on 2018/7/27.
 * 省份数据类
 */
public class Province extends LitePalSupport {
    private String provinceName;
    private int id;
    private int provinceCode;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
