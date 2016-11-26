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
    private Button wtPrabtn;
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
        wtPrabtn = (Button) findViewById(R.id.WritePrivateBtn);
        progressBar = (ProgressBar) findViewById(R.id.progress);


        wtPrabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPrivateFile();
            }
        });


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
                                    Log.e("FileDownload","the file is down");
                                }else {
                                    Log.e("FileDownload","the filesdown is fail!!!!!");
                                }

                            }
                        });
            }
        });

        LogFileWhere();

    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void LogFileWhere(){
        Log.e("File","内部缓存自定义目录 : "+getDir("getDir",MODE_PRIVATE));
        Log.e("File","内部储存的根目录 : "+getFilesDir());
        Log.e("File","内部储存缓存目录 : "+getCacheDir());
        Log.e("File" ,"内部储存缓存代码目录 : "+getCodeCacheDir());

        Log.e("File","外部储存私有部分根目录 : "+ getExternalFilesDir(null));
        Log.e("File","外部储存私有部分音乐 : "+ getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        Log.e("File","外部储存私有部分缓存 : "+getExternalCacheDir());
        for (int i = 0;i<getExternalMediaDirs().length-1;i++){
            Log.e("File","getExternalMediaDirs : "+getExternalMediaDirs()[i].getAbsolutePath());
        }

        Log.e("File","-------------------------------------------------------");
        Log.e("File","Data目录 : "+Environment.getDataDirectory());
        Log.e("File","外部储存的根目录一般不用:"+Environment.getExternalStorageDirectory());
        Log.e("File","外部储存的公共文件根目录: "+Environment.getExternalStoragePublicDirectory("Pub_Test"));
        Log.e("File","下载缓存目录 :"+Environment.getDownloadCacheDirectory());
        Log.e("File","外部储存的状态: "+Environment.getExternalStorageState());

        Log.e("File","DIRECTORY_PICTURES : "+Environment.DIRECTORY_PICTURES);
        Log.e("File","DIRECTORY_MOVIES : "+Environment.DIRECTORY_MOVIES);
    }

    /**
        写在外部储存应用专属位置demo

     */
    public void createExternalStoragePrivateFile(){

        File file = new File(getExternalFilesDir(null),"demoFile.jpg");
        try {
            InputStream is = getResources().openRawResource(R.drawable.supreme);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            os.close();
            is.close();
            Toast.makeText(MainActivity.this,"图片位置在"+getExternalFilesDir(null),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"出错误了哦",Toast.LENGTH_SHORT).show();
        }

    }
    public void deleteExternalStoragePrivateFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(getExternalFilesDir(null), "demoFile.jpg");
        if (file != null) {
            file.delete();
        }
    }

    /**
     *  写在内部储存Demo
     */
    private void createPrivateFile(){
        String filename = "myfile.txt";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            Toast.makeText(MainActivity.this,"文件位置在"+getFilesDir().getAbsolutePath(),Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"出错误了哦",Toast.LENGTH_SHORT).show();
        }
    }




    /**
     *  使用Retrofit下载文件demo
     */
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        Log.e("FileDownload","writeResponseBodyToDisk is start");
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ,"test.apk");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Log.e("FileDownload","InOut is ready");
            try {
                byte[] fileReader = new byte[2048];
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
//                    Log.d("FileDownload", "file download: " + fileSizeDownloaded + " of " + fileSize);
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

