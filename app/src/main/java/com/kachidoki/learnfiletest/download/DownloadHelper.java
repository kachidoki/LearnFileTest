package com.kachidoki.learnfiletest.download;

import android.util.Log;

import java.util.concurrent.TimeUnit;

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
    private static final int DEFAULT_TIMEOUT = 15;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    public static Download getDownloadApi(ProgressResponseListener listener){
        Log.e("Test","getDownloadApi is start::::::");
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new DownloadProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        if (download==null){
            Log.e("Test","Retrofit.Builder is start::::::");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://msoftdl.360.cn")
                    .client(okHttpClient)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            download = retrofit.create(Download.class);
        }
        return download;
    }
}
