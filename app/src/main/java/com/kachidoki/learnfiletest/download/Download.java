package com.kachidoki.learnfiletest.download;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.Observer;

/**
 * Created by mayiwei on 16/11/23.
 */
public interface Download {
    @Streaming
    @GET("/mobilesafe/shouji360/360safesis/360MobileSafe_6.2.3.1060.apk")
    Observable<ResponseBody> download();
}
