package com.pig.jimmy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad Silin on 19/03/16.
 *
 * A model class for the sound response (to be used with gson)
 */
public class Sound implements Parcelable {
    @SerializedName("sound_id")
    private int soundID;
    @SerializedName("sound_name")
    private String soundName;
    @SerializedName("playable")
    private int isPlayable;

    public Sound() {
    }

    public Sound(int soundID, String soundName, int isPlayable) {
        this.soundID = soundID;
        this.soundName = soundName;
        this.isPlayable = isPlayable;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public int getSoundID() {
        return soundID;
    }

    public void setSoundID(int soundID) {
        this.soundID = soundID;
    }

    public int getPlayable() {
        return isPlayable;
    }

    public void setPlayable(int playable) {
        this.isPlayable = playable;
    }

    protected Sound(Parcel in) {
        soundID = in.readInt();
        soundName = in.readString();
        isPlayable = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(soundID);
        dest.writeString(soundName);
        dest.writeInt(isPlayable);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Sound> CREATOR = new Parcelable.Creator<Sound>() {
        @Override
        public Sound createFromParcel(Parcel in) {
            return new Sound(in);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };
}
