package com.fdbdfd.fymplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fdbdfd.fymplayer.MainActivity;


public class UsbBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MEDIA_EJECT:
                Intent exitIntent = new Intent(context, MainActivity.class);
                exitIntent.putExtra(MainActivity.TAG_EXIT, true);
                context.startActivity(exitIntent);
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntent);
                break;
        }
    }
}
