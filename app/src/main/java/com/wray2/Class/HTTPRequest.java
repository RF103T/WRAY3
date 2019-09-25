package com.wray2.Class;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HTTPRequest
{
    private String urlString = "http://localhost";
    private String feedbackJsonString = "";

    public HTTPRequest(String url)
    {
        this.urlString = url;
    }

    public JSONObject request(List<String> tags, List<String> datas) throws IOException, JSONException
    {
        //构造POST数据字符串
        StringBuilder postData = new StringBuilder("");
        int tagIndex = 0;
        final int maxIndex = tags.size() - 1;
        for (String tag : tags)
        {
            postData.append(tag);
            postData.append('=');
            postData.append(datas.get(tagIndex));
            if (tagIndex < maxIndex)
                postData.append('&');
            tagIndex++;
        }
        //发送POST请求
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content_Type", "application/x-www-form-urlencoded");
        OutputStream outputStream = connection.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.print(postData.toString());
        //接收数据
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] by = new byte[2048];
            int n;
            while ((n = inputStream.read(by)) != -1)
            {
                baos.write(by, 0, n);
            }
            feedbackJsonString = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            baos.flush();
            inputStream.close();
            baos.close();
        }
        if(!TextUtils.isEmpty(feedbackJsonString))
            return new JSONObject(feedbackJsonString);
        return null;
    }
}
