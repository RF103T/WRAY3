package com.wray2.Util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.wray2.FragmentsActivity;
import com.wray2.Service.NotificationDataUpdateService;

public class NotificationServiceUtil
{
    static public void startNotificationService(FragmentsActivity activity)
    {
        if(!NotificationDataUpdateService.isServiceRunning)
            activity.startForegroundService(new Intent(activity, NotificationDataUpdateService.class));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String calendarNum = sharedPreferences.getString("show_calendar_num", "3");
        activity.bindServiceConnection(() ->
        {
            activity.getServiceConnection().getBinder().setShowAlertNum(Integer.parseInt(calendarNum));
            activity.getServiceConnection().getBinder().updateData();
            activity.unBindServiceConnection();
        });
    }

    public static class NotificationServiceConnection implements ServiceConnection
    {
        private boolean isConnected = false;
        private NotificationDataUpdateService.NotificationDataBinder binder;
        private ServiceConnectCallback callback;

        public void setCallback(ServiceConnectCallback callback)
        {
            this.callback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            binder = (NotificationDataUpdateService.NotificationDataBinder)service;
            isConnected = true;
            callback.OnServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            isConnected = false;
        }

        @Override
        public void onBindingDied(ComponentName name)
        {
            isConnected = false;
        }

        public boolean isConnected()
        {
            return isConnected;
        }

        public NotificationDataUpdateService.NotificationDataBinder getBinder()
        {
            return binder;
        }
    }

    public interface ServiceConnectCallback
    {
        void OnServiceConnected();
    }
}
