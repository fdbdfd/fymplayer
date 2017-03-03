package com.fdbdfd.fymplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.fdbdfd.fymplayer.service.ScannerService;
import com.fdbdfd.fymplayer.unit.MediaFile;

import org.litepal.crud.DataSupport;

import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;



public class MainActivity extends Activity implements
        MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnErrorListener{

    private static final String TAG = "MainActivity";
    public static final String TAG_EXIT = "exit"; //APP退出
    private VideoView videoView;
    private List<MediaFile> mediaFiles;
    private int index = 0; //List下标
    private long postion; //断点位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Vitamio.isInitialized(this))
            return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        setContentView(R.layout.playlayout);

        Intent stopIntent = new Intent(MainActivity.this, ScannerService.class);
        stopService(stopIntent);

        SharedPreferences sharedPreferences = getSharedPreferences("media", MODE_PRIVATE);
        index = sharedPreferences.getInt("currentindex", 0);
        String currentPath = sharedPreferences.getString("currentpath", null);
        postion = sharedPreferences.getLong("postion", 0L);

        mediaFiles = DataSupport.findAll(MediaFile.class);
        videoView = (VideoView) findViewById(R.id.vv);
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); //隐藏状态栏

        if (mediaFiles.isEmpty()) {
            Toast.makeText(MainActivity.this, "没有找到视频", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }else if (currentPath == null) {
            mediaPlay(getMediaPath());
        }else {
            mediaPlay(currentPath);
        }
    }

    private void mediaPlay (String path){
        SharedPreferences.Editor editor = getSharedPreferences("media",MODE_PRIVATE).edit();
        editor.putString("currentpath",path);
        editor.apply();
        videoView.setHardwareDecoder(true);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnSeekCompleteListener(this);
        videoView.setOnCompletionListener(this);
    }

    private String getMediaPath(){
        if (index == mediaFiles.size() ){
            index = 0;
        }
        return mediaFiles.get(index).getPath();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG,"onDestroy");
        super.onDestroy();
    }


    /*
    APP接受SD卡拔出的广播退出
     */
    protected void onNewIntent (Intent exitIntent) {
        super.onNewIntent(exitIntent);
        if (exitIntent != null) {
            boolean isExit = exitIntent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                SharedPreferences.Editor editor = getSharedPreferences("media",MODE_PRIVATE).edit();
                editor.putInt("currentindex",index);
                editor.putLong("postion",videoView.getCurrentPosition());
                editor.apply();  //保存播放的视频路径及播放的位置
                MediaFile.deleteAll(MediaFile.class); //清除数据库数据避免重复
                MainActivity.this.finish();
            }
        }
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        nextMedia();
    }

    /*
    视频被删除或打不开时，播放下一个
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        postion = 0L; //避免因视频被删除导致下个视频播放时被定位
        nextMedia();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.seekTo(postion);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        videoView.start();
    }

    protected void nextMedia (){
        Log.v(TAG,"nextMedia");
        videoView.stopPlayback(); //停止视频播放，并释放资源。
        index = index + 1;
        mediaPlay(getMediaPath());
    }
}
