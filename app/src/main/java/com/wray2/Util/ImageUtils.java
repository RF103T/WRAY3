package com.wray2.Util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

public class ImageUtils
{

    /**
     * 获取缓存文件夹的相对路径
     */
    public static String getDiskCacheDir(Context ctx)
    {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
        {
            cachePath = ctx.getExternalCacheDir().getPath();
        }
        else
        {
            cachePath = ctx.getCacheDir().getPath();
        }
        return cachePath;
    }


    /**
     * bitmap转file(原图转换)
     */
    public static String bitmapToFile(Context context, Bitmap bitmap)
    {
        String sdPath = getDiskCacheDir(context);
        String name = new DateFormat().format("yyyyMMddhhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String picPath = sdPath + "/" + name;
        File outImage = new File(picPath);
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(outImage);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        //返回一个图片路径
        return picPath;
    }

    /**
     * bitmap转file(带压缩转换 可以自己设定 默认100kb) 上传服务器的时候使用
     */
    public static String bitmapToFileWhithCompress(Context context, Bitmap bitmap, int kb) throws IOException
    {
        String sdPath = getDiskCacheDir(context);
        String name = new DateFormat().format("yyyyMMddhhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String picPath = sdPath + "/" + name;
        File outImage = new File(picPath);
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(outImage);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        //调用了下面封装好的压缩方法返回已经压缩的bitmap 然后再调用cmpress 输出流把bitmap转走 100不再压缩 因为已经压缩好了
        compressImage(bitmap, kb).compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        //返回一个压缩图片绝对路径
        return picPath;
    }

    /**
     * file转bitmap(进行压缩,放置内存泄漏)
     */
    public static Bitmap fileToBitmap(String imagePath, int kb)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            FileInputStream fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, options);
            //将bitmap进行压缩防止内存泄漏
            bitmap = compressImage(bitmap, kb);
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    //无回调有返回值的压缩方法
    public static Bitmap compressImage(Bitmap image, int quality) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        Bitmap result = BitmapFactory.decodeStream(isBm, null, null);
        isBm.close();
        return result;
    }

    //有回调有返回值的压缩方法
    public static Bitmap compressBitmap(Bitmap image, int kb, CompressCallback callback)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > kb)
        { //循环判断如果压缩后图片是否大于设定的kb,大于继续压缩
            baos.reset();//重置baos即清空baos

            if (options <= 0)
            {
                break;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        callback.finish(bitmap);
        return bitmap;
    }

    //获取View上的Bitmap
    public static Bitmap getViewBitmap(View view)
    {

        if (view == null)
        {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        if (bitmap == null)
        {
            return null;
        }
        return bitmap;
    }

    /**
     * 通过文件路径转Base64
     */
    public static String fileToBase64(String path)
    {
        // decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        // base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;
    }

    /**
     * 通过Bitmap转Base64
     */
    public static String bitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        // base64 encode
        byte[] encode = Base64.encode(bytes, Base64.URL_SAFE);
        return new String(encode);
    }

    /**
     * Base64转成Bitmap
     */
    public static Bitmap base64ToBitmap(String base64)
    {
        byte[] decode = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    /**
     * 清理缓存文件夹
     */
    public static void clearCache(Context ctx, String cachePath)
    {
        File file = new File(cachePath);
        File[] childFile = file.listFiles();
        if (childFile == null || childFile.length == 0)
        {
            return;
        }

        for (File f : childFile)
        {
            f.delete(); // 循环删除子文件
        }
    }

    /**
     * 保持图片宽高比不变，根据指定height缩放图片
     *
     * @param bitmap
     * @param height
     * @return
     */
    public static Bitmap cropWithHeight(Bitmap bitmap, int height)
    {
        Bitmap tempBitmap = bitmap;
        int h = tempBitmap.getHeight();
        int w = tempBitmap.getWidth();
        float scaleHeight = (float)height / h;
        float scaleWidth = ((float)height * ((float)w / h)) / w;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);//使用后乘
        tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, w, h, matrix, false);
        return tempBitmap;
    }

    //回调接口
    public interface CompressCallback
    {
        void finish(Bitmap bitmap);
    }

    //获取文件名
    public static String getFileName(String pathandname)
    {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1)
        {
            return pathandname.substring(start + 1, end);
        }
        else
        {
            return null;
        }
    }

    /*****************相册URI解析判断4.4系统****************/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri)
    {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs)
    {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
        {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}