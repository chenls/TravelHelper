package com.cqupt.travelhelper.module;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.datatype.BmobFile;

public class Comment implements Parcelable {
    String myId, name, price;
    BmobFile picture;

    public Comment(String myId, String name, String price, BmobFile picture) {
        this.myId = myId;
        this.name = name;
        this.price = price;
        this.picture = picture;
    }

    protected Comment(Parcel in) {
        picture = (BmobFile) in.readSerializable();
        myId = in.readString();
        name = in.readString();
        price = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getMyId() {
        return myId;
    }


    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public BmobFile getPicture() {
        return picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(picture);
        dest.writeString(myId);
        dest.writeString(name);
        dest.writeString(price);
    }
}
