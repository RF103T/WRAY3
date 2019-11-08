package com.wray2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.SwitchPreference;

import com.wray2.CustomComponent.FragmentTab.MyTabBar;
import com.wray2.Fragment.CalendarFragment;
import com.wray2.Fragment.CameraFragment;
import com.wray2.Fragment.HomepageFragment;
import com.wray2.Fragment.SearchFragment;
import com.wray2.Fragment.SettingFragment;
import com.wray2.Manager.CalendarManager;
import com.wray2.Manager.NotificationChannelsManager;
import com.wray2.Manager.PermissionManager;
import com.wray2.Service.NotificationDataUpdateService;
import com.wray2.Thread.SearchResultRunnable;
import com.wray2.Util.AlertUtils;
import com.wray2.Util.ThemeUtils;

public class FragmentsActivity extends FragmentActivity
        implements HomepageFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener
{

    private MyTabBar tabBar;

    //只用来获取WindowToken，隐藏输入法而已
    private ImageView backgroundImageView;

    private boolean isDealShortCutsAction = false;

    private NotificationServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getThemeId(this));
        setContentView(R.layout.activity_fragments);

        //初始化日程管理器
        CalendarManager.initCalendarManager(this);

        backgroundImageView = (ImageView)findViewById(R.id.backgroundImageView);

        PermissionManager.permissionManager = new PermissionManager(this, "android.permission.CAMERA");

        //权限申请提示
        if (!PermissionManager.permissionManager.getPermissionState("android.permission.CAMERA"))
        {
            AlertDialog.Builder permissionsInfoDialog = new AlertDialog.Builder(FragmentsActivity.this);
            permissionsInfoDialog.setView(R.layout.permission_info_dialog)
                    .setTitle("我们可能需要一些权限")
                    .setPositiveButton("我知道啦", (dialog, which) -> ActivityCompat.requestPermissions(FragmentsActivity.this, new String[]{"android.permission.CAMERA"}, PermissionManager.CAMERA_PERMISSION))
                    .setCancelable(false)
                    .create();
            permissionsInfoDialog.show();
        }

        //注册通知通道
        NotificationChannelsManager.createAllNotificationChannels(this);

        tabBar = new MyTabBar(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("show_calendar", false))
        {
            this.startForegroundService(new Intent(this, NotificationDataUpdateService.class));
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bindServiceConnection();
        if (serviceConnection.isConnected())
        {
            serviceConnection.getBinder().updateData();
            unBindServiceConnection();
        }
        showSystemBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!tabBar.isTabBarInit())
            tabBar.initTabBar();
        if (!isDealShortCutsAction)
        {
            Intent intent = getIntent();
            String actionFlag = intent.getAction();
            if (actionFlag != null)
            {
                switch (actionFlag)
                {
                    case "intent.action.shortcuts.camera":
                        tabBar.fakeDragToPosition(2);
                        break;
                    case "intent.action.shortcuts.search":
                        tabBar.fakeDragToPosition(1);
                        break;
                    case "intent.action.shortcuts.calendar":
                        tabBar.fakeDragToPosition(3);
                        break;
                }
            }
            isDealShortCutsAction = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void onBackPressed()
    {
        if (tabBar.getNowSelectedFragmentIndex() > 0)
            tabBar.fakeClick(0);
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionManager.PERMISSION_SETTING_FLAG)
        {
            if (!PermissionManager.permissionManager.checkPermission("android.permission.CAMERA"))
                PermissionManager.permissionManager.askForPermission("android.permission.CAMERA", PermissionManager.CAMERA_PERMISSION, (dialog, which) -> FragmentsActivity.this.finish());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //摄像头
        if (requestCode == PermissionManager.CAMERA_PERMISSION)
        {
            //用户拒绝
            if (permissions[0].equals("android.permission.CAMERA") && grantResults[0] == -1)
                PermissionManager.permissionManager.askForPermission("android.permission.CAMERA", PermissionManager.CAMERA_PERMISSION, (dialog, which) -> FragmentsActivity.this.finish());
            else
                PermissionManager.permissionManager.updatePermission("android.permission.CAMERA");
        }
        if (requestCode == PermissionManager.EXTERNAL_STORAGE_PERMISSION)
        {
            //用户拒绝
            if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE") && grantResults[0] == -1)
                PermissionManager.permissionManager.askForPermission("android.permission.READ_EXTERNAL_STORAGE", PermissionManager.EXTERNAL_STORAGE_PERMISSION, (dialog, which) -> dialog.cancel());
            else if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE") && grantResults[0] == 0)
                PermissionManager.permissionManager.updatePermission("android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    public void hideIME()
    {
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(backgroundImageView.getWindowToken(), 0);
    }

    public NotificationServiceConnection getServiceConnection()
    {
        return serviceConnection;
    }

    public void setTabBarPosition(int position)
    {
        //tabBar.setCurrentPosition(position);
        tabBar.fakeDragToPosition(position);
    }

    public void showSystemBar()
    {
        View decorView = this.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean("dark_mode", false))
            option |=  View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(option);
        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public void hideSystemBar()
    {
        View decorView = this.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        AlertUtils.updateAllAlertList(this.getApplicationContext(), CalendarManager.calendarManager.getAlertList());
    }

    public void bindServiceConnection()
    {
        serviceConnection = new NotificationServiceConnection();
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (NotificationDataUpdateService.isServiceRunning)
        {
            Intent intent = new Intent(FragmentsActivity.this, NotificationDataUpdateService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    public void unBindServiceConnection()
    {
        if (serviceConnection.isConnected())
            unbindService(serviceConnection);
    }

    public class NotificationServiceConnection implements ServiceConnection
    {
        private boolean isConnected = false;
        private NotificationDataUpdateService.NotificationDataBinder binder;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            binder = (NotificationDataUpdateService.NotificationDataBinder)service;
            isConnected = true;
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
}
