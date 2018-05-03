package com.github.onlynight.rxdownload.options;

import com.github.onlynight.rxdownload.AndroidDownload;
import com.github.onlynight.rxdownload.http.HttpRequesterFactory;

import java.util.ArrayList;
import java.util.List;

public class AndroidDownloadOptions implements Cloneable {

    private int parallelMaxDownloadSize;
    private String host;
    private String downloadPath;
    private List<AndroidDownload.HttpHeader> headers;
    private HttpRequesterFactory httpRequesterFactory;
    private int connectTimeout = 10 * 1000;
    private int writeTimeout = 10 * 1000;
    private int readTimeout = 10 * 1000;

    public AndroidDownloadOptions() {
        headers = new ArrayList<>();
    }

    public AndroidDownloadOptions setParallelMaxDownloadSize(int parallelMaxDownloadSize) {
        this.parallelMaxDownloadSize = parallelMaxDownloadSize;
        return this;
    }

    public int getParallelMaxDownloadSize() {
        return parallelMaxDownloadSize;
    }

    public AndroidDownloadOptions setHost(String host) {
        this.host = host;
        return this;
    }

    public String getHost() {
        return host;
    }

    public AndroidDownloadOptions setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public AndroidDownloadOptions addHeader(AndroidDownload.HttpHeader header) {
        if (header != null) {
            headers.add(header);
        }
        return this;
    }

    public AndroidDownloadOptions addHeaders(List<AndroidDownload.HttpHeader> headers) {
        if (headers != null) {
            this.headers.addAll(headers);
        }
        return this;
    }

    public AndroidDownloadOptions setHeaders(List<AndroidDownload.HttpHeader> headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return this;
    }

    public AndroidDownloadOptions operateHeader(AndroidDownload.HeadersCallback headersCallback) {
        if (headersCallback != null) {
            headersCallback.operateHeaders(headers);
        }
        return this;
    }

    public AndroidDownloadOptions setHttpRequesterFactory(HttpRequesterFactory httpRequesterFactory) {
        this.httpRequesterFactory = httpRequesterFactory;
        return this;
    }

    public HttpRequesterFactory getHttpRequesterFactory() {
        return httpRequesterFactory;
    }

    public List<AndroidDownload.HttpHeader> getHeaders() {
        return headers;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public AndroidDownloadOptions setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public AndroidDownloadOptions setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public AndroidDownloadOptions setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    @Override
    public AndroidDownloadOptions clone() throws CloneNotSupportedException {
        return (AndroidDownloadOptions) super.clone();
    }
}
