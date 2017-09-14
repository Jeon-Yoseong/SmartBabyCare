package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryItem;

/**
 * Created by yoseong on 2017-04-29.
 */

public class BabyBoardItem {

    private String titleStr;
    private String writerStr;
    private String dateStr;
    private String contentsStr;
    private String USER_ID;
    private String PHOTO_URI;

    public BabyBoardItem() {}

    public BabyBoardItem(String titleStr, String writerStr, String dateStr, String contentsStr, String USER_ID, String PHOTO_URI) {
        this.titleStr = titleStr;
        this.writerStr = writerStr;
        this.dateStr = dateStr;
        this.contentsStr = contentsStr;
        this.USER_ID = USER_ID;
        this.PHOTO_URI = PHOTO_URI;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setWriter(String writer) {
        writerStr = writer;
    }

    public void setDate(String date) {
        dateStr = date;
    }

    public void setContent(String content) {
        contentsStr = content;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setPHOTO_URI(String PHOTO_URI) {
        this.PHOTO_URI = PHOTO_URI;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getWriter() {
        return this.writerStr;
    }

    public String getDate() {
        return this.dateStr;
    }

    public String getContent() {
        return this.contentsStr;
    }

    public String getUSER_ID() {
        return this.USER_ID;
    }

    public String getPHOTO_URI() {
        return this.PHOTO_URI;
    }
}
