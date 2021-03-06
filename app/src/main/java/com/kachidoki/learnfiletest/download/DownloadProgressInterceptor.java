package com.kachidoki.learnfiletest.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by mayiwei on 16/11/23.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private ProgressResponseListener listener;

    public DownloadProgressInterceptor(ProgressResponseListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //拦截
        Response originalResponse = chain.proceed(chain.request());

        //包装响应体并返回
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
