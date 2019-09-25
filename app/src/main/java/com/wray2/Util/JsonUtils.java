package com.wray2.Util;

import android.text.TextUtils;

import com.wray2.Thread.JsonDataObjects.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonUtils
{
    /**
     * 判断是否服务器返回错误信息
     */
    public static boolean isErrorJson(JSONObject feedbackJsonObj)
    {
        String error = feedbackJsonObj.optString("error_data", "");
        return !TextUtils.isEmpty(error);
    }

    /**
     * 获取字符串的MD5
     */
    public static String md5(String string)
    {
        if (TextUtils.isEmpty(string))
        {
            return "";
        }
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1)
                {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Date转格式化String
     */
    public static String getFormatDateString(Date date, String format)
    {
        if (format == null || format.isEmpty())
        {
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return formatter.format(date);
    }

    /**
     * 利用根Json对象构建ErrorData
     */
    public static ErrorData getErrorJsonObject(JSONObject feedbackJsonObj) throws JSONException
    {
        JSONObject header = new JSONObject(feedbackJsonObj.getString("header"));
        JSONObject errorData = new JSONObject(feedbackJsonObj.getString("error_data"));
        String time = header.getString("time");
        int error_code = errorData.getInt("error_code");
        String error_detail = errorData.getString("error_detail");
        return new ErrorData(time, error_code, error_detail);
    }

}
