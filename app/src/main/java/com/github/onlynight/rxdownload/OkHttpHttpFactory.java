package com.github.onlynight.rxdownload;

import com.github.onlynight.rxdownload.http.HttpRequester;
import com.github.onlynight.rxdownload.http.HttpRequesterFactory;
import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpHttpFactory implements HttpRequesterFactory {

    @Override
    public HttpRequester createHttpRequester() {
        return new OkHttpRequester();
    }

    public static class OkHttpRequester implements HttpRequester {

        @Override
        public HttpResponse onRequest(AndroidDownloadOptions options, String url) throws IOException {
            Request.Builder builder = new Request.Builder().url(url);
            if (options.getHeaders() != null) {
                for (AndroidDownload.HttpHeader header : options.getHeaders()) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(options.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(options.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(options.getWriteTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            Response response = client.newCall(builder.build()).execute();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setCode(response.code()).setMsg(response.message());

            ResponseBody body = response.body();
            if (body != null) {
                httpResponse.setContentLength(body.contentLength())
                        .setInputStream(body.byteStream());
            }

            return httpResponse;
        }

    }
}
