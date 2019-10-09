package com.wray2.Thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.wray2.Class.HTTPRequest;
import com.wray2.Class.SearchResultRubbish;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.SearchbackData;
import com.wray2.Util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class RelativeSearchThreadRunnable implements Runnable{
    private String sendJson;
    private Handler handler1;
    private String keyword;
    private JSONObject search_feedbackJsonObject;

    public RelativeSearchThreadRunnable(Handler handler,String keyword){
        this.handler1 = handler;
        this.keyword = keyword;
    }

    @Override
    public void run() {
        try {
            Date date = new Date();
            String datetime = JsonUtils.getFormatDateString(date, "yyyyMMddHHmmss");
            String key = JsonUtils.md5(datetime + "K&E#AiXS%r90!Yn%");
            Bundle bundle = new Bundle();
            Message msg1 = new Message();
            sendJson = JsonStringCreate(datetime,key,keyword);
            HTTPRequest requester = new HTTPRequest("https://rf103t.club/rubbish/search.php");
            search_feedbackJsonObject = requester.request(new ArrayList<String>()
            {{
                add("key_json_string");
            }}, new ArrayList<String>()
            {{
                add(sendJson);
            }});
            if (search_feedbackJsonObject != null)
            {
                if (JsonUtils.isErrorJson(search_feedbackJsonObject))
                {
                    //如果服务器报错
                    ErrorData errorData = JsonUtils.getErrorJsonObject(search_feedbackJsonObject);
                    bundle.putParcelable("error_data", errorData);
                    msg1.what = -1;
                }
                else
                {
                    //如果服务器返回正确结果
                    SearchbackData searchbackData = getJsonObject(search_feedbackJsonObject);
                    bundle.putParcelable("search_feedback_data", searchbackData);//bundle存入集合
                    msg1.what = 1;
                }
                msg1.setData(bundle);
                handler1.sendMessage(msg1);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String JsonStringCreate(String time,String key,String keyword) throws JSONException
    {
        //根节点
        JSONObject root = new JSONObject();
        //头信息
        JSONObject header = new JSONObject();
        header.put("time", time);
        header.put("key", key);
        //数据
        JSONObject data = new JSONObject();
        data.put("keyword", keyword);
        root.put("header", header);
        root.put("data", data);
        return root.toString();
    }

    private SearchbackData getJsonObject(JSONObject feedbackJsonObj) throws JSONException
    {
        SearchbackData search_feedbackData;
        JSONObject header = new JSONObject(feedbackJsonObj.getString("header"));
        JSONObject data = new JSONObject(feedbackJsonObj.getString("data"));
        JSONArray searchresultList = data.getJSONArray("result_list");
        search_feedbackData = new SearchbackData(header.getString("time"), data.getInt("num"), new LinkedList<SearchResultRubbish>());
        for (int i = 0; i < searchresultList.length(); i++)
        {
            JSONObject item = searchresultList.getJSONObject(i);//取出数组中的JSONObject对象（这里取出的是第1个）
            search_feedbackData.addItemTosearchResultRubbishes(new SearchResultRubbish(item.getString("name"), item.getInt("sort"), item.getString("id")));
        }
        return search_feedbackData;
    }
}
