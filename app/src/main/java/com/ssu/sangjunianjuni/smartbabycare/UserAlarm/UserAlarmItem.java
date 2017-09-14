package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

/**
 * Created by kang on 2017-07-07.
 */

public class UserAlarmItem {

    private String[] mData;

    public UserAlarmItem(String[] data) {
        mData = data;
    }

    public UserAlarmItem(String title, String time, String daily, String onoff){
        mData=new String[4];
        mData[0]=title;
        mData[1]=time;
        mData[2]=daily;
        mData[3]=onoff;
    }

    public String[] getData() {
        return mData;
    }

    public String getData(int index) {
        return mData[index];
    }

    public void setData(String[] data) {
        mData = data;
    }

    public void setData(int index, String data){mData[index]=data;}
}
