package com.ssu.sangjunianjuni.smartbabycare.UserAlarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;

/**
 * Created by kang on 2017-06-08.
 */

public class UserAlarmDialog extends Activity {
    private String notiMessage;
    private Intent service;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_page);
        TextView text=(TextView)findViewById(R.id.alarmdialogtext);
        text.setText("사용자 알람입니다.");

        service = new Intent(getApplicationContext(), UserAlarmService.class);
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

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)
        {
            stopService(service);
            finish();
        }
        return false;
    }

    private class SubmitOnClickListener implements View.OnClickListener {

        public void onClick(View v) {
            Intent intent=new Intent();
            intent.putExtra("RESULT_END", "end");
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}