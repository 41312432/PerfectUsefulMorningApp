package com.example.perfectusefulmorningapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent bIntent=new Intent("com.example.perfectusefulmorningapp.ACTION_ALARM");
        PendingIntent alarmIntent=PendingIntent.getActivity(context, 100, bIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(alarmIntent);
        alarmIntent.cancel();

        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(222);
    }
}