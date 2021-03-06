package com.fdbdfd.fymplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.fdbdfd.fymplayer.unit.FileUnit;


import java.io.File;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;



public class MainActivity extends Activity implements
        MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnErrorListener{

    private static final String TAG = "MainActivity";
    public static final String TAG_EXIT = "exit"; //APP退出
    private VideoView videoView;
    private ArrayList<String> mediaFiles = new ArrayList<>();
    private ArrayList<String> mediaNames = new ArrayList<>();
    private int index = 0; //List下标
    private long postion; //断点位置
    private String currentPath;
    ProgressDialog progressDialog;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Vitamio.isInitialized(this))
            return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.playlayout);

        SharedPreferences sharedPreferences = getSharedPreferences("media", MODE_PRIVATE);
        index = sharedPreferences.getInt("currentindex", 0);
        currentPath = sharedPreferences.getString("currentpath", null);
        postion = sharedPreferences.getLong("postion", 0L);

        textView = (TextView) findViewById(R.id.name);
        videoView = (VideoView) findViewById(R.id.vv);
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); //隐藏状态栏

        new ScannerAsyncTask().execute();

    }

    private void mediaPlay (String path){
        SharedPreferences.Editor editor = getSharedPreferences("media",MODE_PRIVATE).edit();
        editor.putString("currentpath",path);
        editor.apply();
        textView.setText(getMediaName());
        videoView.setHardwareDecoder(true);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnSeekCompleteListener(this);
        videoView.setOnCompletionListener(this);
    }

    private String getMediaName(){
        if (index >= mediaNames.size() ){
            index = 0;
        }
        return mediaNames.get(index);
    }

    private String getMediaPath(){
        if (index >= mediaFiles.size() ){
            index = 0;
        }
        return mediaFiles.get(index);
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
                MainActivity.this.finish();
            }
        }
    }

    @Override
    protected void onStop() {
        Log.e(TAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.e(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG,"onPause"+videoView.getCurrentPosition());
        SharedPreferences.Editor editor = getSharedPreferences("media",MODE_PRIVATE).edit();
        editor.putInt("currentindex",index);
        editor.putLong("postion",videoView.getCurrentPosition());
        editor.apply();  //保存播放的视频路径及播放的位置
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG,"onRestart");
        super.onRestart();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        postion = 0L;
        nextMedia();
    }

    /*
    视频被删除或打不开时，播放下一个
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        postion = 0L; //避免因视频被删除导致下个视频播放时被定位
        nextMedia();
        return true;//false代表会弹窗告诉无法播放等待点击确认，true代表不会弹窗
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

    class ScannerAsyncTask extends AsyncTask<Void, Integer, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("视频搜索中......");
            progressDialog.show();
            super.onPreExecute();
        }

        private void eachAllMedias(String path){
            File[] files = new File(path).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (FileUnit.isVideo(file)) {
                            mediaFiles.add(file.getAbsolutePath());
                            mediaNames.add(file.getName());
                        }
                    } else if (file.isDirectory()
                            && !file.getPath().contains("/.")) {
                        eachAllMedias(file.getPath());
                    }
                }
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            eachAllMedias(Environment.getExternalStorageDirectory().getAbsolutePath());
            return mediaFiles;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            progressDialog.dismiss();
            if (mediaFiles.isEmpty()) {
                Toast.makeText(MainActivity.this, "没有找到视频", Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }else if (currentPath == null) {
                Log.v(TAG,getMediaPath());
                mediaPlay(getMediaPath());
            }else {
                mediaPlay(currentPath);
            }
            super.onPostExecute(list);
        }
    }
}
