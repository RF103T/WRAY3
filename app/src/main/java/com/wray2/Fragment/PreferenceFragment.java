package com.wray2.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;
import com.wray2.Manager.NotificationChannelsManager;
import com.wray2.R;
import com.wray2.Service.NotificationDataUpdateService;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener
{
    private FragmentsActivity activity;

    private ListPreference showCalendarNum;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);

        showCalendarNum = findPreference("show_calendar_num");
        showCalendarNum.setSummary("预先显示" + showCalendarNum.getValue() + "条日程");
        showCalendarNum.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        activity = (FragmentsActivity)context;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(activity);
        switch (preference.getKey())
        {
            case "show_calendar":
            {
                if (sharedPreferences.getBoolean("show_calendar", false))
                {
                    if (CalendarManager.calendarManager.getAlertList().size() < 1)
                    {
                        infoDialog.setMessage("您还没有添加日程，真的要开启通知吗？").setTitle("尚未添加日程")
                                .setPositiveButton("确定", (dialog, which) -> activity.startForegroundService(new Intent(activity, NotificationDataUpdateService.class)))
                                .setNeutralButton("添加日程", ((dialog, which) -> {((SwitchPreference)preference).setChecked(false);activity.setTabBarPosition(3);}))
                                .setNegativeButton("算了", ((dialog, which) -> ((SwitchPreference)preference).setChecked(false)))
                                .show();
                    }
                    else
                        activity.startForegroundService(new Intent(activity, NotificationDataUpdateService.class));
                }
                else
                    activity.stopService(new Intent(activity, NotificationDataUpdateService.class));
                break;
            }
            case "how_calendar_background":
            {
                infoDialog.setMessage("APP的通知功能需要后台服务才能正常运行，后台服务对电池续航几乎没有影响，而且不使用恶劣手段进行后台保活，请您自行将本APP添加到相应的省电白名单中。").setTitle("如何保护后台服务不被杀死？").setPositiveButton("我知道啦", null).create();
                infoDialog.show();
                break;
            }
            case "show_permission_explanation":
            {
                infoDialog.setView(R.layout.permission_info_dialog).setTitle("为什么我们需要权限？").setPositiveButton("我知道啦", null).create();
                infoDialog.show();
                break;
            }
            case "show_app_info":
            {
                infoDialog.setMessage("分类帮 V1.6.0\nBy.juggier RF103T Elmo").setTitle("关于").setPositiveButton("我知道啦", null).create();
                infoDialog.show();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (preference.getKey().contentEquals("show_calendar_num"))
            showCalendarNum.setSummary("预先显示" + (String)newValue + "条日程");
        return true;
    }
}
