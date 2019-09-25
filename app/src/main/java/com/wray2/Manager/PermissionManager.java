package com.wray2.Manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.wray2.CameraActivity;
import com.wray2.Class.ROMChecker;
import com.wray2.Interface.CancelDialogCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager
{
    //Activity的跳转Flag
    //应用设置
    public static final int PERMISSION_SETTING_FLAG = 1;

    //相机权限
    public static final int CAMERA_PERMISSION = 1;
    //存储空间权限
    public static final int EXTERNAL_STORAGE_PERMISSION = 2;

    public static PermissionManager permissionManager;

    private Map<String, Boolean> permissions;

    private Activity activity;

    public PermissionManager(Activity activity)
    {
        this.activity = activity;
        permissions = new HashMap<String, Boolean>();
    }

    public PermissionManager(Activity activity, String permissionName)
    {
        this(activity);
        permissions.put(permissionName, checkPermission(permissionName));
    }

    public PermissionManager(Activity activity, List<String> permissionNames)
    {
        this(activity);
        for (String name : permissionNames)
            permissions.put(name, checkPermission(name));
    }

    public boolean checkPermission(String permissionName)
    {
        return (PermissionChecker.checkSelfPermission(activity, permissionName) == PermissionChecker.PERMISSION_GRANTED);
    }

    public void updatePermission(String permissionName)
    {
        permissions.replace(permissionName, checkPermission(permissionName));
    }

    public boolean getPermissionState(String permissionName)
    {
        Boolean hasPermission = permissions.getOrDefault(permissionName, null);
        if (hasPermission != null)
            return hasPermission;
        else
            return false;
    }

    public boolean askForPermission(String permissionName, int requestCode, CancelDialogCallback cancelDialogCallback)
    {
        Boolean hasPermission = permissions.getOrDefault(permissionName, null);
        if (hasPermission != null)
        {
            if (!hasPermission)
            {
                AlertDialog.Builder permissionsRefuseDialog = new AlertDialog.Builder(activity);
                permissionsRefuseDialog.setTitle("缺少权限")
                        .setCancelable(false)
                        .setMessage("我们申请的权限都是为了使APP的主要功能正常进行，请您放心授权，否则APP将无法正常使用。")
                        .setPositiveButton("确定", (dialog, which) ->
                        {
                            //是否被拒绝
                            if (activity.shouldShowRequestPermissionRationale(permissionName))
                                ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
                            else
                            {
                                AlertDialog.Builder permissionsUserAllowDialog = new AlertDialog.Builder(activity);
                                permissionsUserAllowDialog.setTitle("需要您的帮助")
                                        .setCancelable(false)
                                        .setMessage("由于您勾选了“不再提示”，需要您去设置界面手动授予权限。")
                                        .setPositiveButton("前往授权", (dialog12, which12) ->
                                        {
                                            //跳转到应用授权界面
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri1 = Uri.fromParts("package", activity.getPackageName(), null);
                                            intent.setData(uri1);
                                            activity.startActivityForResult(intent, PERMISSION_SETTING_FLAG);
                                        })
                                        .create();
                                permissionsUserAllowDialog.show();
                            }
                        })
                        .setNegativeButton("取消", cancelDialogCallback::onCancelDialog)
                        .create();
                permissionsRefuseDialog.show();
                return true;
            }
        }
        else
        {
            boolean temp = checkPermission(permissionName);
            permissions.put(permissionName, temp);
            if (!temp)
                return askForPermission(permissionName, requestCode, cancelDialogCallback);
        }
        return false;
    }
}
