package com.fdbdfd.fymplayer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fdbdfd.fymplayer.database.MovieDatabaseHelper;

import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class MainActivity extends Activity {

    public static final String TAG_EXIT = "exit"; //APP退出
    private VideoView videoView;
    ArrayList<String> listVideo = new ArrayList<>();
    private int index = 0;//listVideo的下标
    private SQLiteDatabase db;
    private String recordPath; //数据库储存的路径
    private ContentValues values = new ContentValues(); //用于向数据库保存数据
    private long time = 0, position; //播放时间
    private boolean flag = true; //标记（用于停止线程）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Vitamio.isInitialized(this))
            return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        setContentView(R.layout.playlayout);

        videoView = (VideoView) findViewById(R.id.vv);
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); //隐藏状态栏

        MovieDatabaseHelper movieDatabaseHelper = new MovieDatabaseHelper(this,"Movie.db",null,1);
        movieDatabaseHelper.getWritableDatabase();
        db = movieDatabaseHelper.getWritableDatabase();

        searchFile();

        Cursor cursor = db.query("movie",null,null,null,null,null,null);
        if  (cursor != null) {
            while (cursor.moveToNext()) {
                index = cursor.getInt(cursor.getColumnIndex("number"));
                recordPath = cursor.getString(cursor.getColumnIndex("path"));
                position = cursor.getLong(cursor.getColumnIndex("time"));
            }
            cursor.close();
        }

        if (recordPath == null) {
            Log.i("MainActivity","第一次进入");
            playVideo(getPath(), time);
        }else {
            Log.i("MainActivity","断点续播"+position);
            playVideo(recordPath,position);
        }
    }

    /*
    利用MediaStore多媒体数据库获取视频信息
     */
    public void searchFile(){

        String str[] = {MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA};

        Cursor cursor = MainActivity.this.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null,
                null, null);
        if (cursor != null){
            while (cursor.moveToNext()) {
//                System.out.println(cursor.getString(0)); // 视频文件名
//                System.out.println(cursor.getString(1)); // 视频绝对路径
                listVideo.add(cursor.getString(1));
            }
            cursor.close();
        }
    }

    public void playVideo (final String path, final long seekToPosition){

        saveDate();
        MediaController mediaController = new MediaController(this);
        videoView.setVideoPath(path);
        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.seekTo(seekToPosition);
        startThread();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.stopPlayback();
                flag = false; //停止线程
                db.delete("movie", "path = ?", new String[] { path }); //删除数据
                index = index + 1; //下标+1
                playVideo(getPath(),time);
            }
        });
    }

    /*
    获取路径
     */
    public String getPath(){

        if (index > listVideo.size()-1){
            index = 0;
        }
        return listVideo.get(index);
    }

    /*
    APP接受SD卡拔出的广播退出
     */
    protected void onNewIntent (Intent exitIntent) {
        super.onNewIntent(exitIntent);
        if (exitIntent != null) {
            boolean isExit = exitIntent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }
    /*
    保存下标、路径、时间
     */

    public void saveDate (){
        values.put("number", index);
        values.put("path", getPath());
        values.put("time", time);
        db.insert("movie", null, values); // 插入数据
        values.clear();
    }

    /*
    启动线程，一秒更新一次
     */
    public void startThread (){

        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
                    try {
                        Thread.sleep(1000);
                        long temp= videoView.getCurrentPosition();
                        values.put("time", temp);
                        db.update("movie", values, "path = ?", new String[] { getPath() });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }
}
