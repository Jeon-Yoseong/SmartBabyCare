package com.ssu.sangjunianjuni.smartbabycare;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by god on 15. 12. 29..
 * 삼성 스마트 매니저를 피하기 위함
 */
public class AvoidSmartManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(getApplicationContext(), "fuck smartmanager", Toast.LENGTH_SHORT).show();

        finish();
    }
}