package com.fdbdfd.fymplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.fdbdfd.fymplayer.MainActivity;


public class UsbBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MEDIA_EJECT:
                Intent exitIntent = new Intent(context, MainActivity.class);
                exitIntent.putExtra(MainActivity.TAG_EXIT, true);
                exitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(exitIntent);
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntent);
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED:
                Toast.makeText(context,"视频搜索完成会自动播放，请耐心等待",Toast.LENGTH_SHORT).show();
        }
    }
}
