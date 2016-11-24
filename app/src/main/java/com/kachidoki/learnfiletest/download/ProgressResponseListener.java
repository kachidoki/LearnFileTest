package com.kachidoki.learnfiletest.download;

/**
 * Created by mayiwei on 16/11/23.
 */
public interface ProgressResponseListener {

    void onResponseProgress(long bytesRead, long contentLength, boolean done);

}
