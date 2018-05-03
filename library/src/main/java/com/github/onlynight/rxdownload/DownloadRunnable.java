package com.github.onlynight.rxdownload;

import android.os.Handler;
import android.os.Looper;

import com.github.onlynight.rxdownload.http.HttpRequester;
import com.github.onlynight.rxdownload.task.AndroidDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class DownloadRunnable implements Runnable {

    private AndroidDownloadTask task;
    private AndroidDownload.DownloadListener downloadListener;

    private Handler handler = new Handler(Looper.getMainLooper());

    public DownloadRunnable(AndroidDownloadTask task, AndroidDownload.DownloadListener listener) {
        this.task = task;
        this.downloadListener = listener;
    }

    public void setTask(AndroidDownloadTask task) {
        this.task = task;
    }

    public void setDownloadListener(AndroidDownload.DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public void run() {
        download();
    }

    private void saveToFile(InputStream inputStream, final long contentLength) {
        try {
            byte buffer[] = new byte[2048];
            int offset;
            long downloadSize = 0;
            FileOutputStream fos = new FileOutputStream(new File(task.getAbsoluteFilename()));
            while ((offset = inputStream.read(buffer, 0, 2048)) != -1) {
                fos.write(buffer, 0, offset);
                downloadSize += offset;
                if (downloadListener != null) {
                    final long finalDownloadSize = downloadSize;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.onProgressUpdate(contentLength, finalDownloadSize,
                                    finalDownloadSize / (float) contentLength * 100);
                        }
                    });
                }
            }

            fos.flush();
            fos.close();
            inputStream.close();

            if (downloadListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onFinish("", task.getFinalUrl(), task.getAbsoluteFilename());
                    }
                });
            }
        } catch (final IOException e) {
            e.printStackTrace();
            onError(-1, e.getMessage());
        }
    }

    private void download() {
        if (downloadListener != null) {
            try {
                downloadListener.onStart(task.generateKey(), task.getFinalUrl(), task.getAbsoluteFilename());
            } catch (Exception e) {
                e.printStackTrace();
                onError(-1, e.getMessage());
            }
        }
        try {
            HttpRequester requester = task.getFinalOptions().getHttpRequesterFactory().createHttpRequester();
            HttpRequester.HttpResponse response = requester.onRequest(task.getFinalOptions(), task.getFinalUrl());
            if (response != null) {
                if (response.getCode() == 200) {
                    saveToFile(response.getInputStream(), response.getContentLength());
                } else {
                    if (downloadListener != null) {
                        final int errorCode = response.getCode();
                        final String errorMsg = response.getMsg();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadListener.onError(errorCode, errorMsg);
                            }
                        });
                    }
                }
            } else {
                onError(-1, "no HttpResponse");
            }
        } catch (final IOException e) {
            e.printStackTrace();
            onError(-1, e.getMessage());
        }
    }

    private void onError(final int code, final String message) {
        if (downloadListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    downloadListener.onError(code, message);
                }
            });
        }
    }

}
