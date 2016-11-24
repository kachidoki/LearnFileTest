package com.kachidoki.learnfiletest;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kachidoki.learnfiletest.download.DownloadHelper;
import com.kachidoki.learnfiletest.download.ProgressBean;
import com.kachidoki.learnfiletest.download.ProgressResponseListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Button dlbtn;
    private Button wtbtn;
    private ProgressBar progressBar;
    private final int DOWNLOAD_PROGRESS = 1;
    private Handler downloadHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ProgressBean progressBean = (ProgressBean)msg.obj;
            progressBar.setMax((int) progressBean.getContentLength()/1024);
            progressBar.setProgress((int) (progressBean.getBytesRead()/1024));
        }
    };

    ProgressResponseListener progressResponseListener = new ProgressResponseListener() {
        private ProgressBean progressBean = new ProgressBean();

        @Override
        public void onResponseProgress(long bytesRead, long contentLength, boolean done) {

            progressBean.setBytesRead(bytesRead);
            progressBean.setContentLength(contentLength);
            progressBean.setDone(done);
            Message message = downloadHandler.obtainMessage(DOWNLOAD_PROGRESS,progressBean);
            downloadHandler.sendMessage(message);
            if (done){
                Toast.makeText(MainActivity.this,"download is ok!",Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dlbtn = (Button) findViewById(R.id.downloadBtn);
        wtbtn = (Button) findViewById(R.id.WriteBtn);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        wtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExternalStoragePrivateFile();
            }
        });

        dlbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Test","  onclick  ");
                DownloadHelper.getDownloadApi(progressResponseListener)
                        .download()
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                if(writeResponseBodyToDisk(responseBody)){
                                    Log.e("FileDownload","the music is down");
                                }else {
                                    Log.e("FileDownload","the music down is fail!!!!!");
                                }

                            }
                        });
            }
        });

        LogFileWhere();

    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void LogFileWhere(){
        Log.e("File","getDir : "+getDir("getDir",MODE_PRIVATE).getAbsolutePath());
        Log.e("File","getFilesDir : "+getFilesDir().getAbsolutePath());
        //        Log.e("File","getExternalMediaDirs : "+getFileStreamPath());
        Log.e("File","getCacheDir : "+getCacheDir());
        Log.e("File" ,"getCodeCacheDir : "+getCodeCacheDir());
//        Log.e("File","getDataDir :"+getDataDir());
        Log.e("File","getEnternalFilesDir : "+ getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        Log.e("File","getExternalCacheDir : "+getExternalCacheDir());
        for (int i = 0;i<getExternalMediaDirs().length-1;i++){
            Log.e("File","getExternalMediaDirs : "+getExternalMediaDirs()[i].getAbsolutePath());
        }

        Log.e("File","-------------------------------------------------------");
        Log.e("File","getDataDirectory : "+Environment.getDataDirectory().getAbsolutePath());
        Log.e("File","getDataDirectory : "+Environment.getDataDirectory());
        Log.e("File","getDownLoadCacheDirectory :"+Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.e("File","getExternalStorageDirectory :"+Environment.getExternalStorageDirectory().getAbsolutePath());

        Log.e("File","getExternalStoragePublicDirectory : "+Environment.getExternalStoragePublicDirectory("Pub_Test").getAbsolutePath());
        Log.e("File","getExternalStorageState : "+Environment.getExternalStorageState());
        Log.e("File","DIRECTORY_PICTURES : "+Environment.DIRECTORY_PICTURES);
        Log.e("File","DIRECTORY_MOVIES : "+Environment.DIRECTORY_MOVIES);
    }

    public void createExternalStoragePrivateFile(){
        String filename = "myfile";
        String string = "Hello world!";
        File file = new File(getExternalFilesDir(null),filename);
        FileOutputStream outputStream;
        Log.e("Test"," "+file.mkdir());
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void deleteExternalStoragePrivateFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(getExternalFilesDir(null), "test.jpg");
        if (file != null) {
            file.delete();
        }
    }




    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC) ,"test.mp3");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Log.e("FileDownload","InOut is ready");
            try {
                byte[] fileReader = new byte[99999];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                Log.e("FileDownload","FileOutputStream is work");
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("FileDownload", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}

