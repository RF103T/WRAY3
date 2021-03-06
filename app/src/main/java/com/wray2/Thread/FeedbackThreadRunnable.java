package com.wray2.Thread;

import com.wray2.Class.HTTPRequest;
import com.wray2.Util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

public class FeedbackThreadRunnable implements Runnable
{
    private String id;
    private int otherResult;
    private String value;
    private String result;

    public FeedbackThreadRunnable(String id, int otherResult, String value)
    {
        this.id = id;
        this.otherResult = otherResult;
        this.value = value;
    }

    public void run()
    {
        try
        {
            //创建Json数据
            Date date = new Date();
            String datetime = JsonUtils.getFormatDateString(date, "yyyyMMddHHmmss");
            result = feedbackJsonStringCreate(datetime, id, otherResult, value).toString();
            /* todo:测试地址
             * http://192.168.1.6/test/feedback.php
             * http://192.168.31.129/test/feedback.php
             * https://rf103t.club/rubbish/feedback.php
             */
//            byte[] _byte = {0x68,0x74,0x74,0x70,0x73,0x3a,0x2f,0x2f,0x72,0x66,0x31,0x30,0x33,0x74,0x2e,
//                    0x63,0x6c,0x75,0x62,0x2f,0x72,0x75,0x62,0x62,0x69,0x73,0x68,0x2f,0x66,0x65,0x65,0x64,
//                    0x62,0x61,0x63,0x6b,0x2e,0x70,0x68,0x70};
//            String str = new String(_byte);
            HTTPRequest requester = new HTTPRequest("https://rf103t.club/rubbish/feedback.php");
            JSONObject feedbackJsonObject = requester.request(new ArrayList<String>()
            {{
                add("feedback_json_string");
            }}, new ArrayList<String>()
            {{
                add(result);
            }});
            if (feedbackJsonObject != null && JsonUtils.isErrorJson(feedbackJsonObject))
            {
                //todo:错误处理
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

    //创建反馈Json
    private static JSONObject feedbackJsonStringCreate(String time, String id, int otherResult, String value) throws JSONException
    {
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("time", time);
        header.put("id", id);
        root.put("header", header);
        JSONObject data = new JSONObject();
        data.put("other_result", otherResult);
        data.put("value", value);
        root.put("data", data);
        return root;
    }
}
