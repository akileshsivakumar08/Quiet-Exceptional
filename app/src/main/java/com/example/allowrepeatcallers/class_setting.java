package com.example.allowrepeatcallers;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class class_setting implements Parcelable {


    private String extraPing;
    private Integer volume;
    private boolean ringInDND;
    private String ringtone_uri;

    class_setting(String extraPing_inp, Integer volume_in, boolean ringInDND_in,String ringtone_in) {
        extraPing = extraPing_inp;
        volume = volume_in;
        ringInDND = ringInDND_in;
        ringtone_uri=ringtone_in;
    }
    class_setting(Parcel in) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ringInDND = in.readBoolean();
        }
        volume = in.readInt();
        extraPing = in.readString();
        ringtone_uri=in.readString();


    }

    public String getExtraPing() {
        return extraPing;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setExtraPing(String extraPing) {
        this.extraPing = extraPing;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public void setRingInDND(boolean ringInDND) {
        this.ringInDND = ringInDND;
    }
    public void setRingtone_uri(String uri_ip){
        ringtone_uri=uri_ip;
    }

    public boolean isRingInDND() {
        return ringInDND;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isRingInDND());
        }
        dest.writeInt(getVolume());
        dest.writeString(getExtraPing());
        dest.writeString(getRingtoneUri());

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public class_setting createFromParcel(Parcel in) {
            return new class_setting(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public String getRingtoneUri() {
        return ringtone_uri;
    }
}
