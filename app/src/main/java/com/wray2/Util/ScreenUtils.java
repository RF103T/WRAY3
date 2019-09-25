package com.wray2.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ScreenUtils
{
    public static Context context;

    public static int screenWidth;
    public static int screenHeight;

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue)
    {
        final float scale = getDisplayMetrics(context).density;
        return (int)(pxValue / scale + 0.5f);
    }

    private static DisplayMetrics mDm;

    private static DisplayMetrics getDisplayMetrics(Context context)
    {
        if (mDm == null)
        {
            if (context != null)
            {
                mDm = context.getResources().getDisplayMetrics();
            }
            return mDm;
        }
        return mDm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px
     */
    public static int dp2Px(int dp)
    {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context)) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp(字体) 的单位 转成为 px
     */
    public static int sp2Px(int spValue)
    {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int)(spValue * fontScale + 0.5f);
    }
}
