package com.github.onlynight.rxdownload.task;

import android.text.TextUtils;

import com.github.onlynight.rxdownload.RxDownload;
import com.github.onlynight.rxdownload.options.DownloadOptions;
import com.github.onlynight.rxdownload.utils.MD5Util;

import java.io.File;
import java.util.List;

public class RxDownloadTask {

    private DownloadOptions defaultDownloadOptions;
    private DownloadOptions downloadOptions;
    private DownloadOptions finalOptions;
    private String url;
    private String filename;
    private String downloadKey;

    public RxDownloadTask setDefaultDownloadOptions(DownloadOptions downloadOptions) {
        this.defaultDownloadOptions = downloadOptions;
        return this;
    }

    public RxDownloadTask applyDownloadOptions(DownloadOptions downloadOptions) {
        this.downloadOptions = downloadOptions;
        return this;
    }

    public RxDownloadTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public RxDownloadTask setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public DownloadOptions getDefaultDownloadOptions() {
        return defaultDownloadOptions;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public String getFinalUrl() {

        DownloadOptions options = getFinalOptions();
        if (options != null) {
            String host = options.getHost();
            if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(url) && !url.startsWith("http")) {
                return host + url;
            }
        }

        return url;
    }

    public String getAbsoluteFilename() {
        DownloadOptions options = getFinalOptions();
        if (options != null) {
            String path = options.getDownloadPath();
            if (!TextUtils.isEmpty(path)) {
                return path + File.separator + filename;
            }
        }
        return filename;
    }

    public DownloadOptions getFinalOptions() {
        if (finalOptions == null) {
            try {
                if (defaultDownloadOptions != null) {
                    finalOptions = defaultDownloadOptions.clone();
                }
                if (downloadOptions != null) {
                    if (finalOptions == null) {
                        finalOptions = downloadOptions.clone();
                    } else {
                        String host = downloadOptions.getHost();
                        if (!TextUtils.isEmpty(host)) {
                            finalOptions.setHost(host);
                        }
                        List<RxDownload.HttpHeader> headers = downloadOptions.getHeaders();
                        if (headers != null) {
                            finalOptions.setHeaders(headers);
                        }
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

    public static String generateTaskKey(RxDownloadTask task) throws Exception {
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
