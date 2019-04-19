package com.ykj.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2015/6/9.
 */
public class WhCalendarBean implements Parcelable{

    private long beginDate;
    private long endDate;
    private String type;
    private String beginFormat;
    private String endFormat;

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeginFormat() {
        return beginFormat;
    }

    public void setBeginFormat(String beginFormat) {
        this.beginFormat = beginFormat;
    }

    public String getEndFormat() {
        return endFormat;
    }

    public void setEndFormat(String endFormat) {
        this.endFormat = endFormat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(beginDate);
        dest.writeLong(endDate);
        dest.writeString(type);
        dest.writeString(beginFormat);
        dest.writeString(endFormat);
    }

    public static final Creator<WhCalendarBean> CREATOR = new Creator<WhCalendarBean>() {
        @Override
        public WhCalendarBean createFromParcel(Parcel source) {
            WhCalendarBean whCalendarBean = new WhCalendarBean();
            whCalendarBean.beginDate = source.readLong(); //读取beginData
            whCalendarBean.endDate = source.readLong(); //读取endDate
            whCalendarBean.type = source.readString(); //读取type
            whCalendarBean.beginFormat = source.readString(); //读取beginFormat
            whCalendarBean.endFormat = source.readString(); //读取endFormat
            return whCalendarBean;
        }

        @Override
        public WhCalendarBean[] newArray(int size) {
            return new WhCalendarBean[size];
        }
    };
}
