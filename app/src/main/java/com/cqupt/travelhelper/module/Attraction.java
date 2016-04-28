package com.cqupt.travelhelper.module;


import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Attraction extends BmobObject {
    String name;
    BmobFile picture;

    public String getName() {
        return name;
    }

    public BmobFile getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        return "Attraction{" +
                "name='" + name + '\'' +
                ", picture=" + picture +
                '}';
    }
}
