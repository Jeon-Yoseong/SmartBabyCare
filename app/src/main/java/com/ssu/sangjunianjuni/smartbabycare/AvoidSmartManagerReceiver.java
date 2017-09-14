package com.ssu.sangjunianjuni.smartbabycare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Chiung Choi on 2015-12-20.
 * 스마트매니저 회피용 리시버
 */
public class AvoidSmartManagerReceiver extends BroadcastReceiver {
    private final static String SMART_MANAGER_PACKAGE_NAME = "com.samsung.android.sm";
    private final static String SMART_MANAGER_PACKAGE_NAME_AFTERNOTE7="com.samsung.android.sm.policy";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())
                || Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            boolean isSmartManagerExist = false;

            try {
                context.getPackageManager().getPackageInfo(SMART_MANAGER_PACKAGE_NAME, PackageManager.GET_META_DATA);
                //Toast.makeText(context, "try", Toast.LENGTH_SHORT).show();
                isSmartManagerExist = true;
            } catch (PackageManager.NameNotFoundException e) {
                //Toast.makeText(context, "catch", Toast.LENGTH_SHORT).show();
            }
            try{
                context.getPackageManager().getPackageInfo(SMART_MANAGER_PACKAGE_NAME_AFTERNOTE7, PackageManager.GET_META_DATA);
                //Toast.makeText(context, "try2", Toast.LENGTH_SHORT).show();
                isSmartManagerExist = true;
            } catch (PackageManager.NameNotFoundException e) {
                //Toast.makeText(context, "catch2", Toast.LENGTH_SHORT).show();
            }

            if(isSmartManagerExist) {
                //Toast.makeText(context, "avoid smartmanager", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent serviceIntent = new Intent(context, AvoidSmartManagerActivity.class);
                        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(serviceIntent);
                    }
                }, new Random().nextInt(3000));
            }
        }
    }
}