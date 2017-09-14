package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

/**
 * Created by yoseong on 2017-07-05.
 */

public class BabyDiaryItem {

    private int Index;
    private String USER_ID;
    private String Date;
    private String Title;
    private String Author;
    private String Content;
    private String DiaryPhoto;
    private String PHOTO_URI;

    public BabyDiaryItem(int index, String user_id, String date, String title, String author, String content, String diaryPhoto) {
        Index = index;
        USER_ID = user_id;
        Date = date;
        Title = title;
        Author = author;
        Content = content;
        DiaryPhoto = diaryPhoto;
    }

    public BabyDiaryItem(int index, String user_id, String date, String title, String author, String content, String diaryPhoto, String PHOTO_URI) {
        Index = index;
        USER_ID = user_id;
        Date = date;
        Title = title;
        Author = author;
        Content = content;
        DiaryPhoto = diaryPhoto;
        this.PHOTO_URI = PHOTO_URI;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String user_id) {
        Author = user_id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDiaryPhoto() {
        return DiaryPhoto;
    }

    public void setDiaryPhoto(String diaryPhoto) {
        DiaryPhoto = diaryPhoto;
    }

    public String getPHOTO_URI() {
        return this.PHOTO_URI;
    }

    public void setPHOTO_URI(String PHOTO_URI) {
        this.PHOTO_URI = PHOTO_URI;
    }
}
