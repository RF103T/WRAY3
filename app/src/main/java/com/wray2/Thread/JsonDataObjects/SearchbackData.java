package com.wray2.Thread.JsonDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.wray2.Class.SearchResultRubbish;

import java.util.LinkedList;

public class SearchbackData implements Parcelable {
    private String time;
    private int num;
    private LinkedList<SearchResultRubbish> searchResultRubbishes;

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


    public SearchbackData(String time, int num, LinkedList<SearchResultRubbish> searchResultRubbishes)
    {
        this.time = time;
        this.num = num;
        this.searchResultRubbishes = searchResultRubbishes;
    }

    protected SearchbackData(Parcel in)
    {
        this.num = in.readInt();
        this.time = in.readString();
        in.readTypedList(searchResultRubbishes, SearchResultRubbish.CREATOR);
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

    public LinkedList<SearchResultRubbish> getResultList()
    {
        return searchResultRubbishes;
    }

    public void setsearchResultRubbishes(LinkedList<SearchResultRubbish> searchResultRubbishes)
    {
        this.searchResultRubbishes = searchResultRubbishes;
    }

    public void addItemTosearchResultRubbishes(SearchResultRubbish searchResultRubbish)
    {
        searchResultRubbishes.add(searchResultRubbish);
    }

    public void destroy()
    {
        searchResultRubbishes.clear();
        searchResultRubbishes = null;
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
        dest.writeTypedList(searchResultRubbishes);
    }
}
