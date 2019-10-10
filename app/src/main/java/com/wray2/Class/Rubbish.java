package com.wray2.Class;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Rubbish implements Parcelable
{
    private String rubbishId = "";
    private String rubbishName = "";
    private String rubbishClassifyName = "";
    private String rubbishSortName = "";
    private int rubbishSortNum;
    private Bitmap rubbishPicture;

    private static String[] RUBBISH_CLASS = new String[]{"可回收垃圾", "干垃圾", "湿垃圾", "有害垃圾"};

    public static final Creator<Rubbish> CREATOR = new Creator<Rubbish>()
    {
        @Override
        public Rubbish createFromParcel(Parcel in)
        {
            return new Rubbish(in);
        }

        @Override
        public Rubbish[] newArray(int size)
        {
            return new Rubbish[size];
        }
    };

    public Rubbish(String rubbishName, String rubbishId, int rubbishSortNum)
    {
        this.rubbishName = rubbishName;
        this.rubbishId = rubbishId;
        this.rubbishSortNum = rubbishSortNum;
        this.rubbishSortName = RUBBISH_CLASS[rubbishSortNum];
    }

    public Rubbish(String rubbishName, String rubbishClassifyName, int rubbishSortNum, Bitmap rubbishPicture)
    {
        this.rubbishName = rubbishName;
        this.rubbishClassifyName = rubbishClassifyName;
        this.rubbishSortNum = rubbishSortNum;
        this.rubbishSortName = RUBBISH_CLASS[rubbishSortNum];
        this.rubbishPicture = rubbishPicture;
    }

    protected Rubbish(Parcel in)
    {
        this.rubbishId = in.readString();
        this.rubbishName = in.readString();
        this.rubbishClassifyName = in.readString();
        this.rubbishSortName = in.readString();
        this.rubbishSortNum = in.readInt();
        this.rubbishPicture = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public String getRubbishName()
    {
        return rubbishName;
    }

    public void setRubbishName(String rubbishName)
    {
        this.rubbishName = rubbishName;
    }

    public String getRubbishClassifyName()
    {
        return rubbishClassifyName;
    }

    public void setRubbishClassifyName(String rubbishClassifyName)
    {
        this.rubbishClassifyName = rubbishClassifyName;
    }

    public String getRubbishSortName()
    {
        return rubbishSortName;
    }

    public void setRubbishSortName(String rubbishSortName)
    {
        this.rubbishSortName = rubbishSortName;
    }

    public int getRubbishSortNum()
    {
        return rubbishSortNum;
    }

    public void setRubbishSortNum(int rubbishSortNum)
    {
        this.rubbishSortNum = rubbishSortNum;
    }

    public Bitmap getRubbishPicture()
    {
        return rubbishPicture;
    }

    public void setRubbishPicture(Bitmap rubbishPicture)
    {
        this.rubbishPicture = rubbishPicture;
    }

    public String getRubbishId() {
        return rubbishId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(rubbishId);
        dest.writeString(rubbishName);
        dest.writeString(rubbishClassifyName);
        dest.writeString(rubbishSortName);
        dest.writeInt(rubbishSortNum);
        dest.writeParcelable(rubbishPicture, 0);
    }
}