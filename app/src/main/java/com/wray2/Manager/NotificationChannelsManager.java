package com.wray2.Manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import java.util.Arrays;

public class NotificationChannelsManager
{
    public final static String MIN = "min";

    public static NotificationManager notificationManager;

    public static void createAllNotificationChannels(Context context)
    {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        NotificationChannel channel = new NotificationChannel(MIN, "日程表提醒", NotificationManager.IMPORTANCE_MIN);
        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
    }
}
