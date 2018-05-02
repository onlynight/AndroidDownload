package com.github.onlynight.rxdownload;

import android.os.Handler;
import android.os.Looper;

import com.github.onlynight.rxdownload.task.RxDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadRunnable implements Runnable {

    private RxDownloadTask task;
    private RxDownload.DownloadListener downloadListener;

    private Handler handler = new Handler(Looper.getMainLooper());

    public DownloadRunnable(RxDownloadTask task, RxDownload.DownloadListener listener) {
        this.task = task;
        this.downloadListener = listener;
    }

    public void setTask(RxDownloadTask task) {
        this.task = task;
    }

    public void setDownloadListener(RxDownload.DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public void run() {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(task.getFinalUrl()).openConnection();
            InputStream is = connection.getInputStream();
            final int fileSize = connection.getContentLength();
            byte buffer[] = new byte[2048];
            int offset;
            int downloadSize = 0;
            FileOutputStream fos = new FileOutputStream(new File(task.getAbsoluteFilename()));
            while ((offset = is.read(buffer, 0, 2048)) != -1) {
                fos.write(buffer, 0, offset);
                downloadSize += offset;
                if (downloadListener != null) {
                    final int finalDownloadSize = downloadSize;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.onProgressUpdate(fileSize, finalDownloadSize,
                                    finalDownloadSize / (float) fileSize * 100);
                        }
                    });
                }
            }

            fos.flush();
            fos.close();
            is.close();

            if (downloadListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onFinish("", task.getFinalUrl(), task.getAbsoluteFilename());
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
