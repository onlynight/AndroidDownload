package com.github.onlynight.rxdownload;

import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;
import com.github.onlynight.rxdownload.task.AndroidDownloadTask;

import java.util.List;

public class AndroidDownload {

    private String url;
    private String filename;
    private AndroidDownloadOptions defaultAndroidDownloadOptions;
    private AndroidDownloadOptions androidDownloadOptions;

    private static AndroidDownload instance;

    private AndroidDownload() {
    }

    public static AndroidDownload get() {
        if (instance == null) {
            instance = new AndroidDownload();
        }
        return instance;
    }

    public AndroidDownload setUrl(String url) {
        this.url = url;
        return this;
    }

    public AndroidDownload setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public AndroidDownload setDefaultOptions(AndroidDownloadOptions options) {
        this.defaultAndroidDownloadOptions = options;
        return this;
    }

    public AndroidDownload applyOptions(AndroidDownloadOptions options) {
        this.androidDownloadOptions = options;
        return this;
    }

    public void downloadAsync() throws Exception {
        downloadAsync(null);
    }

    public void downloadAsync(DownloadListener listener) throws Exception {
        AndroidDownloadTask task = createTask();
        AndroidDownloadManager.getInstance().enqueue(task, listener);
        resetProperty();
    }

    public void downloadSync(DownloadListener listener) throws Exception {
        AndroidDownloadTask task = createTask();
        AndroidDownloadManager.getInstance().execute(task, listener);
        resetProperty();
    }

    private AndroidDownloadTask createTask() {
        return new AndroidDownloadTask()
                .setDefaultAndroidDownloadOptions(defaultAndroidDownloadOptions)
                .setUrl(url).setFilename(filename)
                .applyDownloadOptions(androidDownloadOptions);
    }

    /**
     * when create {@link AndroidDownloadTask} finished.
     * reset the specific download property.
     */
    private void resetProperty() {
        androidDownloadOptions = null;
        url = null;
        filename = null;
    }

    /**
     * http header
     */
    public static class HttpHeader {

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public HttpHeader setKey(String key) {
            this.key = key;
            return this;
        }

        public String getValue() {
            return value;
        }

        public HttpHeader setValue(String value) {
            this.value = value;
            return this;
        }

    }

    /**
     * if you want edit head you added,
     * this callback provide you a entry point.
     */
    public interface HeadersCallback {

        void operateHeaders(List<HttpHeader> headers);

    }

    /**
     * simple download callback listener.
     * <p>
     * if you don't need all callback method, you can use this listener to instead.
     */
    public static class SimpleDownloadListener implements DownloadListener {

        @Override
        public void onStart(String key, String url, String path) {
        }

        @Override
        public void onProcessing(String key, String url, String path) {
        }

        @Override
        public void onError(int errorCode, String error) {
        }

        @Override
        public void onProgressUpdate(long total, long current, float percent) {
        }

        @Override
        public void onFinish(String key, String url, String path) {
        }

    }

    /**
     * download callback listener
     * how to generate download key.
     * <p>
     * key = md5( {download_url} + {absolute_path} );
     */
    interface DownloadListener {

        /**
         * on download progress start
         *
         * @param key  download key
         * @param url  download url
         * @param path save file path
         */
        void onStart(String key, String url, String path);

        /**
         * on download processing, you can't download the same file in the same time
         *
         * @param key  download key
         * @param url  download url
         * @param path save file path
         */
        void onProcessing(String key, String url, String path);

        /**
         * on download error
         *
         * @param errorCode error code
         * @param error     error msg
         */
        void onError(int errorCode, String error);

        /**
         * on download progress update
         *
         * @param total   download file length
         * @param current current download file length
         * @param percent download percent
         */
        void onProgressUpdate(long total, long current, float percent);

        /**
         * on finish download
         *
         * @param key  download key
         * @param url  download url
         * @param path save file path
         */
        void onFinish(String key, String url, String path);

    }

}
