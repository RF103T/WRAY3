package com.wray2.Manager;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Util.AlertUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class CalendarManager
{
    private LinkedList<Alert> alertList = new LinkedList<Alert>();

    /* 用于保存一周内发生的日程，按照日期排序，同一天按照发生时间排序
     * 周一 周二 周三 周四 周五 周六 周日
     *   2   3    4    5    6    7   1
     */
    private ArrayList<LinkedList<Alert>> willComingAlertList;

    {
        willComingAlertList = new ArrayList<LinkedList<Alert>>();
        for (int i = 0; i < 7; i++)
            willComingAlertList.add(i, new LinkedList<Alert>());
    }

    private Context context;
    private FragmentsActivity activity;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);

    public static CalendarManager calendarManager;

    public static void initCalendarManager(Context context)
    {
        CalendarManager.calendarManager = new CalendarManager(context);
    }

    private CalendarManager(Context context)
    {
        if (alertList.isEmpty())
        {
            this.context = context.getApplicationContext();
            this.activity = (FragmentsActivity) context;
            alertList = AlertUtils.readAlertList(context);
            for(Alert alert:alertList)
                addAlertToSortList(alert);
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

    public LinkedList<Alert> getWillComingAlertList(int alertNum) throws ParseException
    {
        LinkedList<Alert> linkedList = new LinkedList<Alert>();
        int i = alertNum,dayIndex;
        boolean loopFlag = true;
        //获得当天星期和日期
        Calendar now = Calendar.getInstance();
        int weeksDayIndex = dayIndex =  now.get(Calendar.DAY_OF_WEEK) - 1;
        Date time = format.parse(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
        while (loopFlag)
        {
            LinkedList<Alert> dayAlertList = willComingAlertList.get(dayIndex);
            for (Alert alert : dayAlertList)
            {
                Date temp = format.parse(alert.getStarttime());
                if (dayIndex != weeksDayIndex || temp.getTime() >= time.getTime())
                {
                    int[] weeksDay = {0,0,0,0,0,0,0};
                    weeksDay[dayIndex] = 1;
                    linkedList.add(new Alert(alert.getStarttime(),alert.getEndtime(),weeksDay,alert.getSorts()));
                    i--;
                }
                if (i < 1)
                {
                    loopFlag = false;
                    break;
                }
            }
            if (dayIndex == 6)
                dayIndex = 0;
            else
                dayIndex++;
            if(dayIndex == weeksDayIndex)
                loopFlag = false;
        }
        return linkedList;
    }

    public void addAlert(Alert alert)
    {
        alertList.addFirst(alert);
        addAlertToSortList(alert);
        updateService();
        //AlertUtils.addAlterToAlterList(context, alert);
    }

    private void addAlertToSortList(Alert alert)
    {
        for (int i = 0; i < 7; i++)
        {
            if (alert.getDates()[i] == 1)
            {
                int index = 0;
                for (Alert a : willComingAlertList.get(i))
                {
                    try
                    {
                        Date alertTime = format.parse(alert.getStarttime());
                        Date tempTime = format.parse(a.getStarttime());
                        if (alertTime.getTime() < tempTime.getTime())
                            break;
                        index++;
                    }
                    catch (ParseException ignore)
                    {

                    }
                }
                willComingAlertList.get(i).add(index, alert);
            }
        }
    }

    public void setAlert(int position, Alert alert)
    {
        Alert oldData = alertList.get(position);
        alertList.set(position, alert);
        for (int i = 0; i < 7; i++)
        {
            if (oldData.getDates()[i] == 1)
                willComingAlertList.get(i).remove(oldData);
            if (alert.getDates()[i] == 1)
                addAlertToSortList(alert);
        }
        updateService();
        //AlertUtils.upDateAlertList(context, position, alert);
    }

    public void removeAllAlert()
    {
        alertList.clear();
        for (LinkedList<Alert> linkedList : willComingAlertList)
            linkedList.clear();
        updateService();
        //AlertUtils.deleteallAlertList(context);
    }

    public void removeAlert(int position)
    {
        Alert oldData = alertList.get(position);
        alertList.remove(position);
        for (int i = 0; i < 7; i++)
            if (oldData.getDates()[i] == 1)
                willComingAlertList.get(i).remove(oldData);
        updateService();
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
        alertList.add(position,alert);
        addAlertToSortList(alert);
        updateService();
//        int length = alertList.size();
//        if (position > length / 2)
//        {
//            alertList.addLast(alertList.getLast());
//            for (int i = length - 1; i > position; i--)
//            {
//                Alert alert1 = new Alert();
//                alert1 = alertList.get(i);
//                alertList.set(i, alertList.get(i - 1));
//            }
//            alertList.set(position, alert);
//        }
//        else
//        {
//            alertList.addFirst(alertList.getFirst());
//            for (int i = 1; i < position; i++)
//            {
//                Alert alert1 = new Alert();
//                alert1 = alertList.get(i);
//                alertList.set(i, alertList.get(i + 1));
//            }
//            alertList.set(position, alert);
//        }
        //AlertUtils.updateAllAlertList(context, CalendarManager.calendarManager.getAlertList());
    }

    public void updateService(){
        activity.bindServiceConnection();
        if (activity.getServiceConnection().isConnected())
        {
            activity.getServiceConnection().getBinder().updateData();
            activity.unBindServiceConnection();
        }
    }
}
