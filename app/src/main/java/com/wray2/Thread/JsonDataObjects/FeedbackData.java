package com.wray2.Thread.JsonDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.wray2.Class.Rubbish;

import java.util.LinkedList;
import java.util.List;

public class FeedbackData implements Parcelable
{
    private String time;
    private String id;
    private LinkedList<Rubbish> resultList;

    public static final Creator<FeedbackData> CREATOR = new Creator<FeedbackData>()
    {
        @Override
        public FeedbackData createFromParcel(Parcel source)
        {
            return new FeedbackData(source);
        }

        @Override
        public FeedbackData[] newArray(int size)
        {
            return new FeedbackData[size];
        }
    };


    public FeedbackData(String time, String id, LinkedList<Rubbish> resultList)
    {
        this.time = time;
        this.id = id;
        this.resultList = resultList;
    }

    protected FeedbackData(Parcel in)
    {
        this.id = in.readString();
        this.time = in.readString();
        in.readTypedList(resultList, Rubbish.CREATOR);
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public LinkedList<Rubbish> getResultList()
    {
        return resultList;
    }

    public void setResultList(LinkedList<Rubbish> resultList)
    {
        this.resultList = resultList;
    }

    public void addItemToResultList(Rubbish resultData)
    {
        resultList.add(resultData);
    }

    public void destroy()
    {
        for (Rubbish rubbish :resultList)
            rubbish.getRubbishPicture().recycle();
        resultList.clear();
        resultList = null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(time);
        dest.writeTypedList(resultList);
    }
}
