package com.tobeitech.seouledunews.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LocalUser1 on 2017-04-06.
 */

public class ParcelableUrlInfo implements Parcelable {

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private int type;
    private String url;

    public ParcelableUrlInfo(int type, String url) {
        this.type = type;
        this.url = url;
    }

    protected ParcelableUrlInfo(Parcel in) {
        type = in.readInt();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(url);
    }

    public static final Creator<ParcelableUrlInfo> CREATOR = new Creator<ParcelableUrlInfo>() {
        @Override
        public ParcelableUrlInfo createFromParcel(Parcel source) {
            return new ParcelableUrlInfo(source);
        }

        @Override
        public ParcelableUrlInfo[] newArray(int size) {
            return new ParcelableUrlInfo[size];
        }
    };
}
