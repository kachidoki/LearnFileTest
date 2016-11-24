package com.kachidoki.learnfiletest.download;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mayiwei on 16/11/23.
 */
public class DownloadHelper {
    private static Download download;
    private static OkHttpClient okHttpClient;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    public static Download getDownloadApi(ProgressResponseListener listener){

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new DownloadProgressInterceptor(listener))
                .build();

        if (download==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://dl.stream.qqmusic.qq.com/")
                    .client(okHttpClient)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            download = retrofit.create(Download.class);
        }
        return download;
    }
}
