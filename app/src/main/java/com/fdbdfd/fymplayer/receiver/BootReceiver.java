package com.fdbdfd.fymplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fdbdfd.fymplayer.MainActivity;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED ) ) {     // boot
            Intent bootIntent = new Intent(context, MainActivity.class);
            bootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootIntent);
        }
    }
}
