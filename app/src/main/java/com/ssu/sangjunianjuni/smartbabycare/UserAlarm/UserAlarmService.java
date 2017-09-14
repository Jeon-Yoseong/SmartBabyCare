package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

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

import com.ssu.sangjunianjuni.smartbabycare.BackgroundAlertThread;
import com.ssu.sangjunianjuni.smartbabycare.R;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarmPage;

/**
 * Created by kang on 2017-05-30.
 */

public class UserAlarmService extends Service{
    @Nullable

    NotificationManager Notifim;
    Notification Notifi;
    BackgroundAlertThread Thread;
    Intent intent;
    //벨소리/진동 설정 변수
    boolean changed=false;
    boolean ismute=false;
    //푸쉬 알람 설정 변수
    boolean changepush=false;
    String orig="";
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
        //설정에서 벨소리/진동 유무랑 푸쉬알람 여부를 받아온다. 푸쉬알람은 아직 구현 안됨
        sharedPreferences=getSharedPreferences("setting", MODE_PRIVATE);
        setalarm=sharedPreferences.getString("alarm", "");
        setpushalarm=sharedPreferences.getString("pushalarm", "");

        maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        //푸쉬알람도 벨소리/진동 유무 설정과 같이 진동/소리일경우 무음으로 설정해 줬다가 다시 돌리면 됨
        if(setpushalarm.equals("off")){//푸쉬알람 꺼진 경우
            if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE) {//원래 진동인 경우
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                changepush = true;
                orig="vibrate";
            }
            else if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL) {//원래 벨소리인 경우
                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                changepush = true;
                orig = "sound";
            }
            else{//원래 무음인 경우
                orig="silent";
            }
        }
        else{//푸쉬알람 켜진 경우 벨소리/진동 유무를 본다.
            //2.벨소리/진동 유무 설정:현재 폰 상태가 진동인지 무음인지 벨소리인지에 따라 다르게 설정한다.
            if(setalarm.equals("on")){//설정값이 벨소리인 경우
                if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){//폰상태가 진동인 경우
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    changed=true;
                }
                else if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT){//폰상태가 무음인 경우
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    changed=true;
                    ismute=true;
                }
            }
            else{//설정값이 진동인 경우
                if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){//폰상태가 벨소리인 경우
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    changed=true;
                }
                else if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT){//폰상태가 무음인 경우
                    maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    changed=true;
                    ismute=true;
                }
            }
        }
        //서비스 핸들러, 스레드, 노티피케이션 매니저 초기화
        Notifim=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        Thread = new BackgroundAlertThread(handler, setalarm);

        AlertDialog alertdialog=new AlertDialog.Builder(UserAlarmService.this)//알람시 뜨는 다이얼로그
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
        alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {//다이얼로그 뜰 시 스레드 시작
            @Override
            public void onShow(DialogInterface dialog) {
                Toast.makeText(UserAlarmService.this, "onshow", Toast.LENGTH_SHORT).show();
                Thread.start();
            }
        });
        alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {//다이얼로그 종료 시(확인 버튼 누른 경우) 스레드 종료
            @Override
            public void onDismiss(DialogInterface dialog) {
                Thread.stopForever();
                if(changepush){//푸쉬알람때문에 변경된 경우 돌려놓는다
                    if(orig.equals("vibrate")){
                        maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }
                    else if(orig.equals("sound")){
                        maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    else{
                        maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
                else{//푸쉬변경이 없던 경우 벨소리/진동 변경이 있을시 이를 돌려놓는다
                    if(changed){//설정으로 인해 디바이스 소리/진동 설정이 바뀌었던 경우 원상태로 돌려놓는다.
                        maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
                        if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){//벨소리/무음->진동으로 바뀐 경우
                            if(ismute)//원래 무음이었던 경우
                                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            else
                                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                        if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){//진동/무음->벨소리로 바뀐 경우
                            if(ismute)
                                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            else
                                maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        }
                    }
                }
                Notifim.cancel(777);
            }
        });
        alertdialog.show();

        //죽지 않는 서비스
        /*startForeground(startId,new Notification());
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .build();

        }else{
            notification = new Notification(0, "", System.currentTimeMillis());
        }
        nm.notify(startId, notification);
        nm.cancel(startId);*/


        return START_REDELIVER_INTENT;
    }
    @Override
    public void onCreate(){//onCreate()로 실행해야 단 한번만 실행됨
        Notifim=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        Thread = new BackgroundAlertThread(handler, setalarm);

    }


    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "ondestroy", Toast.LENGTH_SHORT).show();
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

    class myServiceHandler extends Handler {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override

        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(UserAlarmService.this, UserAlarmPage.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(UserAlarmService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
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

