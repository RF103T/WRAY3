package com.wray2.Class;

import android.app.Person;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Alert implements Parcelable, Serializable {
    private String date;
    private String time;
    private String address;
    private String datetime;

    public Alert() {
    }

    public Alert(String date, String time, String address) {
        this.date = date;
        this.time = time;
        this.address = address;
        this.datetime = date + " " + time;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(address);
        dest.writeString(datetime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel parcel) {
            Alert alert = new Alert();
            alert.date = parcel.readString();
            alert.time = parcel.readString();
            alert.address = parcel.readString();
            alert.datetime = parcel.readString();
            return alert;
        }

        @Override
        public Alert[] newArray(int i) {
            return new Alert[i];
        }
    };
}