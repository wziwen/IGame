package com.wenziwen.igame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wenziwen.igame.service.IGameService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {
            Intent intent1 = new Intent(context, IGameService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);
        }
    }
}