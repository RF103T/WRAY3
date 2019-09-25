package com.wray2.Thread.JsonDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

public class ErrorData implements Parcelable
{
    private String time;
    private int errorCode;
    private String errorDetail;

    public static final Creator<ErrorData> CREATOR = new Creator<ErrorData>()
    {
        @Override
        public ErrorData createFromParcel(Parcel in)
        {
            return new ErrorData(in);
        }

        @Override
        public ErrorData[] newArray(int size)
        {
            return new ErrorData[size];
        }
    };

    public ErrorData(String time, int errorCode, String errorDetail)
    {
        this.time = time;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    protected ErrorData(Parcel in)
    {
        time = in.readString();
        errorCode = in.readInt();
        errorDetail = in.readString();
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorDetail()
    {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail)
    {
        this.errorDetail = errorDetail;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(time);
        dest.writeInt(errorCode);
        dest.writeString(errorDetail);
    }
}
