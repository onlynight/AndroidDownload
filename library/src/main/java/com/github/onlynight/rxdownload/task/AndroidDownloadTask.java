package com.github.onlynight.rxdownload.task;

import android.text.TextUtils;

import com.github.onlynight.rxdownload.AndroidDownload;
import com.github.onlynight.rxdownload.http.DefaultHttpRequesterFactory;
import com.github.onlynight.rxdownload.http.HttpRequesterFactory;
import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;
import com.github.onlynight.rxdownload.utils.MD5Util;

import java.io.File;
import java.util.List;

public class AndroidDownloadTask {

    private AndroidDownloadOptions defaultAndroidDownloadOptions;
    private AndroidDownloadOptions androidDownloadOptions;
    private AndroidDownloadOptions finalOptions;
    private String url;
    private String filename;
    private String downloadKey;

    public AndroidDownloadTask setDefaultAndroidDownloadOptions(AndroidDownloadOptions androidDownloadOptions) {
        this.defaultAndroidDownloadOptions = androidDownloadOptions;
        return this;
    }

    public AndroidDownloadTask applyDownloadOptions(AndroidDownloadOptions androidDownloadOptions) {
        this.androidDownloadOptions = androidDownloadOptions;
        return this;
    }

    public AndroidDownloadTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public AndroidDownloadTask setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public AndroidDownloadOptions getDefaultAndroidDownloadOptions() {
        return defaultAndroidDownloadOptions;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public String getFinalUrl() {

        AndroidDownloadOptions options = getFinalOptions();
        if (options != null) {
            String host = options.getHost();
            if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(url) && !url.startsWith("http")) {
                return host + url;
            }
        }

        return url;
    }

    public String getAbsoluteFilename() {
        AndroidDownloadOptions options = getFinalOptions();
        if (options != null) {
            String path = options.getDownloadPath();
            if (!TextUtils.isEmpty(path)) {
                return path + File.separator + filename;
            }
        }
        return filename;
    }

    public AndroidDownloadOptions getFinalOptions() {
        if (finalOptions == null) {
            try {

                if (defaultAndroidDownloadOptions != null) {
                    finalOptions = defaultAndroidDownloadOptions.clone();
                }

                if (androidDownloadOptions != null) {

                    if (finalOptions == null) {
                        finalOptions = androidDownloadOptions.clone();
                    } else {

                        String host = androidDownloadOptions.getHost();
                        if (!TextUtils.isEmpty(host)) {
                            finalOptions.setHost(host);
                        }

                        String downloadPath = androidDownloadOptions.getDownloadPath();
                        if (!TextUtils.isEmpty(downloadPath)) {
                            finalOptions.setDownloadPath(downloadPath);
                        }

                        HttpRequesterFactory factory = androidDownloadOptions.getHttpRequesterFactory();
                        if (factory != null) {
                            finalOptions.setHttpRequesterFactory(factory);
                        }

                        List<AndroidDownload.HttpHeader> headers = androidDownloadOptions.getHeaders();
                        if (headers != null) {
                            finalOptions.setHeaders(headers);
                        }
                    }
                }

                if (finalOptions == null) {
                    finalOptions = new AndroidDownloadOptions().setHttpRequesterFactory(new DefaultHttpRequesterFactory());
                } else {
                    if (finalOptions.getHttpRequesterFactory() == null) {
                        finalOptions.setHttpRequesterFactory(new DefaultHttpRequesterFactory());
                    }
                }

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return finalOptions;
    }

    public String generateKey() throws Exception {
        if (TextUtils.isEmpty(downloadKey)) {
            downloadKey = generateTaskKey(this);
        }
        return downloadKey;
    }

    public static String generateTaskKey(AndroidDownloadTask task) throws Exception {
        if (task != null) {
            String url = task.getFinalUrl();
            String filename = task.getAbsoluteFilename();

            if (TextUtils.isEmpty(url)) {
                throw new Exception("download url is empty, you must set the right download path");
            }

            if (TextUtils.isEmpty(filename)) {
                throw new Exception("download file name is empty, you must set the right file download path and name");
            }

            return MD5Util.MD5(url + filename);
        }
        return "";
    }

    @Override
    public String toString() {
        return "url = " + getFinalUrl() + "\n" +
                "filename = " + getAbsoluteFilename();
    }
}
