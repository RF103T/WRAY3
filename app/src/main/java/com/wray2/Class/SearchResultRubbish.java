package com.wray2.Class;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class SearchResultRubbish implements Parcelable{
    private String rubbishName;
    private String rubbishSortName;
    private int rubbishSortNum;
    private String rubbishPictureId;

    private static String[] RUBBISH_CLASS = new String[]{"可回收垃圾", "干垃圾", "湿垃圾", "有害垃圾"};

    public static final Parcelable.Creator<SearchResultRubbish> CREATOR = new Parcelable.Creator<SearchResultRubbish>()
    {
        @Override
        public SearchResultRubbish createFromParcel(Parcel in)
        {
            return new SearchResultRubbish(in);
        }

        @Override
        public SearchResultRubbish[] newArray(int size)
        {
            return new SearchResultRubbish[size];
        }
    };

    public SearchResultRubbish(String rubbishName, int rubbishSortNum,String rubbishPictureId)
    {
        this.rubbishName = rubbishName;
        this.rubbishSortNum = rubbishSortNum;
        this.rubbishPictureId = rubbishPictureId;
        this.rubbishSortName = RUBBISH_CLASS[rubbishSortNum];
    }

    protected SearchResultRubbish(Parcel in)
    {
        this.rubbishName = in.readString();
        this.rubbishSortName = in.readString();
        this.rubbishSortNum = in.readInt();
        this.rubbishPictureId = in.readString();
    }

    public String getRubbishName()
    {
        return rubbishName;
    }

    public void setRubbishName(String rubbishName)
    {
        this.rubbishName = rubbishName;
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

    public String getRubbishPictureId() {
        return rubbishPictureId;
    }

    public void setRubbishPictureId(String rubbishPictureId) {
        this.rubbishPictureId = rubbishPictureId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(rubbishName);
        dest.writeString(rubbishSortName);
        dest.writeInt(rubbishSortNum);
        dest.writeString(rubbishPictureId);
    }
}
