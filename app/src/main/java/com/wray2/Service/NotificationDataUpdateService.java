package com.wray2.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wray2.FragmentsActivity;
import com.wray2.Manager.NotificationChannelsManager;
import com.wray2.R;


public class NotificationDataUpdateService extends Service
{
    private int showAlertNum = 3;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Notification notification = new NotificationCompat.Builder(this, NotificationChannelsManager.MIN)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, FragmentsActivity.class), 0))
                .setSmallIcon(R.drawable.ic_bean)
                .setContentTitle("分类帮日程提醒")
                .setShowWhen(false)
                .setColor(0xFFD7FFD9)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("1.text")
                        .addLine("2.text")
                        .addLine("3.text"))
                .build();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new NotificationDataBinder();
    }

    @Override
    public void onDestroy()
    {
        stopForeground(true);
        super.onDestroy();
    }

    private void updateNotification()
    {

    }

    public class NotificationDataBinder extends Binder
    {
        public void updateData()
        {
            updateNotification();
        }
    }
}
