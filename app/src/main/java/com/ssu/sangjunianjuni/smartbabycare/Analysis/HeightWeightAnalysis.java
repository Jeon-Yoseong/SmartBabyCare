package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.ssu.sangjunianjuni.smartbabycare.AnalysisPage;
import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kang on 2017-05-02.
 */

public class HeightWeightAnalysis extends AppCompatActivity {

    // 사용자 ID
    private String USER_ID;

    private String MONTH;
    private String SEX;
    private String HEIGHT;
    private String WEIGHT;

    private ArrayList<ListItem> listItem = new ArrayList<ListItem>();

    // 뷰 선언
    private TextView myBabyHeight;
    private TextView myBabyWeight;
    private TextView averageHeight;
    private TextView averageWeight;
    private TextView analysisresultHW;

    // 데이터 불러오기
    PHPMyBabyInfo task;
    PHPAverageInfo aTask;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heightweight_analysis);

        Intent getUSER_ID = getIntent();
        USER_ID = getUSER_ID.getStringExtra("USER_ID");

        // 뷰 설정
        myBabyHeight = (TextView) findViewById(R.id.my_baby_height);
        myBabyWeight = (TextView) findViewById(R.id.my_baby_weight);
        averageHeight = (TextView) findViewById(R.id.average_height);
        averageWeight = (TextView) findViewById(R.id.average_weight);
        analysisresultHW = (TextView) findViewById(R.id.analysisresult_hw);

        // 데이터 불러오기
        task = new PHPMyBabyInfo();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getbabyinfo.php");

        aTask = new PHPAverageInfo();
        aTask.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getaveragehwinfo.php");

    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            finish();//액티비티 종료, 분석 액티비티로 돌아감
        }
        return false;
    }

    // mysql에서 데이터 불러오기
    private class PHPMyBabyInfo extends AsyncTask<String, Integer, String> {
        private String GET_USER_ID;
        private String NAME;
        private String GET_MONTH;
        private String GET_HEIGHT;
        private String GET_WEIGHT;
        private String GET_SEX;
        private String MEAL;

        @Override
        protected String doInBackground(String... params) {

            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
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
        protected void onPostExecute(String str) {
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    GET_USER_ID = jo.getString("USER_ID");
                    NAME = jo.getString("NAME");
                    GET_MONTH = jo.getString("MONTH");
                    GET_HEIGHT = jo.getString("HEIGHT");
                    GET_WEIGHT =jo.getString("WEIGHT");
                    GET_SEX = jo.getString("SEX");
                    MEAL = jo.getString("MEAL");
                    listItem.add(new ListItem(GET_USER_ID, NAME, GET_MONTH, GET_HEIGHT, GET_WEIGHT, GET_SEX, MEAL));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < listItem.size(); i++) {
                System.out.println("ID: "+listItem.get(i).getData(0));
                System.out.println("USER_ID: "+USER_ID);
                if(listItem.get(i).getData(0).equals(USER_ID)) {
                    MONTH = listItem.get(i).getData(2);
                    HEIGHT = listItem.get(i).getData(3);
                    WEIGHT = listItem.get(i).getData(4);
                    SEX = listItem.get(i).getData(5);
                    myBabyHeight.setText(listItem.get(i).getData(1)+" 키 : "+listItem.get(i).getData(3)+"Cm");
                    myBabyWeight.setText(listItem.get(i).getData(1)+" 몸무게 : "+listItem.get(i).getData(4)+"Kg");
                }
            }



        }

    }

    // mysql에서 데이터 불러오기
    private class PHPAverageInfo extends AsyncTask<String, Integer, String> {
        private int month;
        private double boyheight;
        private double boyweight;
        private double girlheight;
        private double grilweight;

        private String tempHeight;
        private String tempWeight;

        @Override
        protected String doInBackground(String... params) {

            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
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
        protected void onPostExecute(String str) {
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    month = jo.getInt("month");
                    boyheight = jo.getDouble("boyheight");
                    boyweight = jo.getDouble("boyweight");
                    girlheight = jo.getDouble("girlheight");
                    grilweight = jo.getDouble("girlweight");
                    listItem.add(new ListItem(String.valueOf(month), String.valueOf(boyheight), String.valueOf(boyweight), String.valueOf(girlheight), String.valueOf(grilweight)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("month: "+MONTH);

            for(int i = 0; i < listItem.size(); i++) {
               if(listItem.get(i).getData(0).equals(MONTH)) {
                   if(SEX.equals("남자")) {
                       tempHeight = listItem.get(i).getData(1);
                       tempWeight = listItem.get(i).getData(2);
                       averageHeight.setText(MONTH+"개월 아이 평균 키 : "+listItem.get(i).getData(1)+"Cm");
                       averageWeight.setText(MONTH+"개월 아이 평균 몸무게 : "+listItem.get(i).getData(2)+"Kg");
                   }
                   if(SEX.equals("여자")) {
                       tempHeight = listItem.get(i).getData(3);
                       tempWeight = listItem.get(i).getData(4);
                       averageHeight.setText(MONTH+"개월 아이 평균 키 : "+listItem.get(i).getData(3)+"Cm");
                       averageWeight.setText(MONTH+"개월 아이 평균 몸무게 : "+listItem.get(i).getData(4)+"Kg");
                   }
                   if (Double.parseDouble(HEIGHT) >= Double.parseDouble(tempHeight) && Double.parseDouble(WEIGHT) >= Double.parseDouble(tempWeight)) {
                       analysisresultHW.setText("분석 결과"+"\n"+"또래 아이보다 키가 크고 몸무게도 많이 나갑니다.");
                   } else if (Double.parseDouble(HEIGHT) >= Double.parseDouble(tempHeight) && Double.parseDouble(WEIGHT) < Double.parseDouble(tempWeight)) {
                       analysisresultHW.setText("분석 결과"+"\n"+"또래 아이보다 키는 크고 몸무게는 적게 나갑니다.");
                   } else if (Double.parseDouble(HEIGHT) < Double.parseDouble(tempHeight) && Double.parseDouble(WEIGHT) >= Double.parseDouble(tempWeight)) {
                       analysisresultHW.setText("분석 결과"+"\n"+"또래 아이보다 키는 작고 몸무게는 많이 나갑니다.");
                   } else {
                       analysisresultHW.setText("분석 결과"+"\n"+"또래 아이보다 키가 작고 몸무게도 적게 나갑니다.");
                   }
               }
            }



        }

    }

}
