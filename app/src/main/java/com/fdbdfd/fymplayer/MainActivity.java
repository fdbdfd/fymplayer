package com.fdbdfd.fymplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class MainActivity extends Activity {

    private VideoView videoView;
    ArrayList<String> listVideo = new ArrayList<String>();
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Vitamio.isInitialized(this))
            return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.playlayout);

        videoView = (VideoView) findViewById(R.id.vv);
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        searchFile();

        playVideo(idx);
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
                System.out.println(cursor.getString(0)); // 视频文件名
                System.out.println(cursor.getString(1)); // 视频绝对路径
                listVideo.add(cursor.getString(1));
            }
            cursor.close();
        }
    }

    public void playVideo (int idx){
        MediaController mediaController = new MediaController(this);
        videoView.setVideoPath(listVideo.get(idx));
        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.stopPlayback();
                if(next() ==(listVideo.size()-1)) {
                    Toast.makeText(MainActivity.this, "所有视频已播放,重新播放", Toast.LENGTH_SHORT).show();
                }
                playVideo(next());
            }
        });
    }

    public int next(){
        int size = listVideo.size();
        idx = ++idx % size;
        return idx;
    }
}
