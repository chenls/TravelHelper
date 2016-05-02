package com.cqupt.travelhelper.module;


import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Travels extends BmobObject implements Parcelable {
    String myId, description;
    BmobFile picture;
    MyUser myUser;

    public Travels(String description, BmobFile picture, MyUser myUser) {
        this.description = description;
        this.picture = picture;
        this.myUser = myUser;
    }

    protected Travels(Parcel in) {
        myId = in.readString();
        picture = (BmobFile) in.readSerializable();
        description = in.readString();
        myUser = (MyUser) in.readSerializable();
    }

    public static final Creator<Travels> CREATOR = new Creator<Travels>() {
        @Override
        public Travels createFromParcel(Parcel in) {
            return new Travels(in);
        }

        @Override
        public Travels[] newArray(int size) {
            return new Travels[size];
        }
    };


    @Override
    public String toString() {
        return "描述：" + description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myId);
        dest.writeSerializable(picture);
        dest.writeString(description);
        dest.writeSerializable(myUser);
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public MyUser getMyUser() {
        return myUser;
    }

}
