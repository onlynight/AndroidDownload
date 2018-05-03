package com.github.onlynight.rxdownload;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        progressBar1 = findViewById(R.id.progress_bar1);

        requestPermission();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        String permission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permission, 1);
    }

    public void onDownloadClick(View view) {
        testDownload();
        testDownloadFail();
    }

    private void testDownload() {
        try {
            AndroidDownloadOptions options = new AndroidDownloadOptions().setHost("http://nc-release.wdjcdn.com/")
                    .setHttpRequesterFactory(new OkHttpHttpFactory())
                    .setParallelMaxDownloadSize(5)
                    .setDownloadPath("/sdcard/Download");

            AndroidDownload.get()
                    .setDefaultOptions(options)
                    .setFilename("wandoujia.apk")
                    .setUrl("files/jupiter/5.52.20.13520/wandoujia-wandoujia_web.apk")
                    .downloadAsync(new AndroidDownload.SimpleDownloadListener() {

                        @Override
                        public void onError(int errorCode, String error) {
                            System.out.println("error code = " + errorCode + " error msg = " + error);
                        }

                        @Override
                        public void onProgressUpdate(long total, long current, float percent) {
                            progressBar.setProgress((int) (percent * 10));
                        }

                        @Override
                        public void onFinish(String downloadId, String url, String path) {
                            System.out.println(url + " =====> download finish");
                        }

                        @Override
                        public void onProcessing(String key, String url, String path) {
                            super.onProcessing(key, url, path);
                            System.out.println("url = " + url + " =====> is in processing");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDownloadFail() {
        try {
            AndroidDownloadOptions options1 = new AndroidDownloadOptions()
                    .setDownloadPath("/sdcard/temp");

            AndroidDownload.get()
                    .setFilename("wandoujia1.apk")
                    .setUrl("http://nc-release.wdjcdn.com/files/jupiter/5.52.20.13520/wandoujia-wandoujia_web.apk")
                    .applyOptions(options1)
                    .downloadAsync(new AndroidDownload.SimpleDownloadListener() {

                        @Override
                        public void onError(int errorCode, String error) {
                            System.out.println("error code = " + errorCode + " error msg = " + error);
                        }

                        @Override
                        public void onProgressUpdate(long total, long current, float percent) {
                            progressBar1.setProgress((int) (percent * 10));
                        }

                        @Override
                        public void onFinish(String key, String url, String path) {
                            System.out.println(url + " =====> download finish");
                        }

                        @Override
                        public void onProcessing(String key, String url, String path) {
                            super.onProcessing(key, url, path);
                            System.out.println("url = " + url + " =====> is in processing");
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
