package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.R;
import com.ssu.sangjunianjuni.smartbabycare.UserAlarmPage;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kang on 2017-06-04.
 */

public class UserAlarmAdapter extends BaseAdapter {

    private TextView titleTextView, timeTextView, dailyTextView;
    private CheckBox onOffBox;
    private String onoff;
    private DBHelper dbHelper;
    //UserAlarmPage에서 접근하기 위해 부득이하게 public 선언
    public ArrayList<UserAlarmItem> ListItemLIst = new ArrayList<UserAlarmItem>();

    public UserAlarmAdapter(){}

    @Override
    public int getCount() {
        return ListItemLIst.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용할 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        dbHelper = new DBHelper(context, "alarminfo.db", null, 1);

        // UserAlarm_item Layout을 inflate하여 converView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_alarm_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        titleTextView = (TextView) convertView.findViewById(R.id.alarmTitle);
        timeTextView = (TextView) convertView.findViewById(R.id.alarmtime);
        dailyTextView = (TextView) convertView.findViewById(R.id.alarmdaily);
        onOffBox=(CheckBox) convertView.findViewById(R.id.onoffcheckbox);



        // Data Set(BabyBoardItemList)에서 position에 위치한 데이터 참조 획득
        UserAlarmItem listitem=ListItemLIst.get(position);

        titleTextView.setText(listitem.getData(0));
        timeTextView.setText(listitem.getData(1));
        dailyTextView.setText(listitem.getData(2));
        onoff=listitem.getData(3);
        if(onoff.equals("true")){
            onOffBox.setChecked(true);
        }
        else if(onoff.equals("false")){
            onOffBox.setChecked(false);
        }

        onOffBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    listitem.setData(3, "true");
                    dbHelper.update(listitem.getData(0), listitem.getData(1), listitem.getData(2), listitem.getData(3));
                    /*StringTokenizer str=new StringTokenizer(listitem.getData(1), ":");
                    int hour=Integer.parseInt(str.nextToken());
                    int minute=Integer.parseInt(str.nextToken());
                    int key=hour*60+minute;//각 알람을 구분해줄 고유 키:알람 시간*60+분+리스트뷰에서의 순서(시간이 같을경우 현재 키값이 전부 동일하게 인식됨)*/
                    /*UserAlarmPage page=new UserAlarmPage();
                    page.resetAlarms(hour, minute, key, position);*/

                }
                else{
                    listitem.setData(3, "false");
                    dbHelper.update(listitem.getData(0), listitem.getData(1), listitem.getData(2), listitem.getData(3));
                    /*StringTokenizer str=new StringTokenizer(listitem.getData(1), ":");
                    int hour=Integer.parseInt(str.nextToken());
                    int minute=Integer.parseInt(str.nextToken());
                    int key=hour*60+minute;//각 알람을 구분해줄 고유 키:알람 시간*60+분+리스트뷰에서의 순서(시간이 같을경우 현재 키값이 전부 동일하게 인식됨)*/
                    /*UserAlarmPage page=new UserAlarmPage();
                    page.pauseAlarms(key);*/
                }
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return ListItemLIst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 아이템 데이터 추가를 위한 함스
    public void addItem( String title, String time, String daily, String onoff) {
        UserAlarmItem item = new UserAlarmItem(title, time, daily, onoff);

        ListItemLIst.add(item);
    }

    public void deleteitem(){
        for(int i=0; i<ListItemLIst.size(); i++){
            ListItemLIst.remove(i);
        }
        ListItemLIst.clear();
    }

}
