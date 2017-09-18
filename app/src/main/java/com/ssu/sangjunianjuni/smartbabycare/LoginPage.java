package com.ssu.sangjunianjuni.smartbabycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 로그인 페이지
 * Created by yoseong on 2017-05-01.
 */

public class LoginPage extends AppCompatActivity {

    private TextView textID;
    private TextView textPW;
    private Button login_btn;
    private Button register_btn;
    private EditText inputId;
    private EditText inputPassword;
    private CheckBox autoLogin;
    private boolean loginChecked;
    // 자동 로그인 위해 사용하는 sharedPreference
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref_not_auto;
    private SharedPreferences.Editor editor_not_auto;



    // mysql
    ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    //   PHPDown task;
    PHPPhotoDown taskPhoto;
    private int loginFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        NetworkUtil.setNetworkPolicy();

        textID = (TextView) findViewById(R.id.textID);
        textPW = (TextView) findViewById(R.id.textPW);

        textID.setPaintFlags(textID.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textPW.setPaintFlags(textPW.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        //getSupportActionBar().setTitle("로그인");

        inputId = (EditText) findViewById(R.id.id);
        inputPassword = (EditText) findViewById(R.id.password);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);



        // 로그인 처리
        login_btn = (Button) findViewById(R.id.btnLogin);

//        task = new PHPDown();
//        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getinfo.php");

        taskPhoto = new PHPPhotoDown();
        taskPhoto.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getmemberinfo.php");


        inputId = (EditText) findViewById(R.id.id);

        // 회원가입
        register_btn = (Button) findViewById(R.id.btnRegister);

        register_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
/*
    // mysql에서 데이터 불러오기
    private class PHPDown extends AsyncTask<String, Integer, String> {
        private String USER_ID;
        private String PASSWORD;

        @Override
        protected String doInBackground(String... params) {

            StringBuilder jsonHtml = new StringBuilder();

            try {
                        URL url = new URL(params[0]);
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                        if (conn != null) {
                            conn.setConnectTimeout(10000);
                            conn.setUseCaches(false);

                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                                for (;;) {
                                    String line = br.readLine();
                                    if (line == null) break;
                                    jsonHtml.append(line + "\n");
                                }
                        br.close();
                    }
                    conn.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    USER_ID = jo.getString("USER_ID");
                    PASSWORD = jo.getString("PASSWORD");
                    listItem.add(new ListItem(USER_ID, PASSWORD));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 자동 로그인
            pref = getSharedPreferences("pref", 0);
            editor = pref.edit();
            if(pref.getBoolean("autoLogin", false)) {
                inputId.setText(pref.getString("id",""));
                inputPassword.setText(pref.getString("password",""));
                autoLogin.setChecked(true);
            }

            //로그인 체크
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //자동 로그인
                    if(autoLogin.isChecked()) {
                        String id = inputId.getText().toString();
                        String password = inputPassword.getText().toString();

                        editor.putString("id", id);
                        editor.putString("password", password);
                        editor.putBoolean("autoLogin", true);
                        editor.commit();
                    } else {
                        editor.clear();
                        editor.commit();
                    }
                    for (int i =0;i<listItem.size();i++) {
                        String tmpID = listItem.get(i).getData(0);
                        String tmpPW = listItem.get(i).getData(1);

                        if(tmpID.equals(inputId.getText().toString())){
                            if(tmpPW.equals(inputPassword.getText().toString())){
                                loginFlag = 1;

                                break;
                            }
                        }
                    }
                    if(loginFlag == 1){
                        Intent intent = new Intent(LoginPage.this, MainPage.class);
                        intent.putExtra("USER_ID", inputId.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "ID나 Password를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
*/

    // 프로필 사진 있는 테이블에서 데이터 불러오기
    private class PHPPhotoDown extends AsyncTask<String, String, String> {

        private String USER_ID;
        private String PASSWORD;
        private String NAME;
        private String HEIGHT;
        private String WEIGHT;
        private String PHOTO_URI;
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (;;) {
                            String line = br.readLine();
                            if (line == null) break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    USER_ID = jo.getString("USER_ID");
                    PASSWORD = jo.getString("PASSWORD");
                    NAME = jo.getString("NAME");
                    HEIGHT = jo.getString("HEIGHT");
                    WEIGHT = jo.getString("WEIGHT");
                    PHOTO_URI = jo.getString("PHOTO_URI");
                    listItem.add(new ListItem(USER_ID, PASSWORD, NAME, HEIGHT, WEIGHT, PHOTO_URI));}
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 자동 로그인
            pref = getSharedPreferences("pref", 0);
            editor = pref.edit();
            if(pref.getBoolean("autoLogin", false)) {
                inputId.setText(pref.getString("id",""));
                inputPassword.setText(pref.getString("password",""));
                autoLogin.setChecked(true);
            }

            // 아이디에 맞는 아이 이름 키 몸무게 불러오기 위한 변수
            final int[] getNum = {0};

            //로그인 체크
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //자동 로그인
                    if(autoLogin.isChecked()) {
                        String id = inputId.getText().toString();
                        String password = inputPassword.getText().toString();

                        editor.putString("id", id);
                        editor.putString("password", password);
                        editor.putBoolean("autoLogin", true);
                        editor.commit();
                    } else {
                        editor.clear();
                        editor.commit();
                    }
                    for (int i =0;i<listItem.size();i++) {
                        String tmpID = listItem.get(i).getData(0);
                        String tmpPW = listItem.get(i).getData(1);

                        if(tmpID.equals(inputId.getText().toString())){
                            if(tmpPW.equals(inputPassword.getText().toString())){
                                loginFlag = 1;
                                getNum[0] = i;
                                break;
                            }
                        }
                    }
                    if(loginFlag == 1){
                        pref_not_auto = getSharedPreferences("pref_not_auto", 0);
                        editor_not_auto = pref_not_auto.edit();
                        editor_not_auto.putString("id", inputId.getText().toString());
                        editor_not_auto.commit();
                        Intent intent = new Intent(LoginPage.this, MainPage.class);
                        intent.putExtra("USER_ID", inputId.getText().toString());
                        intent.putExtra("NAME", listItem.get(getNum[0]).getData(2));
                        intent.putExtra("HEIGHT", listItem.get(getNum[0]).getData(3));
                        intent.putExtra("WEIGHT", listItem.get(getNum[0]).getData(4));
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginPage.this, "ID나 Password를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}