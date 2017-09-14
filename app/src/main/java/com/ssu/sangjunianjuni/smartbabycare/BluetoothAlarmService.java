package com.ssu.sangjunianjuni.smartbabycare;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * Created by kang on 2017-05-30.
 */

public class BluetoothAlarmService extends Service{
    @Nullable


    NotificationManager Notifim;
    Notification Notifi;
    BackgroundAlertThread Thread;
    Intent intent;
    boolean changed=false;
    boolean ismute=false;
    private AudioManager maudiomanager;
    //설정값을 불러와서 알람 요소를 변경하기 위한 프리퍼런스와 받아온 설값을 저장할 스트링
    private SharedPreferences sharedPreferences;
    private String setalarm, setpushalarm;
    @Override
    public IBinder onBind(Intent intent) {
        //startservice를 통해 이 서비스를 호출하고 있으므로 onBind는 호출이 안된다. onStartCommand가 호출
        return null;
    }

    //blarmsound 이후 alarmsound, clarmsound, alarmsound, clarmsound....순으로 실행, alarmsound 2번째는 null값 출력, clarmsound 첫번째는 제대로 출력, 2번째부터 쭉 null 출력..왜지 시발...
    //onCreate()는 onStartCommand()이전에 먼저 실행된다->onStartCommand()에서 받아오는 인텐트 값을 어떻게 onCreate()에 반영..
    //현재 onStartCommand가 두번 시행되고 있음... extra data담은채로 한번, 안담은 채로 한번..왜 씨발...
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        sharedPreferences=getSharedPreferences("setting", MODE_PRIVATE);
        setalarm=sharedPreferences.getString("alarm", "");
        setpushalarm=sharedPreferences.getString("pushalarm", "");

        maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        if(setalarm.equals("on")){
            if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                changed=true;
            }
            else if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT){
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                changed=true;
                ismute=true;
            }
        }
        else{
            if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                changed=true;
            }
            else if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT){
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                changed=true;
                ismute=true;
            }
        }

        Notifim=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myBluetoothServiceHandler handler = new myBluetoothServiceHandler();
        Thread = new BackgroundAlertThread(handler, setalarm);

        AlertDialog alertdialog=new AlertDialog.Builder(BluetoothAlarmService.this)
                .setTitle("사용자 알람")
                .setMessage("사용자 알람입니다")
                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Toast.makeText(BluetoothAlarmService.this, "onshow", Toast.LENGTH_SHORT).show();
                Thread.start();
            }
        });
        alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Thread.stopForever();
                if(changed){//설정으로 인해 디바이스 소리/진동 설정이 바뀌었던 경우 원상태로 돌려놓는다.
                    maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
                    if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
                        if(ismute)
                            maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        else
                            maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){
                        if(ismute)
                            maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        else
                            maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }
                }
                Notifim.cancel(777);
            }
        });
        alertdialog.show();

        return START_REDELIVER_INTENT;
    }
    @Override
    public void onCreate(){//onCreate()로 실행해야 단 한번만 실행됨
        Notifim=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myBluetoothServiceHandler handler = new myBluetoothServiceHandler();
        Thread = new BackgroundAlertThread(handler, setalarm);

    }


    public void onDestroy() {
        Thread.stopForever();
        Thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
        Notifim.cancel(777);
        /*if(changed){//설정으로 인해 디바이스 소리/진동 설정이 바뀌었던 경우 원상태로 돌려놓는다.
            maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
            if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
                if(ismute)
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                else
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){
                if(ismute)
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                else
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
        }*/
    }

    class myBluetoothServiceHandler extends Handler {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override

        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(BluetoothAlarmService.this, UserAlarmPage.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothAlarmService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("사용자 알림")
                    .setContentText("사용자 알림")
                    .setSmallIcon(R.drawable.icon)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;
            Notifim.notify( 777 , Notifi);
            //토스트 띄우기

        }
    };
}

