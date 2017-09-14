package com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.StringTokenizer;

/**
 * Created by kang on 2017-06-27.
 */

//스마트밴드로부터 받은 데이터를 DB에 입력
public class SmartBandDBHelper extends SQLiteOpenHelper {
    public SmartBandDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS "+"HEARTINFO"+" ("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"time TEXT NOT NULL,"
                +"heartbeat TEXT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String time, String heartbeat){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("INSERT INTO HEARTINFO(time, heartbeat) VALUES('"+time+"','"+heartbeat+"');");
        db.close();;
    }

    public String getheartbeat(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM HEARTINFO", null);
        String heartbeat="-1";
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                heartbeat=cursor.getString(2);
            }
        }
        StringTokenizer str=new StringTokenizer(heartbeat, "\n");
        return str.nextToken().trim();
    }

    public int getaverageheartbeat(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM HEARTINFO", null);
        int heartbeatsum=0, count=0;
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String heart=cursor.getString(2);
                StringTokenizer str=new StringTokenizer(heart, "\n");
                heartbeatsum+=Integer.parseInt(str.nextToken().trim());
                count++;
            }
        }
        if(count==0)
            return -1;
        else
            return heartbeatsum/count;
    }

    public String getResult(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";

        Cursor cursor=db.rawQuery("SELECT * FROM HEARTINFO", null);
        while(cursor.moveToNext()){
            result+=cursor.getString(1)
                    +"-"
                    +cursor.getString(2)
                    +"\n";
        }
        return result;
    }
}
