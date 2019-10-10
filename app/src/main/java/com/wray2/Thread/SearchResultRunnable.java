package com.wray2.Thread;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wray2.Class.HTTPRequest;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.SearchbackData;
import com.wray2.Util.ImageUtils;
import com.wray2.Util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class SearchResultRunnable implements Runnable {
    private String id;
    private String requestJson;
    private Handler handler;

    public SearchResultRunnable(String id,Handler handler){
        this.id = id;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Date date = new Date();
            String datetime = JsonUtils.getFormatDateString(date, "yyyyMMddHHmmss");
            String key = JsonUtils.md5(datetime + "K&E#AiXS%r90!Yn%");
            JSONObject pictureJsonObject;
            Bundle bundle = new Bundle();
            Message msg = new Message();
            requestJson = CreateRequestPicJson(datetime,key,id);
            HTTPRequest requester = new HTTPRequest("https://rf103t.club/rubbish/ask_for_pic.php");
            pictureJsonObject = requester.request(new ArrayList<String>()
            {{
                add("key_json_string");
            }}, new ArrayList<String>()
            {{
                add(requestJson);
            }});
            if (pictureJsonObject !=null){
                if (JsonUtils.isErrorJson(pictureJsonObject)){
                    ErrorData errorData = JsonUtils.getErrorJsonObject(pictureJsonObject);
                    bundle.putParcelable("error_data", errorData);
                    msg.what = -1;

                }else{
                    Bitmap pic = getJsonPic(pictureJsonObject);
                    bundle.putParcelable("feedback_pic",pic);
                    msg.what = 1;
                }
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getJsonPic(JSONObject pictureJsonObject) throws JSONException {
        JSONObject data = new JSONObject(pictureJsonObject.getString("data"));
        String picBase64 = data.getString("pic");
        picBase64 = picBase64.replace('-', '+').replace('_', '/').replaceAll("data:image/jpeg;base64,", "");
        Bitmap bitmap = ImageUtils.base64ToBitmap(picBase64);
        return bitmap;
    }

    private String CreateRequestPicJson(String datetime, String key, String id) throws JSONException {
        //根节点
        JSONObject root = new JSONObject();
        //头信息
        JSONObject header = new JSONObject();
        header.put("time", datetime);
        header.put("key", key);
        //数据
        JSONObject data = new JSONObject();
        data.put("id", id);
        root.put("header", header);
        root.put("data", data);
        return root.toString();
    }
}
