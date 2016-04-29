package com.cqupt.travelhelper.module;


import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Attraction extends BmobObject implements Parcelable {
    String myId, name, mAbstract, star, description, open_time, price;
    BmobFile picture;

    protected Attraction(Parcel in) {
        myId = in.readString();
        picture = (BmobFile) in.readSerializable();
        name = in.readString();
        mAbstract = in.readString();
        star = in.readString();
        description = in.readString();
        open_time = in.readString();
        price = in.readString();
    }

    public static final Creator<Attraction> CREATOR = new Creator<Attraction>() {
        @Override
        public Attraction createFromParcel(Parcel in) {
            return new Attraction(in);
        }

        @Override
        public Attraction[] newArray(int size) {
            return new Attraction[size];
        }
    };

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getName() {
        return name;
    }

    public String getmAbstract() {
        return mAbstract;
    }

    public String getStar() {
        return star;
    }

    public String getDescription() {
        return description;
    }

    public String getOpen_time() {
        return open_time;
    }

    public String getPrice() {
        return price;
    }

    public BmobFile getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        return "景点：" + name + "\n\n" +
                "价格：" + price + "\n\n" +
                "开放时间：" + open_time + "\n\n" +
                "简介：" + mAbstract + "\n\n" +
                "描述：" + description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myId);
        dest.writeSerializable(picture);
        dest.writeString(name);
        dest.writeString(mAbstract);
        dest.writeString(star);
        dest.writeString(description);
        dest.writeString(open_time);
        dest.writeString(price);
    }
}
