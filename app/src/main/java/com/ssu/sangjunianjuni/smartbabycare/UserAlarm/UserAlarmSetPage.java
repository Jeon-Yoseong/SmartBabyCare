package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by kang on 2017-06-04.
 */

public class UserAlarmSetPage extends AppCompatActivity {

    private TimePicker alarmtimepicker;
    private EditText alarmtitletext;
    private Button sendalarmbutton, cancelalarmbutton;
    private CheckBox dailyrepeatcheckbox;
    private int hour,minute;
    private String daily="false";
    private String onoff;
    //요일별 알람을 위한 요소
    private boolean daysofweek[];//알람이 울릴 요일을 구분하기 위한 boolean 배열
    private RelativeLayout weekdays[];
    private RelativeLayout monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private String [] days=new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_alarm_set_page);

        alarmtimepicker=(TimePicker)findViewById(R.id.alarmtimepicker);
        alarmtitletext=(EditText)findViewById(R.id.alarmtitletext);
        alarmtitletext.setText("알람");
        dailyrepeatcheckbox=(CheckBox)findViewById(R.id.dailyrepeatcheckbox);
        dailyrepeatcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox) view).isChecked()){
                    daily="true";
                }else{
                    daily="false";
                }
            }
        });
        //요일별 알람
        daysofweek=new boolean[7];
        for(int i=0; i<7; i++){
            daysofweek[i]=false;//전부 false로 초기화
        }

        //각 요일별 버튼 배열
        weekdays=new RelativeLayout[7];
        weekdays[0]=(RelativeLayout)findViewById(R.id.Sunday);
        weekdays[1]=(RelativeLayout)findViewById(R.id.Monday);
        weekdays[2]=(RelativeLayout)findViewById(R.id.Tuesday);
        weekdays[3]=(RelativeLayout)findViewById(R.id.Wednesday);
        weekdays[4]=(RelativeLayout)findViewById(R.id.Thursday);
        weekdays[5]=(RelativeLayout)findViewById(R.id.Friday);
        weekdays[6]=(RelativeLayout)findViewById(R.id.Saturday);
        for(int i=0; i<7; i++){
            int j=i;
            weekdays[j].setOnClickListener(new View.OnClickListener() {//특정 요일을 클릭할 경우
                @Override
                public void onClick(View v) {
                    if(daysofweek[j]==false){//현재 체크가 안되있는 경우
                        weekdays[j].setBackgroundColor(Color.rgb(0,216,255));
                        daysofweek[j]=true;
                    }
                    else if(daysofweek[j]==true){//현재 체크가 되어있는 경우
                        weekdays[j].setBackgroundColor(getResources().getColor(R.color.color_bg ));
                        daysofweek[j]=false;
                    }

                }
            });
        }

        //초기상태에서 현재요일은 체크된 상태로 감
        Calendar calendar=Calendar.getInstance();
        int currentday=calendar.get(Calendar.DAY_OF_WEEK);
        daysofweek[currentday-1]=true;
        weekdays[currentday-1].setBackgroundColor(Color.rgb(0,216,255));

        sendalarmbutton=(Button)findViewById(R.id.sendalarminfo);
        sendalarmbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String alarmtitle=alarmtitletext.getText().toString();
                hour=alarmtimepicker.getHour();
                minute=alarmtimepicker.getMinute();
                String daysweek="";//알람 페이지에 보낼 요일 데이터
                for(int i=0; i<7; i++){
                    if(daysofweek[i]==true){
                        daysweek=daysweek.concat(days[i]);
                        if(i!=6){
                            for(int j=i+1; j<7; j++){
                                if(daysofweek[j]==true) {
                                    daysweek = daysweek.concat(":");
                                    break;
                                }
                            }
                        }
                    }
                }
                if(onoff==null)
                    onoff="true";
                String result=new String();
                result=result.concat(alarmtitle+"."+Integer.toString(hour)+"."+Integer.toString(minute)+"."+daysweek+"."+onoff);
                Intent intent=new Intent();
                intent.putExtra("RESULT_TEXT", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelalarmbutton=(Button)findViewById(R.id.cancelalarminfo);
        cancelalarmbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //알람페이지로부터 데이터를 받아온다.
        //단순 알람 추가인경우 null값을, 기존 알람 수정일경우 기존 알람 정보값을 받아온다.
        Intent intent=getIntent();
        String data=intent.getStringExtra("alarm");
        //알람 수정인 경우
        if(data!=null){
            StringTokenizer str=new StringTokenizer(data, "-");
            String title=str.nextToken();
            String time=str.nextToken();
            String daysofweek=str.nextToken();
            onoff=str.nextToken();
            str=new StringTokenizer(time, ":");
            String hour=str.nextToken();
            String minute=str.nextToken();
            alarmtitletext.setText(title);
            alarmtimepicker.setHour(Integer.valueOf(hour.trim()));
            alarmtimepicker.setMinute(Integer.valueOf(minute.trim()));
            str=new StringTokenizer(daysofweek, ":");
            while(str.hasMoreTokens()){
                String day=str.nextToken();
                for(int i=0; i<7; i++){
                    if(days[i].equals(day)) {
                        weekdays[i].setBackgroundColor(Color.rgb(0, 216, 255));
                        this.daysofweek[i]=true;
                    }
                }
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}
