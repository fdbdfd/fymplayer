package com.fdbdfd.fymplayer.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fdbdfd.fymplayer.MainActivity;
import com.fdbdfd.fymplayer.unit.FileUnit;
import com.fdbdfd.fymplayer.unit.MediaFile;


import org.litepal.LitePal;

import java.io.File;



public class ScannerService extends Service implements Runnable {

    public static final String EXTRA_DIRECTORY = "scan_directory";
    public String path;



    @Override
    public void onCreate() {
        SQLiteDatabase db = LitePal.getDatabase();
        super.onCreate();
        Log.v("fymplayer","搜索服务启动");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void parseIntent(final Intent intent) {

        path = intent.getStringExtra(EXTRA_DIRECTORY);
        new Thread(this).start();

    }


    public void run() {
        eachAllMedias(path);
    }

    /** 递归查找视频 */
    private void eachAllMedias(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (FileUnit.isVideo(file)) {
                        MediaFile mediaFile = new MediaFile();
                        mediaFile.setPath(file.getAbsolutePath());
                        mediaFile.save();
                    }
                } else if (file.isDirectory()
                        && !file.getPath().contains("/.")) {
                    eachAllMedias(file.getPath());
                }
            }
        }

        Intent intent = new Intent(ScannerService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
