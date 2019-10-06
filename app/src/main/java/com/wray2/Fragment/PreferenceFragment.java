package com.wray2.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wray2.FragmentsActivity;
import com.wray2.R;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
    private FragmentsActivity activity;

    private Preference showPermissionExplanation;
    private Preference showAppInfo;

    private ListPreference showCalendarNum;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);

        showPermissionExplanation = findPreference("show_permission_explanation");
        showPermissionExplanation.setOnPreferenceClickListener(this);

        showAppInfo = findPreference("show_app_info");
        showAppInfo.setOnPreferenceClickListener(this);

        showCalendarNum = findPreference("show_calendar_num");
        showCalendarNum.setSummary("预先显示" + showCalendarNum.getValue() + "天的日程");
        showCalendarNum.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        activity = (FragmentsActivity)context;
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        AlertDialog.Builder permissionsInfoDialog = new AlertDialog.Builder(activity);
        if (preference.getKey().contentEquals("show_permission_explanation"))
            permissionsInfoDialog.setView(R.layout.permission_info_dialog).setTitle("为什么我们需要权限？").setPositiveButton("我知道啦", null).create();
        else
            permissionsInfoDialog.setMessage("识圾 V1.7.0\nBy.juggier RF103T Elmo").setTitle("关于").setPositiveButton("我知道啦", null).create();
        permissionsInfoDialog.show();
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (preference.getKey().contentEquals("show_calendar_num"))
            showCalendarNum.setSummary("预先显示" + (String)newValue + "天的日程");
        return true;
    }
}
