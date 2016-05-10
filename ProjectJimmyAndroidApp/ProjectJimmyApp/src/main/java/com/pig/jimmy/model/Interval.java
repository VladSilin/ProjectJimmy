package com.pig.jimmy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad Silin on 25/03/16.
 *
 * A model class for the sound playback interval response (to be used with gson)
 */
public class Interval implements Parcelable{
    @SerializedName("hours")
    private int hours;
    @SerializedName("minutes")
    private int minutes;

    public Interval() {
    }

    public Interval(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int playable) {
        this.minutes = minutes;
    }

    protected Interval(Parcel in) {
        hours = in.readInt();
        minutes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hours);
        dest.writeInt(minutes);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Interval> CREATOR = new Parcelable.Creator<Interval>() {
        @Override
        public Interval createFromParcel(Parcel in) {
            return new Interval(in);
        }

        @Override
        public Interval[] newArray(int size) {
            return new Interval[size];
        }
    };
}
