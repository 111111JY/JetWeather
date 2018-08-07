package com.example.aiiage.jetweather.download;

/**
 * Created by 危伟俊 on 2018/8/6.
 */
public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
