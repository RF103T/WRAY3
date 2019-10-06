package com.wray2.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wray2.Class.Alert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlertUtils
{

    //添加数据
    public static void addAlterToAlterList(Context context, Alert getAlert)
    {
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);//第一个参数为文件名称，第二个为模式
        String alertListJson = sp.getString("Number_Of_AlertList", "");
        if (alertListJson == null || alertListJson.equals(""))
        {//如果原来没有文件
            LinkedList<Alert> alertList = new LinkedList<>();
            alertList.add(getAlert);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(alertList);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Number_Of_AlertList", jsonStr);
            editor.apply();
        }
        else
        {
            //原先已经有文件
            Gson gson = new Gson();
            LinkedList<Alert> alertList = gson.fromJson(alertListJson, new TypeToken<LinkedList<Alert>>(){}.getType());
            alertList.addFirst(getAlert);
            String jsonStr = gson.toJson(alertList);
            SharedPreferences.Editor editor = sp.edit();
            //清空以前的文件
            editor.remove("Number_Of_AlertList");
            editor.putString("Number_Of_AlertList", jsonStr);
            editor.apply();
        }
    }

    //读取数据
    public static LinkedList<Alert> readAlertList(Context context)
    {
        LinkedList<Alert> alertList = new LinkedList<>();
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);
        String alertListJson = sp.getString("Number_Of_AlertList", "");//取出key为第一个参数的值，并将第二个参数作为默认值
        if (alertListJson != null && !alertListJson.equals(""))
        {
            Gson gson = new Gson();
            alertList = gson.fromJson(alertListJson, new TypeToken<LinkedList<Alert>>(){}.getType());
        }
        return alertList;
    }

    //更新数据
    public static void upDateAlertList(Context context, int position, Alert getAlert)
    {
        LinkedList<Alert> alertList = new LinkedList<>();
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);
        String alertListJson = sp.getString("Number_Of_AlertList", "");//取出key为第一个参数的值，并将第二个参数作为默认值
        Gson gson = new Gson();
        String jsonStr;
        if (alertListJson != null && !alertListJson.equals(""))
        {
            //如果已有文件存在则替换
            alertList = gson.fromJson(alertListJson, new TypeToken<LinkedList<Alert>>(){}.getType());
            alertList.set(position, getAlert);
        }
        else
        {
            //未有文件则添加空List
            alertList.add(getAlert);
        }
        jsonStr = gson.toJson(alertList);
        SharedPreferences.Editor editor = sp.edit();
        //清空原有的数据
        editor.remove("Number_Of_AlertList");
        //添加新的数据
        editor.putString("Number_Of_AlertList", jsonStr);
        editor.apply();
    }

    public static void updateAllAlertList(Context context,LinkedList<Alert> alerts){
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(alerts);
        editor.remove("Number_Of_AlertList");
        editor.putString("Number_Of_AlertList", jsonStr);
        editor.apply();
    }

    //删除数据
    public static void deleteallAlertList(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("Number_Of_AlertList");
        editor.apply();

    }

    public static void deleteAlertList(Context context,int position)
    {
        LinkedList<Alert> alertList;
        SharedPreferences sp = context.getSharedPreferences("SP_Alert_List", Context.MODE_PRIVATE);
        String alertListJson = sp.getString("Number_Of_AlertList", "");
        Gson gson = new Gson();
        alertList = gson.fromJson(alertListJson, new TypeToken<LinkedList<Alert>>(){}.getType());
        alertList.remove(position);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("Number_Of_AlertList");
        String jsonStr = gson.toJson(alertList);
        editor.putString("Number_Of_AlertList", jsonStr);
        editor.apply();
    }
}
