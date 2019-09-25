package com.wray2.Thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wray2.Class.HTTPRequest;
import com.wray2.Class.Rubbish;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.FeedbackData;
import com.wray2.Util.ImageUtils;
import com.wray2.Util.JsonUtils;


public class ResultThreadRunnable implements Runnable
{
    private Bitmap uploadBitmap;
    private String result;
    private Handler handler;

    public ResultThreadRunnable(Bitmap bitmap, Handler handler)
    {
        this.uploadBitmap = bitmap;
        this.handler = handler;
    }


    @Override
    public void run()
    {
        try
        {
            //裁剪图片
            uploadBitmap = ImageUtils.cropWithHeight(uploadBitmap, 1280);
            //创建Json数据
            String bitmapBase64 = ImageUtils.bitmapToBase64(ImageUtils.compressImage(uploadBitmap, 60));
            Date date = new Date();
            String datetime = JsonUtils.getFormatDateString(date, "yyyyMMddHHmmss");
            String key = JsonUtils.md5(datetime + "K&E#AiXS%r90!Yn%");
            JSONObject feedbackJsonObject = null;
            Bundle bundle = new Bundle();
            Message msg = new Message();
            result = uploadPicJsonStringCreate(datetime, key, bitmapBase64);
            /* todo:测试地址
             * http://192.168.1.6/test/upload_pic.php
             * http://192.168.31.129/test/upload_pic.php
             * https://rf103t.club/rubbish/upload_pic.php
             */
            //发送请求
            HTTPRequest requester = new HTTPRequest("https://rf103t.club/rubbish/upload_pic.php");
            feedbackJsonObject = requester.request(new ArrayList<String>()
            {{
                add("key_json_string");
            }}, new ArrayList<String>()
            {{
                add(result);
            }});
            if (feedbackJsonObject != null)
            {
                if (JsonUtils.isErrorJson(feedbackJsonObject))
                {
                    //如果服务器报错
                    ErrorData errorData = JsonUtils.getErrorJsonObject(feedbackJsonObject);
                    bundle.putParcelable("error_data", errorData);
                    msg.what = -1;
                }
                else
                {
                    //如果服务器返回正确结果
                    FeedbackData feedbackData = getJsonObject(feedbackJsonObject);
                    bundle.putParcelable("feedback_data", feedbackData);//bundle存入集合
                    msg.what = 1;
                }
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //创建上传图片Json
    private String uploadPicJsonStringCreate(String time, String key, String picture) throws JSONException
    {
        //根节点
        JSONObject root = new JSONObject();
        //头信息
        JSONObject header = new JSONObject();
        header.put("time", time);
        header.put("key", key);
        //数据
        JSONObject data = new JSONObject();
        data.put("pic", picture);
        root.put("header", header);
        root.put("data", data);
        return root.toString();
    }

    //利用根Json对象构建FeedbackData
    private FeedbackData getJsonObject(JSONObject feedbackJsonObj) throws JSONException
    {
        FeedbackData feedbackData;
        JSONObject header = new JSONObject(feedbackJsonObj.getString("header"));
        JSONObject data = new JSONObject(feedbackJsonObj.getString("data"));
        JSONArray resultList = data.getJSONArray("result_list");
        feedbackData = new FeedbackData(header.getString("time"), header.getString("id"), new ArrayList<Rubbish>());
        for (int i = 0; i < resultList.length(); i++)
        {
            JSONObject item = resultList.getJSONObject(i);//取出数组中的JSONObject对象（这里取出的是第1个）
            //解析Json并创建对象
            String base64 = item.getString("pic");
            //将URL安全Json转换为普通Json
            base64 = base64.replace('-', '+').replace('_', '/').replaceAll("data:image/jpeg;base64,", "");
            Bitmap bitmap = ImageUtils.base64ToBitmap(base64);
            feedbackData.addItemToResultList(new Rubbish(item.getString("name"), item.getString("real_name"), item.getInt("sort"), bitmap));
        }
        return feedbackData;
    }
}

