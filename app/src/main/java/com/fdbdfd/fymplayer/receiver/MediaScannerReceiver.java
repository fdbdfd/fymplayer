package com.fdbdfd.fymplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;


import com.fdbdfd.fymplayer.MainActivity;
import com.fdbdfd.fymplayer.service.ScannerService;


public class MediaScannerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MEDIA_MOUNTED:
                Intent scannerIntent = new Intent(context,ScannerService.class);
                scannerIntent.putExtra(ScannerService.EXTRA_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
                context.startService(scannerIntent);
                break;
            case Intent.ACTION_MEDIA_EJECT:
                Intent exitIntent = new Intent(context, MainActivity.class);
                exitIntent.putExtra(MainActivity.TAG_EXIT, true);
                exitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(exitIntent);
                break;
            default:
        }
    }
}
