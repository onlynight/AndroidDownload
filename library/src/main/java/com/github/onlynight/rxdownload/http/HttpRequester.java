package com.github.onlynight.rxdownload.http;

import com.github.onlynight.rxdownload.options.AndroidDownloadOptions;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequester {

    HttpResponse onRequest(AndroidDownloadOptions options, String url) throws IOException;

    class HttpResponse {

        public int code;
        public long contentLength;
        public String msg;
        public InputStream inputStream;

        public int getCode() {
            return code;
        }

        public HttpResponse setCode(int code) {
            this.code = code;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public HttpResponse setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public HttpResponse setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public long getContentLength() {
            return contentLength;
        }

        public HttpResponse setContentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }
    }

}
