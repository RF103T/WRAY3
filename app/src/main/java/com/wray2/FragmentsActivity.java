package com.wray2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.wray2.CustomComponent.FragmentTab.MyTabBar;
import com.wray2.Fragment.CalendarFragment;
import com.wray2.Fragment.CameraFragment;
import com.wray2.Fragment.HomepageFragment;
import com.wray2.Fragment.SearchFragment;
import com.wray2.Fragment.SettingFragment;
import com.wray2.Interface.CancelDialogCallback;
import com.wray2.Manager.PermissionManager;

public class FragmentsActivity extends FragmentActivity
        implements HomepageFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener
{

    private MyTabBar tabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

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

        tabBar = new MyTabBar(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        showSystemBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!tabBar.isTabBarInit())
            tabBar.initTabBar();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PermissionManager.PERMISSION_SETTING_FLAG)
        {
            if(!PermissionManager.permissionManager.checkPermission("android.permission.CAMERA"))
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
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
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
}
