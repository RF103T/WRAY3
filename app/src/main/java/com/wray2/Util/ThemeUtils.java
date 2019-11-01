package com.wray2.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wray2.FragmentsActivity;
import com.wray2.R;
import com.wray2.Service.NotificationDataUpdateService;

public class ThemeUtils {
    public static int getThemeId(Context c) {
        int i;
        //获取通知更新服务
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        if (sharedPreferences.getBoolean("dark_mode", false)){
            i = R.style.darkTheme;
        }
        else i = R.style.AppTheme;

        return i;
    }

}
