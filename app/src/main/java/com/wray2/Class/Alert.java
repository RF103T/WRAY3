package com.wray2.Class;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

import java.io.Serializable;

public class Alert implements Parcelable, Serializable
{
    private String date;//日期
    private String starttime;//开始收垃圾的时间
    private String endtime;//结束收垃圾的时间
    private String time;//仅用于在首页日程列表中显示完整时间段
    private String sort;//显示垃圾种类的文本信息
    private int[] dates;//用于记录选择的日期
    private int[] sorts;//记录选择的垃圾种类
    private static String[] datesname = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private static String[] sortssname = new String[]{"可回收垃圾", "干垃圾", "湿垃圾", "有害垃圾"};
    private int position;//记录从CalerdarList里点进修改的日程位置

    public Alert()
    {
    }

    public Alert(String date, String starttime, String endtime)
    {
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.time = starttime + "-" + endtime;
    }

    public Alert(String starttime, String endtime, int[] dates, int[] sorts)
    {
        this.starttime = starttime;
        this.endtime = endtime;
        this.time = starttime + "-" + endtime;
        this.dates = dates;
        this.sorts = sorts;
        this.date = getDates(dates, "周");
        this.sort = getSorts(sorts, "");
    }

    public String getEndtime()
    {
        return endtime;
    }

    public String getStarttime()
    {
        return starttime;
    }

    public void setEndtime(String endtime)
    {
        this.endtime = endtime;
    }

    public void setStarttime(String starttime)
    {
        this.starttime = starttime;
    }

    public String getDate()
    {
        return date;
    }

    public String getTime()
    {
        return time;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    public String getSort()
    {
        return sort;
    }

    public int[] getDates()
    {
        return dates;
    }

    public int[] getSorts()
    {
        return sorts;
    }

    public void setDates(int[] dates)
    {
        this.dates = dates;
    }

    public void setSorts(int[] sorts)
    {
        this.sorts = sorts;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(date);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(time);
        dest.writeString(sort);
        dest.writeIntArray(sorts);
        dest.writeIntArray(dates);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>()
    {
        @Override
        public Alert createFromParcel(Parcel parcel)
        {
            Alert alert = new Alert();
            alert.date = parcel.readString();
            alert.starttime = parcel.readString();
            alert.endtime = parcel.readString();
            alert.time = parcel.readString();
            alert.sort = parcel.readString();
            alert.sorts = parcel.createIntArray();
            alert.dates = parcel.createIntArray();
            return alert;
        }

        @Override
        public Alert[] newArray(int i)
        {
            return new Alert[i];
        }
    };

    public String getDates(int[] dates, String s)
    {
        StringBuilder sBuilder = new StringBuilder(s);
        byte b = 0;
        for (int i = 0; i < 7; i++)
        {
            if (dates[i] == 1)
            {
                sBuilder.append(datesname[i]).append(" ");
                b |= (0xFF << i) & (0xFF >> (7 - i));
            }
        }
        if (b == 0x7F)
            return "每天";
        else
            return sBuilder.toString();
    }

    public String getSorts(int[] sorts, String s)
    {
        StringBuilder sBuilder = new StringBuilder(s);
        for (int i = 0; i < 4; i++)
            if (sorts[i] == 1)
                sBuilder.append(sortssname[i]).append(" ");
        return sBuilder.toString();
    }

}