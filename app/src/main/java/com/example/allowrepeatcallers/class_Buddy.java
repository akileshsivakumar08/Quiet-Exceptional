package com.example.allowrepeatcallers;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class class_Buddy implements Parcelable {


    private String Buddy_PhNo;
    private String Buddy_Message;
    private String Buddy_name;
    private boolean favourite;

    class_Buddy(String name, String Phno, String message) {
        Buddy_name = name;
        Buddy_PhNo = Phno;
        Buddy_Message = message;
        favourite=false;
    }
    class_Buddy(Parcel in) {
        Buddy_name = in.readString();
        Buddy_PhNo = in.readString();
        Buddy_Message = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            favourite=in.readBoolean();
        }
    }

    public String getBuddy_PhNo() {
        return Buddy_PhNo;
    }

    public String getBuddy_Message() {
        return Buddy_Message;
    }

    public String getBuddy_name() {
        return Buddy_name;
    }

    public void setBuddy_PhNo(String buddy_PhNo) {
        Buddy_PhNo = buddy_PhNo;
    }

    public void setBuddy_Message(String buddy_Message) {
        Buddy_Message = buddy_Message;
    }

    public void setBuddy_name(String buddy_name) {
        this.Buddy_name = buddy_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getBuddy_name());
        dest.writeString(getBuddy_PhNo());
        dest.writeString(getBuddy_Message());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isFavourite());
        }

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public class_Buddy createFromParcel(Parcel in) {
            return new class_Buddy(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
