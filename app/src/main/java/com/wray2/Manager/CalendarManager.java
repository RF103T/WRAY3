package com.wray2.Manager;

import android.content.Context;

import com.wray2.Class.Alert;
import com.wray2.Util.AlertUtils;

import java.util.LinkedList;

public class CalendarManager
{
    private LinkedList<Alert> alertList;

    private Context context;

    public static CalendarManager calendarManager;

    public static void initCalendarManager(Context context)
    {
        CalendarManager.calendarManager = new CalendarManager(context);
    }

    private CalendarManager(Context context)
    {
        alertList = new LinkedList<Alert>();
        this.context = context.getApplicationContext();
        alertList = AlertUtils.readAlertList(context);
        alertList.add(new Alert("ADD_NEW","NONE","NONE"));
    }

    public LinkedList<Alert> getRealAlertList()
    {
        return alertList;
    }

    public LinkedList<Alert> getAlertList()
    {
        LinkedList<Alert> res = new LinkedList<Alert>(alertList);
        res.removeLast();
        return res;
    }

    public void addAlert(Alert alert)
    {
        alertList.addFirst(alert);
        AlertUtils.addAlterToAlterList(context, alert);
    }

    public void setAlert(int position, Alert alert)
    {
        alertList.set(position, alert);
        AlertUtils.upDateAlertList(context, position, alert);
    }

    public void removeallAlert(int position)
    {
        alertList.clear();
        AlertUtils.deleteallAlertList(context);
    }

    public void removeAlert(int position)
    {
        alertList.remove(position);
        AlertUtils.deleteAlertList(context,position);
    }

    public void moveAlert(int fromPosition,int toPosition){
        if (fromPosition < toPosition){
            //分别把中间所有的item的位置重新交换
            for (int i = fromPosition; i < toPosition; i++){
                Alert alert = new Alert();
                alert = alertList.get(i+1);
                alertList.set(i+1,alertList.get(i));
                alertList.set(i,alert);
            }
        }else {
            for (int i = fromPosition; i > toPosition; i--){
                Alert alert = new Alert();
                alert = alertList.get(i);
                alertList.set(i,alertList.get(i-1));
                alertList.set(i-1,alert);
            }
        }
        AlertUtils.updateAllAlertList(context,CalendarManager.calendarManager.getAlertList());
    }
}
