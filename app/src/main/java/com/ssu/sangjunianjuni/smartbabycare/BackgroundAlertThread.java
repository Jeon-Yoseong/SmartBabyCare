package com.ssu.sangjunianjuni.smartbabycare;

/**
 * Created by kang on 2017-05-27.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 백그라운드 알림에 사용할 스레드
 */

public class BackgroundAlertThread extends Thread {
    Handler handler;
    boolean isrun=true;
    String alarmsound="";
    Bundle data=new Bundle();
    Message msg=new Message();

    public BackgroundAlertThread(Handler handler, String alarmsound){
        this.handler=handler;
        this.alarmsound=alarmsound;
    }
    public void stopForever(){
        synchronized (this){
            //data.putString("alarm", "stop");
            //msg.setData(data);
            //handler.sendMessage(msg);
            this.isrun=false;

        }
    }
    public void run(){
        if(alarmsound.equals("on")){//벨소리때는 한번만 핸들링 보낸다.
            //data.putString("alarm", "on");
            //msg.setData(data);
            while(isrun){//진동은 1초딜레이로 계속 보낸다.
                //msg=handler.obtainMessage();
                handler.sendEmptyMessage(0);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
            //handler.sendMessage(msg);
        }
        else{
            //data.putString("alarm", "off");
            //msg.setData(data);
            while(isrun){//진동은 1초딜레이로 계속 보낸다.
                //msg=handler.obtainMessage();
                handler.sendEmptyMessage(0);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        }
    }
}


