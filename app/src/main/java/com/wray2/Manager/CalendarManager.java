package com.wray2.Manager;

import android.content.Context;

import com.wray2.Class.Alert;
import com.wray2.Util.AlertUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        alertList.add(new Alert("NONE","NONE","ADD_NEW"));
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

    public void removeAlert(int position, Alert alert)
    {
        alertList.remove(position);
        AlertUtils.deleteAlertList(context);
    }
}
