package com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.BluetoothAlarmService;
import com.ssu.sangjunianjuni.smartbabycare.R;

/**
 * Created by kang on 2017-06-27.
 */

//블루투스로부터 데이터 수신 알람시 뜨는 다이얼로그
public class AlarmDialog extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_page);
        TextView text=(TextView)findViewById(R.id.alarmdialogtext);
        text.setText("기저귀를 확인해 주세요");

        final Intent service = new Intent(getApplicationContext(), BluetoothAlarmService.class);
        startService(service);
        Button OK=(Button)findViewById(R.id.dialogfinishbutton);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(service);
                finish();
            }
        });
    }
}
