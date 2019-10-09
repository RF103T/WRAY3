package com.wray2.Thread.JsonDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.wray2.Class.Rubbish;

import java.util.LinkedList;

public class SearchbackData implements Parcelable {
    private String time;
    private int num;
    private LinkedList<Rubbish> rubbishes;

    public static final Creator<SearchbackData> CREATOR = new Creator<SearchbackData>()
    {
        @Override
        public SearchbackData createFromParcel(Parcel source)
        {
            return new SearchbackData(source);
        }

        @Override
        public SearchbackData[] newArray(int size)
        {
            return new SearchbackData[size];
        }
    };


    public SearchbackData(String time, int num, LinkedList<Rubbish> rubbishes)
    {
        this.time = time;
        this.num = num;
        this.rubbishes = rubbishes;
    }

    protected SearchbackData(Parcel in)
    {
        this.num = in.readInt();
        this.time = in.readString();
        in.readTypedList(rubbishes, Rubbish.CREATOR);
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public int getnum()
    {
        return num;
    }

    public void setnum(int num)
    {
        this.num = num;
    }

    public LinkedList<Rubbish> getResultList()
    {
        return rubbishes;
    }

    public void setsearchResultRubbishes(LinkedList<Rubbish> rubbishes)
    {
        this.rubbishes = rubbishes;
    }

    public void addItemToRubbishes(Rubbish rubbish)
    {
        rubbishes.add(rubbish);
    }

    public void destroy()
    {
        rubbishes.clear();
        rubbishes = null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(num);
        dest.writeString(time);
        dest.writeTypedList(rubbishes);
    }
}
