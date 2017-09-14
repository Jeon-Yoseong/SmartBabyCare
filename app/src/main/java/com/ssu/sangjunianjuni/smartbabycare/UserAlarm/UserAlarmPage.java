package com.ssu.sangjunianjuni.smartbabycare;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarm.UserAlarmAdapter;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarm.UserAlarmReceiver;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarm.UserAlarmSetPage;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarm.DBHelper;
import java.util.StringTokenizer;
import static java.lang.Thread.sleep;
/**
 * Created by kang on 2017-05-30.
 */
/**
 * 사용자 지정 알람 메인 페이지
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class UserAlarmPage extends AppCompatActivity{
    static final int GET_ALARMINFO=1;
    private Toolbar toolbar1=null;
    private int mHour, pickHour, mMinute, pickMinute;//현 시간, 선택한 시간, 현 분, 선택한 분
    private Calendar calendar=null;
    private ListView alarmlistview=null;
    private UserAlarmAdapter adapter=null;
    private DBHelper dbHelper=null;
    private String fromdb="";
    public static AlarmManager alarmManager;
    public static Intent intent;
    public static PendingIntent pendingIntent;
    protected void onCreate(Bundle savedInstanceState){


        Toast.makeText(getApplicationContext(), "oncreate", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_alarm_page);
        dbHelper = new DBHelper(getApplicationContext(), "alarminfo.db", null, 1);
        //툴바 초기화
        toolbar1=(Toolbar)findViewById(R.id.toolbar_useralarm);
        toolbar1.setTitle("알람");
        setSupportActionBar(toolbar1);
        //버튼 및 리스트뷰 초기화
        alarmlistview=(ListView)findViewById(R.id.alarmlist);
        alarmlistview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new UserAlarmAdapter();
        alarmlistview.setAdapter(adapter);


        showList();
        //리스트에 아이템이 있는 경우
        if(adapter.ListItemLIst.size()!=0){
            setAlarms();//알람 설정
            setListener();
        }
        adapter.notifyDataSetChanged();
        alarmlistview.setAdapter(adapter);

    }
    /*protected void onResume(){
        super.onResume();
        //프리퍼런스에서 설정 데이터 불러온다
        //mainpage에서 프리퍼런스가 초기상태일 경우 기본값을 넣어준다.
    }*/
    //시각 선택 다이얼로그
    /*private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String timeStr = String.format("%d : %d", hourOfDay, minute);
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hourOfDay, minute, 0);
        }
    };*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_alarm_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.user_alarm_setting) {//툴바의 알람 추가 버튼 클릭 시
            Intent intent = new Intent(UserAlarmPage.this, UserAlarmSetPage.class);
            startActivityForResult(intent, GET_ALARMINFO);
        }
        return super.onOptionsItemSelected(item);
    }
    //UserAlarmSetPage로부터 결과를 받아와서 리스트뷰에 등록 및 알람 설정
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        int onoff;//sqlite db에 입력할 때 사용할 data
        String returndata=new String();
        returndata="";
        if(requestCode==GET_ALARMINFO){
            if(resultCode==RESULT_OK){
                returndata=data.getStringExtra("RESULT_TEXT");
            }
        }
        StringTokenizer str=new StringTokenizer(returndata, ".");
        if(str.countTokens()!=0)
        {
            //받아온 데이터를 요소별로 분리
            str.nextToken();
            pickHour=Integer.parseInt(str.nextToken());
            pickMinute=Integer.parseInt(str.nextToken());
            str=new StringTokenizer(returndata, ".");
            String alarmtitle, alarmtime, alarmdaily, alarmonoff;
            alarmtitle=str.nextToken();
            alarmtime=str.nextToken();
            alarmtime=alarmtime.concat(":");
            alarmtime=alarmtime.concat(str.nextToken());
            alarmdaily=str.nextToken();
            alarmonoff=str.nextToken();
            //sql 등록 부분
            dbHelper.insert(alarmtitle, alarmtime, alarmdaily, alarmonoff);
            //리스트 등록 부분
            //adapter.ListItemLIst.add(new ListItem(alarmtitle,alarmtime,alarmdaily));
            alarmlistview.setAdapter(adapter);
            int i=adapter.ListItemLIst.size()-1;
            adapter.addItem(alarmtitle, alarmtime, alarmdaily, alarmonoff);
        }


        showList();
        //리스트에 아이템이 있는 경우
        if(adapter.ListItemLIst.size()!=0){
            setAlarms();//알람 설정
            setListener();
        }
        adapter.notifyDataSetChanged();
        alarmlistview.setAdapter(adapter);
    }
    //리스트뷰에 알람 리스트 아이템들을 등록하는 함수
    public void showList(){
        //if(!adapter.isEmpty())//어댑터에 내용이 있는 경우 초기화 후 다시 등록한다.
        adapter.deleteitem();
        //for(int i=0; i<listItem.size(); i++)
        //    listItem.remove(i);
        fromdb=dbHelper.getResult();
        StringTokenizer str=new StringTokenizer(fromdb, "\n");
        while(str.hasMoreTokens()){
            String alarm=str.nextToken();
            StringTokenizer str2=new StringTokenizer(alarm, "/");
            String title=str2.nextToken();
            String time=str2.nextToken();
            String daily=str2.nextToken();
            String onoff=str2.nextToken();
            //adapter.ListItemLIst.add(new ListItem(title,time,daily));
            int i=adapter.ListItemLIst.size()-1;
            //0번째:title, 1번째:time, 2번째:daily
            adapter.addItem(title, time, daily, onoff);
        }
        //adapter.notifyDataSetChanged();
        alarmlistview.setAdapter(adapter);
    }
    public void setListener(){
        if(alarmlistview!=null){
            //리스트뷰에 컨텍스트 메뉴 리스너 생성
            //주의사항:리스트뷰에 체크박스나 라디오버튼 등이 있는 경우 해당 아이템이 포커스를 받을수 있기 때문에 메뉴 리스너가 무효화된다
            //->리스트 아이템 레이아웃의 체크박스,라디오버튼 항목에 android:focusable="false"로 설정해주면 정상 작동
            alarmlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(id==R.id.onoffcheckbox){
                        Toast.makeText(getApplicationContext(), adapter.ListItemLIst.get(position).getData(0)
                                +" "+adapter.ListItemLIst.get(position).getData(0)
                                +"check", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alarmlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    PopupMenu popup = new PopupMenu(getApplicationContext(), view);//view는 오래 눌러진 뷰를 의미
                    popup.getMenuInflater().inflate(R.menu.alarm_list_menu, popup.getMenu());
                    final int index = position;
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.modify:
                                    Intent intent = new Intent(UserAlarmPage.this, UserAlarmSetPage.class);
                                    String text="";
                                    text=text.concat(adapter.ListItemLIst.get(index).getData(0)+"-")
                                            .concat(adapter.ListItemLIst.get(index).getData(1)+"-")
                                            .concat(adapter.ListItemLIst.get(index).getData(2)+"-")
                                            .concat(adapter.ListItemLIst.get(index).getData(3));//나눌때 -로 구분
                                    intent.putExtra("alarm", text);//아직 미완성
                                    startActivityForResult(intent, GET_ALARMINFO);
                                    //modify시 다시 울리는 문제 발생, 기존 알람 계속 울리는 문제랑 비슷하게 해결하면 될 듯
                                    deleteAlarms(index);
                                    break;
                                case R.id.delete:
                                    deleteAlarms(index);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    return true;
                }
            });
        }
    }

    //알람 설정 함수에서 사용할 string 변수들
    //String alarmtime="", onoff="", daily="";
    //리스트뷰 전체 아이템 알람 설정 함수
    public void setAlarms(){
        //Toast.makeText(getApplicationContext(), "setalarms", Toast.LENGTH_SHORT).show();
        String alarmtime="";
        String onoff="";
        String daily="";
        for(int i=0; i<adapter.ListItemLIst.size(); i++){
            onoff=adapter.ListItemLIst.get(i).getData(3);
            if(onoff.equals("true")){//onoff체크박스에 체크가 되어있는 경우만 알람 적용
                alarmtime=adapter.ListItemLIst.get(i).getData(1);
                daily=adapter.ListItemLIst.get(i).getData(2);
                //Toast.makeText(getApplicationContext(), onoff, Toast.LENGTH_SHORT).show();
                StringTokenizer str=new StringTokenizer(alarmtime, ":");
                int hour=Integer.parseInt(str.nextToken());
                int minute=Integer.parseInt(str.nextToken());
                int key=hour*60+minute+i;//각 알람을 구분해줄 고유 키:알람 시간*60+분+리스트뷰에서의 순서
                calendar=Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, minute, 0);
                alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                intent=new Intent(getApplicationContext(), UserAlarmReceiver.class);
                //FLAG_CANCEL_CURRENT 변수:이미 알람이 등록되어 있던 아이템의 경우 해당 알람을 취소하고 새로 만든다.
                //pendingintent를 불러와서 이미 있는 경우에는 새로이 알람 등록 x(이미 알람이 등록되어있는 상태), 없는 경우 pendingintent 생성 후 그 인텐트로 알람 등록
                if(daily.equals("true")){
                    intent.putExtra("test", "startdaily");
                    //Toast.makeText(this, calendar.get(Calendar.YEAR)+":"+calendar.get(Calendar.MONTH)+":"+calendar.get(Calendar.DATE)+":"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();
                    //flag_no_create: 기존에 생성된 pendingintent가 있으면 그거 가져온다
                    pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_NO_CREATE);
                    boolean result=(pendingIntent==null);
                    if(result) {
                        pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
                else if(daily.equals("false")){
                    intent.putExtra("test", "startnotdaily");
                    //flag_no_create: 기존에 생성된 pendingintent가 있으면 그거 가져온다
                    pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_NO_CREATE);
                    boolean result=(pendingIntent==null);
                    if(result) {//기존에 pendingintent가 없는 경우
                        pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                    //alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                }
            }
            else if(onoff.equals("false")){
                pauseAlarms(i);
            }
        }
    }

    //알람 삭제(리스트뷰의 i번째 아이템 알람 삭제)
    public void deleteAlarms(int i){
        String alarmtime="";
        alarmtime=adapter.ListItemLIst.get(i).getData(1);
        StringTokenizer str=new StringTokenizer(alarmtime, ":");
        int hour=Integer.parseInt(str.nextToken());
        int minute=Integer.parseInt(str.nextToken());
        int key=hour*60+minute+i;//setAlarms()에서 설정해준 알람을 해지하기 위해서는 동일한 키 값이 필요
        //알람 등록 해제
        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        intent=new Intent(getApplicationContext(), UserAlarmReceiver.class);
        intent.putExtra("test", "end");
        pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);//
        pendingIntent.cancel();
        alarmManager=null;
        pendingIntent=null;
        //db에서 해당 알람 삭제
        dbHelper.delete(adapter.ListItemLIst.get(i).getData(0), adapter.ListItemLIst.get(i).getData(1));//listitem 0번째 title, 1번째 time
        //리스트아이템 삭제, 리스트뷰 갱신
        adapter.ListItemLIst.remove(i);
        showList();
    }
    public void pauseAlarms(int i){
        String alarmtime="";
        alarmtime=adapter.ListItemLIst.get(i).getData(1);
        StringTokenizer str=new StringTokenizer(alarmtime, ":");
        int hour=Integer.parseInt(str.nextToken());
        int minute=Integer.parseInt(str.nextToken());
        int key=hour*60+minute+i;//setAlarms()에서 설정해준 알람을 해지하기 위해서는 동일한 키 값이 필요
        //알람 등록 해제
        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        intent=new Intent(getApplicationContext(), UserAlarmReceiver.class);
        intent.putExtra("test", "end");
        pendingIntent=PendingIntent.getBroadcast(UserAlarmPage.this, key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);//
        pendingIntent.cancel();
        alarmManager=null;
        pendingIntent=null;
    }
}