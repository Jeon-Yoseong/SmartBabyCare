package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by kang on 2017-06-08.
 */

public class DBHelper extends SQLiteOpenHelper {
    Context context;
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS "+"ALARMINFO"+" ("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"title TEXT NOT NULL,"
                +"time TEXT NOT NULL,"
                +"daily TEXT NOT NULL,"
                +"onoff TEXT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String title, String time, String daily, String onoff){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("INSERT INTO ALARMINFO(title, time, daily, onoff) VALUES('"+title+"', '"+time+"', '"+daily+"', '"+onoff+"');");
        db.close();
    }

    public void update(String title, String time, String daily, String onoff){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("UPDATE ALARMINFO SET title='"+title+"', time='"+time+"', daily='"+daily+"', onoff='"+onoff+"' WHERE title='"+title+"' AND time='"+time+"';");
        showdb();
        db.close();
    }

    public void showdb(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";

        Cursor cursor=db.rawQuery("SELECT * FROM ALARMINFO", null);
        while(cursor.moveToNext()){
            result+=cursor.getString(1)
                    +"/"
                    +cursor.getString(2)
                    +"/"
                    +cursor.getString(3)
                    +"/"
                    +cursor.getString(4)
                    +"\n";
            Toast.makeText(context, "result "+result, Toast.LENGTH_SHORT).show();
        }

    }

    public void delete(String title, String time){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("DELETE FROM ALARMINFO WHERE title='"+title+"' AND time='"+time+"';");
        db.close();
    }

    public String getResult(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";

        Cursor cursor=db.rawQuery("SELECT * FROM ALARMINFO", null);
        while(cursor.moveToNext()){
            result+=cursor.getString(1)
                    +"/"
                    +cursor.getString(2)
                    +"/"
                    +cursor.getString(3)
                    +"/"
                    +cursor.getString(4)
                    +"\n";
        }
        return result;
    }
}
