package com.wray2.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.wray2.Class.GlobalValue;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.PermissionManager;
import com.wray2.R;
import com.wray2.ResultActivity;
import com.wray2.Util.ImageUtils;
import com.wray2.Util.ScreenUtils;
import com.wray2.Util.ThemeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment
{
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentsActivity activity;

    private CameraView cameraView;

    private ImageView flashLightImageView;
    private ImageView choosePhotoImageView;
    private ImageView takePhotoImageView;

    private final Handler handlerInitAfterTime = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            initCamera();
            return false;
        }
    });

    private Thread thread;

    public CameraFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2)
    {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), ThemeUtils.getThemeId(activity));
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_camera, container, false);
        //布局创建
        cameraView = (CameraView)view.findViewById(R.id.camera_view);
        flashLightImageView = (ImageView)view.findViewById(R.id.flash_light_image_view);
        choosePhotoImageView = (ImageView)view.findViewById(R.id.choose_photo_image_view);
        takePhotoImageView = (ImageView)view.findViewById(R.id.camera_shooting_image_view);

        flashLightImageView.setOnClickListener(v ->
        {
            if (cameraView.isTorchOn())
            {
                cameraView.enableTorch(false);
                flashLightImageView.setImageResource(R.drawable.ic_flash_off);
            }
            else
            {
                cameraView.enableTorch(true);
                flashLightImageView.setImageResource(R.drawable.ic_flash_on);
            }
        });

        choosePhotoImageView.setOnClickListener(v ->
        {
            //存储空间权限
            if (!PermissionManager.permissionManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE"))
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, PermissionManager.EXTERNAL_STORAGE_PERMISSION);
            else
                openSysAlbum();
        });

        takePhotoImageView.setOnClickListener(v ->
                cameraView.takePicture(command -> new Thread(command).start(), new ImageCapture.OnImageCapturedListener()
                {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees)
                    {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        GlobalValue.savedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                        Log.d("bitmap", GlobalValue.savedBitmap.getHeight() + " " + GlobalValue.savedBitmap.getWidth());

                        Intent intent = new Intent(activity, ResultActivity.class);
                        intent.putExtra("isCameraPhoto", true);
                        activity.startActivityForResult(intent, RESULT_ACTIVITY_WITH_CAMERA_FLAG);

                        super.onCaptureSuccess(image, rotationDegrees);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause)
                    {
                        super.onError(imageCaptureError, message, cause);
                        //todo:错误处理
                    }
                }));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume()
    {
        activity.hideSystemBar();
        //通过添加加载延迟，让相机在界面划过时不会加载
        thread = new Thread(new CalTimeRunnable(handlerInitAfterTime, 600));
        thread.start();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        if (thread.isAlive())
            thread.interrupt();
        if (cameraView.isTorchOn())
        {
            cameraView.enableTorch(false);
            flashLightImageView.setImageResource(R.drawable.ic_flash_off);
        }
        activity.showSystemBar();
        super.onPause();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        activity = (FragmentsActivity)context;
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //从相册选择了照片
        if (requestCode == ALBUM_RESULT_FLAG)
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
                File dataFile = new File(activity.getExternalCacheDir(), "cache_cropped.jpg");
                FileInputStream stream = new FileInputStream(dataFile);
                GlobalValue.savedBitmap = BitmapFactory.decodeStream(stream);
                Intent intent = new Intent(activity, ResultActivity.class);
                intent.putExtra("isCameraPhoto", false);
                startActivityForResult(intent, RESULT_ACTIVITY_WITH_ALBUM_FLAG);
            }
            catch (IOException ex)
            {
                //todo:错误处理，文件未找到
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initCamera()
    {
        //拍照权限
        if (PermissionManager.permissionManager.checkPermission("android.permission.CAMERA"))
        {
            cameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
            cameraView.setCameraLensFacing(CameraX.LensFacing.BACK);
            cameraView.setPinchToZoomEnabled(false);
            cameraView.setScaleType(CameraView.ScaleType.CENTER_CROP);
            cameraView.bindToLifecycle((LifecycleOwner)this);
        }
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
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500 * ((float)ScreenUtils.screenHeight / ScreenUtils.screenWidth));
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", ScreenUtils.screenWidth);
        intent.putExtra("aspectY", ScreenUtils.screenHeight);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        String picPath = ImageUtils.getPath(activity, uri);
        Uri realUri = FileProvider.getUriForFile(activity, "com.wray2", new File(picPath));

        intent.setDataAndType(realUri, "image/*");

        File dataFile = new File(activity.getExternalCacheDir(), "cache_cropped.jpg");
        Uri reaultUri = Uri.fromFile(dataFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, reaultUri);

        startActivityForResult(intent, CROP_FLAG);
    }

    class CalTimeRunnable implements Runnable
    {
        private Handler handler;

        private int times;

        public CalTimeRunnable(Handler handler, int times)
        {
            this.handler = handler;
            this.times = times;
        }

        @Override
        public void run()
        {
            handler.sendEmptyMessageDelayed(1, times);
        }

    }
}
