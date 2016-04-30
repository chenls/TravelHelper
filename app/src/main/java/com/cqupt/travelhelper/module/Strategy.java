package com.cqupt.travelhelper.module;


import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Strategy extends BmobObject implements Parcelable {
    String myId, name, description, take_time, address, traffic, price;
    BmobFile picture;

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getTake_time() {
        return take_time;
    }

    public String getAddress() {
        return address;
    }

    public String getTraffic() {
        return traffic;
    }

    public String getPrice() {
        return price;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    protected Strategy(Parcel in) {
        picture = (BmobFile) in.readSerializable();
        myId = in.readString();
        name = in.readString();
        description = in.readString();
        take_time = in.readString();
        address = in.readString();
        traffic = in.readString();
        price = in.readString();
    }

    public static final Creator<Strategy> CREATOR = new Creator<Strategy>() {
        @Override
        public Strategy createFromParcel(Parcel in) {
            return new Strategy(in);
        }

        @Override
        public Strategy[] newArray(int size) {
            return new Strategy[size];
        }
    };

    @Override
    public String toString() {
        return "景点：" + name + "\n\n" +
                "价格：" + price + "\n\n" +
                "用时参考：" + take_time + "\n\n" +
                "地址：" + address + "\n\n" +
                "介绍：" + description + "\n\n" +
                "交通：" + traffic;
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
        dest.writeString(description);
        dest.writeString(take_time);
        dest.writeString(address);
        dest.writeString(traffic);
        dest.writeString(price);
    }
}
