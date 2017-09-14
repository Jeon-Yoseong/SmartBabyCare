package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kang on 2017-05-30.
 */

public class UserAlarmReceiver extends BroadcastReceiver {

    static final int GET_DIALOGINFO=2;
    public static Intent intent, intent2;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Bundle bundle=intent.getExtras();
        String test=bundle.getString("test");
        intent=new Intent(context,UserAlarmService.class);
        if(test.equals("end")){
            context.stopService(intent);
        }
        else{//알람 종료가 아닌 경우->알람이 울릴 요일을 파악하여 해당 요일에만 알람 서비스 시작
            boolean[] daysofweek=new boolean[7];//요일 파악을 위한 boolean 배열
            String[] daysofweek2=new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
            for(int i=0; i<7; i++){
                daysofweek[i]=false;//모두 false로 초기화
            }
            StringTokenizer str=new StringTokenizer(test, ":");
            while(str.hasMoreTokens()){//알람이 울릴 요일을 하나씩 비교한다
                String day=str.nextToken();
                for(int j=0; j<7; j++){
                    if(day.equals(daysofweek2[j])){//각 요일별로 boolean배열의 해당 파트를 true로 설정
                        daysofweek[j]=true;
                    }
                }
            }
            Calendar calendar=Calendar.getInstance();
            int currentday= calendar.get(Calendar.DAY_OF_WEEK);//캘린더에서 가져 온 현재 요일, 1~7사이로 표시(일~토)->1 뺀 후 비교할 것
            if(daysofweek[currentday-1]==true){//현재 요일에 알람이 설정되어 있는 경우 서비스 시작
                context.startService(intent);
            }

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        String returndata=new String();
        if(requestCode==GET_DIALOGINFO){
            if(resultCode==RESULT_OK){
                returndata=data.getStringExtra("RESULT_END");
                Toast.makeText(context, returndata, Toast.LENGTH_SHORT).show();
                if(returndata.equals("end"))
                    context.stopService(intent);
            }
        }
    }


}
