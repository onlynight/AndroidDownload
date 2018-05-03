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

    /**
     * the default GLOBAL download options
     */
    private AndroidDownloadOptions defaultAndroidDownloadOptions;

    /**
     * applied download options, it will replace {@link this#defaultAndroidDownloadOptions}'s property
     */
    private AndroidDownloadOptions downloadOptions;

    /**
     * when you want get the download options, you should use this options.
     */
    private AndroidDownloadOptions finalOptions;

    /**
     * download url.
     * if it start with http, then then {@link AndroidDownloadOptions#getHost()} will never be used,
     * or it will use the {@link AndroidDownloadOptions#getHost()} as host.
     */
    private String url;

    /**
     * final download file name.
     * the absolute file path is {@link AndroidDownloadOptions#getDownloadPath()} + {@link this#filename}
     */
    private String filename;

    /**
     * use the download url and filename to generate the download key to prevent multi request download.
     */
    private String downloadKey;

    public AndroidDownloadTask setDefaultAndroidDownloadOptions(AndroidDownloadOptions androidDownloadOptions) {
        this.defaultAndroidDownloadOptions = androidDownloadOptions;
        return this;
    }

    public AndroidDownloadTask applyDownloadOptions(AndroidDownloadOptions androidDownloadOptions) {
        this.downloadOptions = androidDownloadOptions;
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

                if (downloadOptions != null) {

                    if (finalOptions == null) {
                        finalOptions = downloadOptions.clone();
                    } else {

                        String host = downloadOptions.getHost();
                        if (!TextUtils.isEmpty(host)) {
                            finalOptions.setHost(host);
                        }

                        String downloadPath = downloadOptions.getDownloadPath();
                        if (!TextUtils.isEmpty(downloadPath)) {
                            finalOptions.setDownloadPath(downloadPath);
                        }

                        HttpRequesterFactory factory = downloadOptions.getHttpRequesterFactory();
                        if (factory != null) {
                            finalOptions.setHttpRequesterFactory(factory);
                        }

                        List<AndroidDownload.HttpHeader> headers = downloadOptions.getHeaders();
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
