package com.wray2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.wray2.Class.GlobalValue;
import com.wray2.Interface.CancelDialogCallback;
import com.wray2.Util.ImageUtils;
import com.wray2.Class.ROMChecker;
import com.wray2.Util.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CameraActivity extends AppCompatActivity
{
    //Activity的跳转Flag
    //应用设置
    public static final int PERMISSION_SETTING_FLAG = 1;
    //系统相册
    public static final int ALBUM_RESULT_FLAG = 2;
    //裁剪
    public static final int CROP_FLAG = 3;
    //结果界面
    public static final int RESULT_ACTIVITY_WITH_CAMERA_FLAG = 4;//从相机拍摄的图片
    public static final int RESULT_ACTIVITY_WITH_ALBUM_FLAG = 5;//从相册选择的图片

    //Activity的返回Flag
    //结果界面
    public static final int RESULT_BACK_FLAG = 3;

    //相机权限
    public static final int CAMERA_PERMISSION = 1;
    //存储空间权限
    public static final int EXTERNAL_STORAGE_PERMISSION = 2;

    //private CameraHelper cameraHelper = null;
    //private TextureView cameraPreviewTextureView;

    private CameraView previewCameraView;

    private ImageView flashLightImageView;
    private ImageView choosePhotoImageView;
    private ImageView takePhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //初始化ScreenUtils的一些参数
        ScreenUtils.context = this.getApplicationContext();
        //获得屏幕分辨率
        Point point = new Point();
        CameraActivity.this.getWindowManager().getDefaultDisplay().getRealSize(point);
        ScreenUtils.screenWidth = point.x;
        ScreenUtils.screenHeight = point.y;

        //cameraPreviewTextureView = (TextureView)findViewById(R.id.camera_view);
        flashLightImageView = (ImageView)findViewById(R.id.flash_light_image_view);
        choosePhotoImageView = (ImageView)findViewById(R.id.choose_photo_image_view);
        takePhotoImageView = (ImageView)findViewById(R.id.camera_shooting_image_view);

        flashLightImageView.setOnClickListener(v ->
        {
            if (previewCameraView.getFlash() == FlashMode.ON)
            {
                previewCameraView.setFlash(FlashMode.OFF);
                flashLightImageView.setImageResource(R.drawable.ic_flash_off);
            }
            else
            {
                previewCameraView.setFlash(FlashMode.ON);
                flashLightImageView.setImageResource(R.drawable.ic_flash_on);
            }
        });

        choosePhotoImageView.setOnClickListener(v ->
        {
            //处理权限
            //存储空间
            if (!checkPermission("android.permission.READ_EXTERNAL_STORAGE"))
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, EXTERNAL_STORAGE_PERMISSION);
            else
                openSysAlbum();
        });

        takePhotoImageView.setOnClickListener(v ->
        {
            previewCameraView.takePicture(new ImageCapture.OnImageCapturedListener(){
                @Override
                public void onCaptureSuccess(ImageProxy image, int rotationDegrees)
                {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    GlobalValue.savedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                    Log.d("bitmap", GlobalValue.savedBitmap.getHeight() + " " + GlobalValue.savedBitmap.getWidth());

                    Intent intent = new Intent(CameraActivity.this, ResultActivity.class);
                    intent.putExtra("isCameraPhoto", true);
                    CameraActivity.this.startActivityForResult(intent, RESULT_ACTIVITY_WITH_CAMERA_FLAG);

                    super.onCaptureSuccess(image, rotationDegrees);
                }

                @Override
                public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause)
                {
                    super.onError(imageCaptureError, message, cause);
                    //todo:错误处理
                }
            });
        });

        //处理权限
        //摄像头
        if (!checkPermission("android.permission.CAMERA"))
            getPermission();
        else
            initCamera();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //全屏,隐藏系统状态栏和虚拟按键
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //关闭闪光灯和相机
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //摄像头
        if (requestCode == CAMERA_PERMISSION)
        {
            //用户拒绝
            if (permissions[0].equals("android.permission.CAMERA") && grantResults[0] == -1)
                askPermission("android.permission.CAMERA", (dialog, which) -> CameraActivity.this.getPackageName(), CAMERA_PERMISSION);
            else if (permissions[0].equals("android.permission.CAMERA") && grantResults[0] == 0)
                initCamera();
        }
        //存储空间
        if (requestCode == EXTERNAL_STORAGE_PERMISSION)
        {
            //用户拒绝
            if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE") && grantResults[0] == -1)
                askPermission("android.permission.READ_EXTERNAL_STORAGE", (dialog, which) -> dialog.cancel(), EXTERNAL_STORAGE_PERMISSION);
            else if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE") && grantResults[0] == 0)
                openSysAlbum();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //从应用权限设置界面返回的时候，如果没有权限则继续申请
        if (requestCode == PERMISSION_SETTING_FLAG)
        {
            //摄像头
            if (!checkPermission("android.permission.CAMERA"))
                getPermission();
            else
                initCamera();
            //存储空间
            if (checkPermission("android.permission.READ_EXTERNAL_STORAGE"))
                openSysAlbum();
        }
        //从相册选择了照片
        else if (requestCode == ALBUM_RESULT_FLAG)
        {
            if (data != null)
            {
                Uri uri = data.getData();
                if (uri != null)
                    cropPhoto(uri);
            }
        }
        //完成图片裁切
        else if (requestCode == CROP_FLAG)
        {
            try
            {
                File dataFile = new File(getExternalCacheDir(), "cache_cropped.jpg");
                FileInputStream stream = new FileInputStream(dataFile);
                GlobalValue.savedBitmap = BitmapFactory.decodeStream(stream);
                Intent intent = new Intent(CameraActivity.this, ResultActivity.class);
                intent.putExtra("isCameraPhoto", false);
                CameraActivity.this.startActivityForResult(intent, RESULT_ACTIVITY_WITH_ALBUM_FLAG);
            }
            catch (IOException ex)
            {
                //todo:错误处理，文件未找到
            }
        }
    }

    private void askPermission(String permissionName, CancelDialogCallback cancelDialogCallback, int requestCode)
    {
        AlertDialog.Builder permissionsRefuseDialog = new AlertDialog.Builder(CameraActivity.this);
        permissionsRefuseDialog.setTitle("缺少权限")
                .setCancelable(false)
                .setMessage("我们申请的权限都是为了使APP的主要功能正常进行，请您放心授权，否则APP将无法正常使用。")
                .setPositiveButton("确定", (dialog, which) ->
                {
                    //是否被永久拒绝
                    if (shouldShowRequestPermissionRationale(permissionName))
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{permissionName}, requestCode);
                    else
                    {
                        AlertDialog.Builder permissionsUserAllowDialog = new AlertDialog.Builder(CameraActivity.this);
                        permissionsUserAllowDialog.setTitle("需要您的帮助")
                                .setCancelable(false)
                                .setMessage("由于您勾选了“不再提示”，需要您去设置界面手动授权存储空间权限。")
                                .setPositiveButton("前往授权", (dialog12, which12) ->
                                {
                                    //跳转到应用授权界面
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri1 = Uri.fromParts("package", CameraActivity.this.getPackageName(), null);
                                    intent.setData(uri1);
                                    startActivityForResult(intent, PERMISSION_SETTING_FLAG);
                                })
                                .create();
                        permissionsUserAllowDialog.show();
                    }
                })
                .setNegativeButton("取消", cancelDialogCallback::onCancelDialog)
                .create();
        permissionsRefuseDialog.show();
    }

    /**
     * 检查权限获取情况
     *
     * @param permissionName 要检测的权限名称
     * @return 需要的权限是否都获取到了
     */
    private boolean checkPermission(String permissionName)
    {
        //MIUI跟其他安卓系统判断权限的方法不一样
        if (ROMChecker.isMIUI())
            return (PermissionChecker.checkPermission(CameraActivity.this, permissionName, Process.myPid(), Process.myUid(), CameraActivity.this.getPackageName()) == PermissionChecker.PERMISSION_GRANTED);
        else
            return (PermissionChecker.checkSelfPermission(CameraActivity.this, permissionName) == PermissionChecker.PERMISSION_GRANTED);
    }

    /**
     * 弹出对话框解释权限作用，并尝试申请
     */
    private void getPermission()
    {
        AlertDialog.Builder permissionsInfoDialog = new AlertDialog.Builder(CameraActivity.this);
        permissionsInfoDialog.setView(R.layout.permission_info_dialog)
                .setTitle("我们需要权限")
                .setPositiveButton("确定", (dialog, which) ->
                {
                    //todo:请求程序运行必要权限
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{"android.permission.CAMERA"}, CAMERA_PERMISSION);
                })
                .setCancelable(false)
                .create();
        permissionsInfoDialog.show();
    }

    /**
     * 初始化摄像头帮助类
     */
    private void initCamera()
    {
        //todo:旧相机
        //if (cameraHelper == null)
        //cameraHelper = new CameraHelper(cameraPreviewTextureView, this);
        previewCameraView = (CameraView)findViewById(R.id.camera_view);
        previewCameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
        previewCameraView.setCameraLensFacing(CameraX.LensFacing.BACK);
        previewCameraView.setPinchToZoomEnabled(false);
        previewCameraView.setScaleType(CameraView.ScaleType.CENTER_CROP);
        previewCameraView.bindToLifecycle((LifecycleOwner)this);
    }

    /**
     * 调用系统相册
     */
    private void openSysAlbum()
    {
        Intent albumIntent = new Intent();
        albumIntent.setAction(Intent.ACTION_GET_CONTENT);
        albumIntent.setType("image/*");
        startActivityForResult(albumIntent, ALBUM_RESULT_FLAG);
    }

    /**
     * 调用系统裁剪功能
     *
     * @param uri 要裁剪的图片资源链接
     */
    private void cropPhoto(Uri uri)
    {
        //获得屏幕分辨率
        Point point = new Point();
        CameraActivity.this.getWindowManager().getDefaultDisplay().getRealSize(point);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500 * ((float)ScreenUtils.screenHeight / ScreenUtils.screenWidth));
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", ScreenUtils.screenWidth);
        intent.putExtra("aspectY", ScreenUtils.screenHeight);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        String picPath = ImageUtils.getPath(CameraActivity.this, uri);
        Uri realUri = FileProvider.getUriForFile(CameraActivity.this, "com.wray", new File(picPath));

        intent.setDataAndType(realUri, "image/*");

        File dataFile = new File(getExternalCacheDir(), "cache_cropped.jpg");
        Uri reaultUri = Uri.fromFile(dataFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, reaultUri);

        startActivityForResult(intent, CROP_FLAG);
    }
}
