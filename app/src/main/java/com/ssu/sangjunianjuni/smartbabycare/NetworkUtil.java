package com.ssu.sangjunianjuni.smartbabycare;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by yoseong on 2017-05-24.
 * 네트워크 연결
 */

public class NetworkUtil {
    @SuppressLint("NewApi")
    static public void setNetworkPolicy() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}
