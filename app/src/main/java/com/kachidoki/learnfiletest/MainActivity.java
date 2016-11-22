package com.kachidoki.learnfiletest;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogFileWhere();
        createExternalStoragePrivateFile();
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

}

