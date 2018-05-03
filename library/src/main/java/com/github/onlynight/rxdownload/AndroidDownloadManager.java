package com.github.onlynight.rxdownload;

import android.text.TextUtils;

import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;
import com.github.onlynight.rxdownload.task.AndroidDownloadTask;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class AndroidDownloadManager {

    private static AndroidDownloadManager instance;

    private HashMap<String, AndroidDownloadTask> asyncTasks;
    private HashMap<String, AndroidDownloadTask> syncTasks;
    private HashMap<String, DownloadRunnable> syncDownloadRunnables;
    private int parallelMaxDownloadSize = 5;

    private Executor downloadExecutor;

    public static AndroidDownloadManager getInstance() {
        if (instance == null) {
            instance = new AndroidDownloadManager();
        }
        return instance;
    }

    private AndroidDownloadManager() {
        asyncTasks = new HashMap<>();
        syncTasks = new HashMap<>();
        syncDownloadRunnables = new HashMap<>();
    }

    public void enqueue(AndroidDownloadTask task, final AndroidDownload.DownloadListener listener) throws Exception {
        final String key = task.generateKey();
        if (TextUtils.isEmpty(key)) {
            throw new Exception("generate download key fail");
        }
        boolean exist = checkDownloadExist(key, asyncTasks);
        if (exist) {
            if (listener != null) {
                listener.onProcessing(key, task.getFinalUrl(), task.getAbsoluteFilename());
            }
            return;
        }
        asyncTasks.put(key, task);
        Executor executor = getDownloadExecutor(task);
        executor.execute(new DownloadRunnable(task, new AndroidDownload.DownloadListener() {

            @Override
            public void onStart(String key, String url, String path) {
                if (listener != null) {
                    listener.onStart(key, url, path);
                }
            }

            @Override
            public void onProcessing(String key, String url, String path) {
                if (listener != null) {
                    listener.onProcessing(key, url, path);
                }
            }

            @Override
            public void onError(int errorCode, String error) {
                asyncTasks.remove(key);
                if (listener != null) {
                    listener.onError(errorCode, error);
                }
            }

            @Override
            public void onProgressUpdate(long total, long current, float percent) {
                if (listener != null) {
                    listener.onProgressUpdate(total, current, percent);
                }
            }

            @Override
            public void onFinish(String key, String url, String path) {
                asyncTasks.remove(key);
                if (listener != null) {
                    listener.onFinish(key, url, path);
                }
            }

        }));
    }

    public void execute(AndroidDownloadTask task, final AndroidDownload.DownloadListener listener) throws Exception {
        final String key = task.generateKey();
        if (TextUtils.isEmpty(key)) {
            throw new Exception("generate download key fail");
        }
        boolean exist = checkDownloadExist(key, syncTasks);
        if (exist) {
            if (listener != null) {
                listener.onProcessing(key, task.getFinalUrl(), task.getAbsoluteFilename());
            }
            return;
        }
        syncTasks.put(key, task);
        DownloadRunnable runnable = new DownloadRunnable(task, new AndroidDownload.DownloadListener() {

            @Override
            public void onStart(String key, String url, String path) {
                if (listener != null) {
                    listener.onStart(key, url, path);
                }
            }

            @Override
            public void onProcessing(String key, String url, String path) {
                if (listener != null) {
                    listener.onProcessing(key, url, path);
                }
            }

            @Override
            public void onError(int errorCode, String error) {
                syncTasks.remove(key);
                syncDownloadRunnables.remove(key);
                if (listener != null) {
                    listener.onError(errorCode, error);
                }
            }

            @Override
            public void onProgressUpdate(long total, long current, float percent) {
                if (listener != null) {
                    listener.onProgressUpdate(total, current, percent);
                }
            }

            @Override
            public void onFinish(String key, String url, String path) {
                syncTasks.remove(key);
                syncDownloadRunnables.remove(key);
                if (listener != null) {
                    listener.onFinish(key, url, path);
                }
            }

        });
        syncDownloadRunnables.put(key, runnable);
        runnable.run();
    }

    private boolean checkDownloadExist(String key, HashMap<String, AndroidDownloadTask> tasks) {
        return tasks.containsKey(key);
    }

    private Executor getDownloadExecutor(AndroidDownloadTask task) {
        if (downloadExecutor != null) {
            return downloadExecutor;
        }

        AndroidDownloadOptions options = task.getDefaultAndroidDownloadOptions();
        if (options != null) {
            parallelMaxDownloadSize = options.getParallelMaxDownloadSize();
        }

        downloadExecutor = new ThreadPoolExecutor(parallelMaxDownloadSize, parallelMaxDownloadSize,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        return downloadExecutor;
    }

}
