package com.github.onlynight.rxdownload.http;

import com.github.onlynight.rxdownload.AndroidDownload;
import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultHttpRequester implements HttpRequester {
    @Override
    public HttpResponse onRequest(AndroidDownloadOptions options, String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        if (options.getHeaders() != null && options.getHeaders().size() > 0) {
            for (AndroidDownload.HttpHeader header : options.getHeaders()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        connection.setReadTimeout(options.getReadTimeout());
        connection.setConnectTimeout(options.getConnectTimeout());
        connection.connect();
        return new HttpResponse()
                .setCode(connection.getResponseCode())
                .setMsg(connection.getResponseMessage())
                .setContentLength(connection.getContentLength())
                .setInputStream(connection.getInputStream());
    }
}
