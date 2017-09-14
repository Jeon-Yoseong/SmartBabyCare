package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by kang on 2017-05-02.
 */

public class PoopAnalysis extends AppCompatActivity {

    public FrameLayout box;
    public AnalysisSpecificGraphic graphic;
    TextView recentpoop, useraveragepoop, poopanalysisresult;
    private String fromdb="";
    private ListView pooplistview;
    private PoopAdapter adapter;
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
        graphic.getdata(0.0F, 6.0F, 0.5F, 4.0F, (float)poopcounttoday);
        //addgraph(box, graphic);

        recentpoop=(TextView)findViewById(R.id.recentpoop);
        poopanalysisresult=(TextView)findViewById(R.id.poopanalysisresult);
        recentpoop.setText("최근 배변 시간:"+dbhelper.getrecent());

        useraveragepoop=(TextView)findViewById(R.id.usersnormalpoop);
        int averagepoop=dbhelper.getaveragepoop();
        useraveragepoop.setText("사용자 평균 일 배변량:"+Integer.toString(averagepoop));

        String result="";
        if(poopcounttoday>0.5&&poopcounttoday<4)
            result="정상입니다";
        else if(Integer.parseInt(timehour)<=20){
            Toast.makeText(getApplicationContext(), "time to wait", Toast.LENGTH_SHORT).show();
            result="하루가 기니 차분히 기다려 보세요";
        }
        else{
            Toast.makeText(getApplicationContext(), "time to hospital", Toast.LENGTH_SHORT).show();
            result="병원에 가 보세요";
        }
        poopanalysisresult.setText("분석 결과"+"\n"+result);


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
        entries.add(new BarEntry(6, 0));
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(7, 2));
        entries.add(new BarEntry(2, 3));
        entries.add(new BarEntry(3, 4));
        entries.add(new BarEntry(5, 5));
        entries.add(new BarEntry(1, 6));



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
/*
        //리스트뷰
        pooplistview=(ListView)findViewById(R.id.pooplist);
        adapter = new PoopAdapter();
        pooplistview.setAdapter(adapter);
        fromdb=dbhelper.getResult();
        StringTokenizer str=new StringTokenizer(fromdb, "\n");
        while(str.hasMoreTokens()){
            String tim=str.nextToken();
            //Toast.makeText(getApplicationContext(), title+" "+time+" "+daily+" "+onoff, Toast.LENGTH_SHORT).show();
            listitem.add(new ListItem(tim));
            int i=listitem.size()-1;
            adapter.addItem(listitem.get(i).getData(0));
        }
*/

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
