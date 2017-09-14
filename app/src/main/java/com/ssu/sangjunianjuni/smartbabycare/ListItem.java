package com.ssu.sangjunianjuni.smartbabycare;

/**
 * Created by yoseong on 2017-05-26.
 * MySQL에서 불러온 데이터 저장시 이용하는 클래스
 */

public class ListItem {
    private String[] mData;

    public ListItem(String[] data) {
        mData = data;
    }

    public ListItem(String USER_ID) {
        mData = new String[1];

        mData[0] = USER_ID;
    }

    public ListItem(String USER_ID, String PASSWORD) {
        mData = new String[2];

        mData[0] = USER_ID;
        mData[1] = PASSWORD;
    }

    public ListItem(String RegisterNumber, String Title, String Author, String Date, String Context) {
        mData = new String[5];

        mData[0] = RegisterNumber;
        mData[1] = Title;
        mData[2] = Author;
        mData[3] = Date;
        mData[4] = Context;
    }

    public ListItem(String RegisterNumber, String Title, String Author, String Date, String Context, String PHOTO_URI) {
        mData = new String[6];

        mData[0] = RegisterNumber;
        mData[1] = Title;
        mData[2] = Author;
        mData[3] = Date;
        mData[4] = Context;
        mData[5] = PHOTO_URI;
    }

    public ListItem(String USER_ID, String HEIGHT, String WEIGHT, String MEAL) {
        mData = new String[4];

        mData[0] = USER_ID;
        mData[1] = HEIGHT;
        mData[2] = WEIGHT;
        mData[3] = MEAL;
    }

    public ListItem(String title, String time, String daily){
        mData=new String[3];
        mData[0]=title;
        mData[1]=time;
        mData[2]=daily;
    }

    public ListItem(String USER_ID, String NAME, String MONTH, String HEIGHT, String WEIGHT, String SEX, String MEAL) {
        mData = new String[7];

        mData[0] = USER_ID;
        mData[1] = NAME;
        mData[2] = MONTH;
        mData[3] = HEIGHT;
        mData[4] = WEIGHT;
        mData[5] = SEX;
        mData[6] = MEAL;
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

}
