package com.github.onlynight.rxdownload.http;

public class DefaultHttpRequesterFactory implements HttpRequesterFactory {

    @Override
    public HttpRequester createHttpRequester() {
        return new DefaultHttpRequester();
    }

}
