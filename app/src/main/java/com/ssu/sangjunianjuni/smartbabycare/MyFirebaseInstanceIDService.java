package com.ssu.sangjunianjuni.smartbabycare;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by yoseong on 2017-07-27.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    SharedPreferences pref_not_auto;


    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        String User_ID;
        Log.d(TAG, "Refreshed token: " + token);

        pref_not_auto = getSharedPreferences("pref_not_auto", 0);
        User_ID = pref_not_auto.getString("id","");
        Log.d(TAG, "User_ID: " + User_ID);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token, User_ID);
    }

    private void sendRegistrationToServer(String token, String User_ID) {
        // Add custom implementation, as needed.
        Log.d(TAG, "token: " + token);
        if(!User_ID.equals("")) {
            try {
                PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/register_fcm.php");
                String result = phpRequest.PhPtest(String.valueOf(token), String.valueOf(User_ID));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


/*
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();


        //request
        Request request = new Request.Builder()
                .url("http://rkdehdduq92.cafe24.com/smart_babycare/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

}