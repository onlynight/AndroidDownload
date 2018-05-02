package com.github.onlynight.rxdownload;

import android.text.TextUtils;

import com.github.onlynight.rxdownload.options.DownloadOptions;
import com.github.onlynight.rxdownload.task.RxDownloadTask;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class DownloadManager {

    private static DownloadManager instance;

    private HashMap<String, RxDownloadTask> asyncTasks;
    private HashMap<String, RxDownloadTask> syncTasks;
    private HashMap<String, DownloadRunnable> syncDownloadRunnables;
    private int parallelMaxDownloadSize = 5;

    private Executor downloadExecutor;

    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    private DownloadManager() {
        asyncTasks = new HashMap<>();
        syncTasks = new HashMap<>();
        syncDownloadRunnables = new HashMap<>();
    }

    public void enqueue(RxDownloadTask task, final RxDownload.DownloadListener listener) throws Exception {
        String key = task.generateKey();
        if (TextUtils.isEmpty(key)) {
            throw new Exception("generate download key fail");
        }
        asyncTasks.put(key, task);
        Executor executor = getDownloadExecutor(task);
        executor.execute(new DownloadRunnable(task, new RxDownload.DownloadListener() {

            @Override
            public void onStart(String key, String url, String path) {
                if (listener != null) {
                    listener.onStart(key, url, path);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onError(error);
                }
            }

            @Override
            public void onProgressUpdate(int total, int current, float percent) {
                if (listener != null) {
                    listener.onProgressUpdate(total, current, percent);
                }
            }

            @Override
            public void onFinish(String key, String url, String path) {
                if (listener != null) {
                    listener.onFinish(key, url, path);
                }
                asyncTasks.remove(key);
            }

        }));
    }

    public void execute(RxDownloadTask task, final RxDownload.DownloadListener listener) throws Exception {
        String key = task.generateKey();
        if (TextUtils.isEmpty(key)) {
            throw new Exception("generate download key fail");
        }
        syncTasks.put(key, task);
        DownloadRunnable runnable = new DownloadRunnable(task, new RxDownload.DownloadListener() {

            @Override
            public void onStart(String key, String url, String path) {
                if (listener != null) {
                    listener.onStart(key, url, path);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onError(error);
                }
            }

            @Override
            public void onProgressUpdate(int total, int current, float percent) {
                if (listener != null) {
                    listener.onProgressUpdate(total, current, percent);
                }
            }

            @Override
            public void onFinish(String key, String url, String path) {
                if (listener != null) {
                    listener.onFinish(key, url, path);
                }
                asyncTasks.remove(key);
                syncDownloadRunnables.remove(key);
            }

        });
        syncDownloadRunnables.put(key, runnable);
        runnable.run();
    }

    private Executor getDownloadExecutor(RxDownloadTask task) {
        if (downloadExecutor != null) {
            return downloadExecutor;
        }

        DownloadOptions options = task.getDefaultDownloadOptions();
        if (options != null) {
            parallelMaxDownloadSize = options.getParallelMaxDownloadSize();
        }

        downloadExecutor = new ThreadPoolExecutor(parallelMaxDownloadSize, parallelMaxDownloadSize,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        return downloadExecutor;
    }

}
