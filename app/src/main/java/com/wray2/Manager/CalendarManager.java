package com.wray2.Manager;

import android.content.Context;

import com.wray2.Class.Alert;
import com.wray2.Util.AlertUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CalendarManager
{
    private static LinkedList<Alert> alertList = new LinkedList<Alert>();

    //用于保存一周内发生的日程，按照日期排序，同一天按照发生时间排序
    private ArrayList<Alert> willComingAlertQueue;

    private Context context;

    public static CalendarManager calendarManager;

    public static void initCalendarManager(Context context)
    {
        CalendarManager.calendarManager = new CalendarManager(context);
    }

    private CalendarManager(Context context)
    {
        if (alertList.isEmpty()){
            //alertList = new LinkedList<Alert>();
            this.context = context.getApplicationContext();
            alertList = AlertUtils.readAlertList(context);
            alertList.add(new Alert("ADD_NEW", "NONE", "NONE"));
        }
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
        //AlertUtils.addAlterToAlterList(context, alert);
    }

    public void setAlert(int position, Alert alert)
    {
        alertList.set(position, alert);
        //AlertUtils.upDateAlertList(context, position, alert);
    }

    public void removeallAlert(int position)
    {
        alertList.clear();
        //AlertUtils.deleteallAlertList(context);
    }

    public void removeAlert(int position)
    {
        alertList.remove(position);
        //AlertUtils.deleteAlertList(context, position);
    }

    public void moveAlert(int fromPosition, int toPosition)
    {
        Alert alert = alertList.get(fromPosition);
        alertList.remove(fromPosition);
        alertList.add(toPosition, alert);
        //AlertUtils.updateAllAlertList(context, CalendarManager.calendarManager.getAlertList());
    }

    public void reAddAlert(int position, Alert alert)
    {
        int length = alertList.size();
        if (position > length / 2)
        {
            alertList.addLast(alertList.getLast());
            for (int i = length - 1; i > position; i--)
            {
                Alert alert1 = new Alert();
                alert1 = alertList.get(i);
                alertList.set(i, alertList.get(i - 1));
            }
            alertList.set(position, alert);
        }
        else
        {
            alertList.addFirst(alertList.getFirst());
            for (int i = 1; i < position; i++)
            {
                Alert alert1 = new Alert();
                alert1 = alertList.get(i);
                alertList.set(i, alertList.get(i + 1));
            }
            alertList.set(position, alert);
        }
        //AlertUtils.updateAllAlertList(context, CalendarManager.calendarManager.getAlertList());
    }
}
