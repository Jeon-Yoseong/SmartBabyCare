package com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.StringTokenizer;

/**
 * Created by kang on 2017-06-27.
 */

//기저귀 센서에서 측정한 데이터를 저장하기 위한 DB
public class BlueToothDBHelper extends SQLiteOpenHelper {

    public BlueToothDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS "+"POOPINFO"+" ("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"date TEXT NOT NULL, "
                +"time TEXT NOT NULL);";
        db.execSQL(sql);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String date, String time){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("INSERT INTO POOPINFO(date, time) VALUES('"+date+"','"+time+"');");
        db.close();
    }

    public int getcount(String time){
        SQLiteDatabase db=getReadableDatabase();
        int count=0;

        Cursor cursor=db.rawQuery("SELECT * FROM POOPINFO", null);
        while(cursor.moveToNext()){
            String dbdate=cursor.getString(1);
            if(dbdate.contains(time))
                count++;
        }
        return count;
    }

    public String getrecent(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM POOPINFO", null);
        String dbdate="", dbtime="";
        if(cursor.getCount()!=0){
            cursor.moveToLast();
            dbdate=cursor.getString(1);
            dbtime=cursor.getString(2);
            //cursor.moveToNext();
            //String dbtime=cursor.getString(1);
            //StringTokenizer str=new StringTokenizer(dbtime, " ");
            //str.nextToken();
            //dbtime=str.nextToken();
            dbdate=dbdate.concat(" "+dbtime);
        }
        return dbdate;
    }

    public int getaveragepoop(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM POOPINFO", null);
        String recentdate="", lastdate="";
        int date=0;
        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            recentdate=cursor.getString(1);
            cursor.moveToLast();
            lastdate=cursor.getString(1);
            /*if(cursor.getCount()==1){
                cursor.moveToNext();
                lastdate=cursor.getString(1);
                recentdate=cursor.getString(1);
            }
            else{
                cursor.moveToNext();
                recentdate=cursor.getString(1);
                while(cursor.moveToNext()){
                    lastdate=cursor.getString(1);
                }
            }*/
            Log.e("TAG", "lastdate:"+lastdate+" recentdate:"+recentdate);
            StringTokenizer str1=new StringTokenizer(recentdate, "/");
            StringTokenizer str2=new StringTokenizer(lastdate, "/");
            String lastyear=str1.nextToken();
            String lastmonth=str1.nextToken();
            String lastday=str1.nextToken();
            //str1=new StringTokenizer(lastday, " ");
            //lastday=str1.nextToken();
            String recentyear=str2.nextToken();
            String recentmonth=str2.nextToken();
            String recentday=str2.nextToken();
            //str2=new StringTokenizer(recentday, " ");
            //recentday=str2.nextToken();
            int year=Integer.parseInt(lastyear.trim())-Integer.parseInt(recentyear.trim());
            int month=Integer.parseInt(lastmonth.trim())-Integer.parseInt(recentmonth.trim());
            int day=Integer.parseInt(lastday.trim())-Integer.parseInt(recentday.trim());
            date=year*365+month*30+day;
            Log.e("TAG", "year:"+year+" month:"+month+" day:"+day);
        } else{
            date=1;
        }
        if(date<0)
            date*=-1;
        Log.e("TAG", "cursor:"+cursor.getCount()+" date:"+date);
        return cursor.getCount()/date;
    }

    public String getResult(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";

        Cursor cursor=db.rawQuery("SELECT * FROM POOPINFO", null);
        while(cursor.moveToNext()){
            result=result.concat(cursor.getString(1));
            result=result.concat("|");
            result=result.concat(cursor.getString(2));
            result=result.concat("\n");
        }
        return result;

    }
}