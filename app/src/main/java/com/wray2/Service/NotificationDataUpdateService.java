package com.wray2.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;
import com.wray2.Manager.NotificationChannelsManager;
import com.wray2.R;

import java.text.ParseException;
import java.util.LinkedList;


public class NotificationDataUpdateService extends Service
{
    public static boolean isServiceRunning = false;

    private final Object lock = new Object();

    private volatile int showAlertNum = 3;

    private volatile boolean loopFlag = true;

    private Thread thread;

    private Runnable runnable = () ->
    {
        while (loopFlag)
        {
            synchronized (lock)
            {
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                try
                {
                    LinkedList<Alert> alertList = CalendarManager.calendarManager.getWillComingAlertList(showAlertNum);
                    for (Alert alert : alertList)
                        style.addLine(alert.getDate() + " - " + alert.getTime() + " - " + alert.getSort());
                }
                catch (ParseException ignore)
                {

                }
                Notification notification = new NotificationCompat.Builder(this, NotificationChannelsManager.MIN)
                        .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, FragmentsActivity.class), 0))
                        .setSmallIcon(R.drawable.ic_bean)
                        .setContentTitle("识圾日程提醒")
                        .setShowWhen(false)
                        .setAutoCancel(false)
                        .setColor(0xFFD7FFD9)
                        .setStyle(style)
                        .build();
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                NotificationChannelsManager.notificationManager.notify(1, notification);
                if (Thread.currentThread().isInterrupted())
                {
                    NotificationChannelsManager.notificationManager.cancel(1);
                    break;
                }
                try
                {
                    lock.wait(900000);
                }
                catch (InterruptedException ignore)
                {

                }
            }
        }
        NotificationChannelsManager.notificationManager.cancel(1);
    };

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
                .setContentTitle("识圾日程提醒")
                .setShowWhen(false)
                .setAutoCancel(false)
                .setColor(0xFFD7FFD9)
                .setStyle(new NotificationCompat.InboxStyle())
                .build();

        startForeground(1, notification);
        isServiceRunning = true;
        thread = new Thread(runnable);
        thread.start();
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
        if (thread != null)
        {
            synchronized (lock)
            {
                loopFlag = false;
                thread.interrupt();
                lock.notify();
            }
        }
        stopForeground(true);
        isServiceRunning = false;
        NotificationChannelsManager.notificationManager.cancel(1);
        super.onDestroy();
    }

    public class NotificationDataBinder extends Binder
    {
        public void updateData()
        {
            synchronized (lock)
            {
                lock.notify();
            }
        }

        public void setShowAlertNum(int n)
        {
            showAlertNum = n;
        }
    }
}
