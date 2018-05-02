package com.github.onlynight.rxdownload;

import com.github.onlynight.rxdownload.options.DownloadOptions;
import com.github.onlynight.rxdownload.task.RxDownloadTask;

import java.util.ArrayList;
import java.util.List;

public class RxDownload {

    private String url;
    private String filename;
    private DownloadOptions defaultDownloadOptions;
    private DownloadOptions downloadOptions;

    private static RxDownload instance;

    private RxDownload() {
    }

    public static RxDownload get() {
        if (instance == null) {
            instance = new RxDownload();
        }
        return instance;
    }

    public RxDownload setUrl(String url) {
        this.url = url;
        return this;
    }

    public RxDownload setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public RxDownload setDefaultOptions(DownloadOptions options) {
        this.defaultDownloadOptions = options;
        return this;
    }

    public RxDownload applyOptions(DownloadOptions options) {
        this.downloadOptions = options;
        return this;
    }

    public void downloadAsync() throws Exception {
        downloadAsync(null);
    }

    public void downloadAsync(DownloadListener listener) throws Exception {
        RxDownloadTask task = createTask();
        DownloadManager.getInstance().enqueue(task, listener);
        resetProperty();
    }

    public void downloadSync(DownloadListener listener) throws Exception {
        RxDownloadTask task = createTask();
        DownloadManager.getInstance().execute(task, listener);
        resetProperty();
    }

    private RxDownloadTask createTask() {
        return new RxDownloadTask()
                .setDefaultDownloadOptions(defaultDownloadOptions)
                .setUrl(url).setFilename(filename)
                .applyDownloadOptions(downloadOptions);
    }

    private void resetProperty() {
        downloadOptions = null;
        url = null;
        filename = null;
    }

    public static class HttpHeader {

        private String content;

        public HttpHeader(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    public interface HeadersCallback {

        void operateHeaders(List<HttpHeader> headers);

    }

    public static class SimpleDownloadListener implements DownloadListener {

        @Override
        public void onStart(String key, String url, String path) {
        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onProgressUpdate(int total, int current, float percent) {
        }

        @Override
        public void onFinish(String key, String url, String path) {
        }

    }

    interface DownloadListener {

        void onStart(String key, String url, String path);

        void onError(String error);

        void onProgressUpdate(int total, int current, float percent);

        void onFinish(String downloadId, String url, String path);

    }

}
