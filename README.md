AndroidDownload
===============

# WHY?

Why don't use android DownloadManger? In fact, it's easy to use android DownloadManger to download file. But you can't customize the download options, such as HTTP pipeline, download path, request header, and continue download when breakdown. So enjoy use **AndroidDownload**.

# VERSION

Now, it's an early version, it just support download file and configuration some options, and reset HTTP request pipeline.

# GRADLE

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```java
dependencies {
    implementation 'com.github.onlynight:AndroidDownload:0.0.1'
}
```

# USE

It is simple to use **AndroidDownload**:

```java
AndroidDownloadOptions options = new AndroidDownloadOptions().setHost("http://nc-release.wdjcdn.com/")
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

        });
```

# UPDATE

The next step is to support big file download and continue download when breakdown.
