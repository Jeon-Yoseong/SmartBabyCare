package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.BlueToothDBHelper;
import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by yoseong on 2017-05-02.
 */

public class PoopAnalysis extends AppCompatActivity {

    public FrameLayout box;
    public AnalysisSpecificGraphic graphic;
    TextView recentpoop, useraveragepoop, poopanalysisresult;
    private String fromdb="";


    private BlueToothDBHelper dbhelper;
    ArrayList<ListItem> listitem = new ArrayList<ListItem>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poop_analysis_page);

        box=(FrameLayout)findViewById(R.id.poopgraphbox);
        graphic=(AnalysisSpecificGraphic)findViewById(R.id.poopAnalysisGui);

        dbhelper=new BlueToothDBHelper(getApplicationContext(), "poopinfo.db", null, 1);
        SimpleDateFormat timeformat=new SimpleDateFormat("YYYY/MM/dd");
        SimpleDateFormat timeformat2=new SimpleDateFormat("HH");
        String time=timeformat.format(new Date(System.currentTimeMillis()));
        String timehour=timeformat2.format(new Date(System.currentTimeMillis()));

        int poopcounttoday=dbhelper.getcount(time);
        graphic.getdata(0.0F, 6.0F, 0.5F, 4.0F, (float)poopcounttoday, "일일 배변량");
        //addgraph(box, graphic);

        recentpoop=(TextView)findViewById(R.id.recentpoop);
        poopanalysisresult=(TextView)findViewById(R.id.poopanalysisresult);
        recentpoop.setText("최근 배변 시간:"+dbhelper.getrecent());

        useraveragepoop=(TextView)findViewById(R.id.usersnormalpoop);
        int averagepoop=dbhelper.getaveragepoop();
        useraveragepoop.setText("사용자 평균 일 배변량:"+Integer.toString(averagepoop));

        String result="";
        if(poopcounttoday>0.5&&poopcounttoday<4) {
            result = "정상입니다";
        } else{
            if(Integer.parseInt(timehour)<=20&&poopcounttoday<6){
                Toast.makeText(getApplicationContext(), "time to wait", Toast.LENGTH_SHORT).show();
                result="하루가 기니 차분히 기다려 보세요";
            }else{
                Toast.makeText(getApplicationContext(), "time to hospital", Toast.LENGTH_SHORT).show();
                result="병원에 가 보세요 배탈입니다";
            }
        }

        poopanalysisresult.setText("분석 결과"+"\n"+result);

        // 오늘 날짜 구하기
        SimpleDateFormat today1 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(); // 오늘날짜
        String todayDate = today1.format(calendar.getTime());

        // 해당 요일별 그래프에 추가되는 데이터의 숫자 설정
        int graphSize = 0;
        String todayStr = null;
        try {
            todayStr = getDateDay(todayDate, "yyyy/MM/dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (todayStr) {
            case "일":
                graphSize = 1;
                break;
            case "월":
                graphSize = 2;
                break;
            case "화":
                graphSize = 3;
                break;
            case "수":
                graphSize = 4;
                break;
            case "목":
                graphSize = 5;
                break;
            case "금":
                graphSize = 6;
                break;
            case "토":
                graphSize = 7;
                break;
        }

        // 전체 배변량 리스트로 저장
        ArrayList<String> poopList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(dbhelper.getResult(), "||\n");
        while(st.hasMoreElements()) {
            poopList.add(st.nextToken());
            st.nextToken();
        }
        // 오늘부터 지난주 일요일까지 배변량 저장
        int[] poopDayCount = new int[graphSize];
        int flag = 0;
        String tempDate = todayDate;
        for (int i = poopList.size()-1; i >= 0; i--) {
            if(tempDate.equals(poopList.get(i))) {
                poopDayCount[flag]++;
            } else {
                tempDate = poopList.get(i);
                flag++;
                if(flag == graphSize) {
                    break;
                } else {
                    poopDayCount[flag]++;
                }

            }
        }

        // 그래프 예제
        // 오늘 날짜 불러오기
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        final java.text.SimpleDateFormat curDay = new java.text.SimpleDateFormat("dd", Locale.KOREA);
        final java.text.SimpleDateFormat curMonth = new java.text.SimpleDateFormat("MM", Locale.KOREA);
        final String month = curMonth.format(date);
        final String day= curDay.format(date);


        ArrayList<String>labels = new ArrayList<String>();
        /*for(int i = 1; i <= Integer.parseInt(day); i++) {
            labels.add(String.valueOf(i));
            System.out.println("day: "+String.valueOf(i));
        }*/
        labels.add("일");
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");

        ArrayList<BarEntry> entries = new ArrayList<>();
        /*
        entries.add(new BarEntry(6, 0));
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(7, 2));
        entries.add(new BarEntry(2, 3));
        entries.add(new BarEntry(3, 4));
        entries.add(new BarEntry(5, 5));
        entries.add(new BarEntry(1, 6));
        */
        // 오늘부터 지난주 일요일까지 배변 횟수 저장
        int j = 0;
        for(int i = poopDayCount.length -1 ; i >= 0 ; i--) {
            entries.add(new BarEntry(poopDayCount[i], j));
            j++;
        }
        // 나머지 0으로 초기화화
        for(int i = j + 1; i < 7; i++) {
            entries.add(new BarEntry(0, j));
        }

        // x축 정렬
        Collections.sort(entries, new Comparator<BarEntry>() {
            @Override
            public int compare(BarEntry t0, BarEntry t1) {
                return String.valueOf(t0.getXIndex()).compareTo(String.valueOf(t1.getXIndex()));
            }
        });

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        BarDataSet barDataSet = new BarDataSet(entries, "배변 횟수");

        BarData barData = new BarData(labels, barDataSet);

        // y축 왼쪽 label이 정수로 보이게
        barChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        // y축 오른쪽 라벨 안보이게
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawLabels(false);

        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        barDataSet.setColor(Color.rgb(240,215,210));
        barChart.setData(barData);


    }

    // 날짜에 해당하는 요일 구하기
    public static String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            finish();//액티비티 종료, 분석 액티비티로 돌아감
        }
        return false;
    }
}