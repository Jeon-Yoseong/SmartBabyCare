package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by yoseong on 2017-07-04.
 */

public class BabyDiaryDBHelper extends SQLiteOpenHelper {

    // 생성자로 관리 할 DB 이름과 버전 정보를 받음
   public BabyDiaryDBHelper(Context context, String DB_name, SQLiteDatabase.CursorFactory factory, int version) {
       super(context,  DB_name, factory, version);
   }

   // DB를 새로 생성할 때 호출되는 함수
   @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
       String query = "CREATE TABLE IF NOT EXISTS " + "BABYDIARY"+" ("
               +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
               +"Date TEXT NOT NULL, "
               +"Title TEXT NOT NULL, "
               +"Author TEXT NOT NULL, "
               +"Content TEXT, "
               +"DiaryPhoto TEXT);";
       db.execSQL(query);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // DB에 데이터 삽입
    public void insert(String Date, String Title, String Author, String Content, String DiaryPhoto) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO BABYDIARY(Date, Title, Author, Content, DiaryPhoto) VALUES('"+Date+"','"+Title+"','"+Author+"','"+Content+"','"+DiaryPhoto+"');");
        db.close();
    }

    // DB에 있는 데이터 수정
    public void update(String Date, String Title, String Content, String DiaryPhoto) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE BABYDIARY SET Title='"+Title+"', Content='"+Content+"', DiaryPhoto='"+DiaryPhoto+"' WHERE Date='"+Date+"';");
        db.close();
    }

    // DB에 있는 데이터 불러오기
    public ArrayList<BabyDiaryItem> getInfo(String USER_ID) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<BabyDiaryItem> listitem = new ArrayList<BabyDiaryItem>();

        Cursor cursor = db.rawQuery("SELECT * FROM BABYDIARY", null);
        while(cursor.moveToNext()) {
            // 현재 사용자 아이디와 작성가 아이디가 같을 경우만 데이터를 불러올 수 있다.
            if(USER_ID.equals(cursor.getString(3))) {
                listitem.add(new BabyDiaryItem(cursor.getInt(0), USER_ID, cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            }
        }
        return listitem;
    }

    // DB에 있는 데이터 삭제
    public void delete(String Date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM BABYDIARY WHERE Date='"+Date+"';");
        db.close();
    }
}
