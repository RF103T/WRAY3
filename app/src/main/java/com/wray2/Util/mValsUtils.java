package com.wray2.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wray2.Class.Alert;

import java.util.LinkedList;

public class mValsUtils {
    //添加数据
//    public void addmValsTomValsList(Context context, String string){
//        SharedPreferences sp =context.getSharedPreferences("SP_mVals_List",Context.MODE_PRIVATE);//第一个参数为文件名称，第二个为模式
//        String mValsListJson = sp.getString("Number_Of_mValsList","");
//        if (mValsListJson == ""){//如果原来没有文件
//            LinkedList<String> mValsList = new LinkedList<>();
//            mValsList.add(string);
//            Gson gson = new Gson();
//            String jsonStr = gson.toJson(mValsList);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("Number_Of_mValsList",jsonStr);
//            editor.commit();
//        }else{
//            //原先已经有文件
//            Gson gson = new Gson();
//            LinkedList<String> mValsList = gson.fromJson(mValsListJson,new TypeToken<LinkedList<String>>(){}.getType());
//            if (mValsList.size()>=8&& !isRename(string,mValsList)){
//                for (int i =1; i<=7;i++){
//                    mValsList.set(mValsList.size()-i,mValsList.get(mValsList.size()-i-1));
//                }
//                mValsList.set(0,string);
//            }else if (mValsList.size()<8 && !isRename(string,mValsList)){
//                for (int i =1; i<=mValsList.size()-1;i++){
//                    mValsList.set(mValsList.size()-i,mValsList.get(mValsList.size()-i-1));
//                }
//                mValsList.set(0,string);
//            }
//            String jsonStr = gson.toJson(mValsList);
//            SharedPreferences.Editor editor = sp.edit();
//            //清空以前的文件
//            editor.remove("Number_Of_mValsList");
//            editor.putString("Number_Of_mValsList",jsonStr);
//            editor.commit();
//        }
//    }

    //读取数据
    public static LinkedList<String> readmValsList(Context context){
        LinkedList<String> mValsList = new LinkedList<>();
        SharedPreferences sp = context.getSharedPreferences("SP_mVals_List",Context.MODE_PRIVATE);
        String alertListJson = sp.getString("Number_Of_mValsList","");//取出key为第一个参数的值，并将第二个参数作为默认值
        if (alertListJson!=""){
            Gson gson = new Gson();
            mValsList = gson.fromJson(alertListJson,new TypeToken<LinkedList<String>>(){}.getType());
        }
        return mValsList;
    }

    //更新数据
    public static void upDatemValsList(Context context,String getString){
        LinkedList<String> mValsList = new LinkedList<>();
        SharedPreferences sp = context.getSharedPreferences("SP_mVals_List",Context.MODE_PRIVATE);
        String alertListJson = sp.getString("Number_Of_mValsList","");//取出key为第一个参数的值，并将第二个参数作为默认值
        if (alertListJson!=""){
            //如果已有文件存在则替换
            Gson gson = new Gson();
            mValsList = gson.fromJson(alertListJson,new TypeToken<LinkedList<String>>(){}.getType());
            if (mValsList.size()>=8&& !isRename(getString,mValsList)){
                for (int i =1; i<=7;i++){
                    mValsList.set(mValsList.size()-i,mValsList.get(mValsList.size()-i-1));
                }
                mValsList.set(0,getString);
            }else if (mValsList.size()<8 && !isRename(getString,mValsList)){
                mValsList.addFirst(getString);
            }
            String jsonStr = gson.toJson(mValsList);
            SharedPreferences.Editor editor = sp.edit();
            //清空以前的文件
            editor.remove("Number_Of_mValsList");
            editor.putString("Number_Of_mValsList",jsonStr);
            editor.commit();
        }else {//未有文件则添加至空List
            mValsList.add(getString);
            Gson gson =new Gson();
            String jsonStr = gson.toJson(mValsList);
            SharedPreferences.Editor editor = sp.edit();
            //清空原有的数据
            editor.remove("Number_Of_mValsList");
            //添加新的数据
            editor.putString("Number_Of_mValsList",jsonStr);
            editor.commit();
        }
    }

    //删除数据
    public static void deletemValsList(Context context){
        SharedPreferences sp = context.getSharedPreferences("SP_mVals_List",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("Number_Of_mValsList");
        editor.commit();
    }

    //判断重复
    public static boolean isRename(String str,LinkedList<String> mVals){
        for (int i = 0; i < mVals.size();i++){
            if (str.equals(mVals.get(i))){
                return true;
            }
        }
        return false;
    }
}
