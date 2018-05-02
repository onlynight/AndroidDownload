package com.github.onlynight.rxdownload.options;

import com.github.onlynight.rxdownload.RxDownload;

import java.util.ArrayList;
import java.util.List;

public class DownloadOptions implements Cloneable {

    private int parallelMaxDownloadSize;
    private String host;
    private String downloadPath;
    private List<RxDownload.HttpHeader> headers;

    public DownloadOptions() {
        headers = new ArrayList<>();
    }

    public DownloadOptions setParallelMaxDownloadSize(int parallelMaxDownloadSize) {
        this.parallelMaxDownloadSize = parallelMaxDownloadSize;
        return this;
    }

    public int getParallelMaxDownloadSize() {
        return parallelMaxDownloadSize;
    }

    public DownloadOptions setHost(String host) {
        this.host = host;
        return this;
    }

    public String getHost() {
        return host;
    }

    public DownloadOptions setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public DownloadOptions addHeader(RxDownload.HttpHeader header) {
        if (header != null) {
            headers.add(header);
        }
        return this;
    }

    public DownloadOptions addHeaders(List<RxDownload.HttpHeader> headers) {
        if (headers != null) {
            this.headers.addAll(headers);
        }
        return this;
    }

    public DownloadOptions setHeaders(List<RxDownload.HttpHeader> headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return this;
    }

    public DownloadOptions operateHeader(RxDownload.HeadersCallback headersCallback) {
        if (headersCallback != null) {
            headersCallback.operateHeaders(headers);
        }
        return this;
    }

    public List<RxDownload.HttpHeader> getHeaders() {
        return headers;
    }

    @Override
    public DownloadOptions clone() throws CloneNotSupportedException {
        return (DownloadOptions) super.clone();
//        return BeanCloneUtil.cloneTo(this);
    }
}
