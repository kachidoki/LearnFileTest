package com.kachidoki.learnfiletest.download;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;
import rx.Observer;

/**
 * Created by mayiwei on 16/11/23.
 */
public interface Download {
    @GET("109106851.mp3?vkey=FE4CBE3362739F2DB2A791D279D69E7FDA8AC06A807002FE76B754BE39525EA64C58F0FB86862783CE3AD87000EC1A18935FA5EFBEF5C62C&guid=2718671044")
    Observable<ProgressResponseBody> download();
}
