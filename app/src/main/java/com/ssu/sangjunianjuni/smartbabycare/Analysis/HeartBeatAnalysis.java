package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.SmartBandDBHelper;
import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.R;

import org.w3c.dom.Text;

import java.util.StringTokenizer;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kang on 2017-05-01.
 */

//심박수 상세분석 액티비티
public class HeartBeatAnalysis extends AppCompatActivity {
    public FrameLayout box;
    public AnalysisSpecificGraphic graphic;

    TextView recentheartbeat, useraverageheartbeat, heartbeatanalysisresult;
    private String fromdb="";
    private ListView heartbeatlistview;
    private HeartBeatAdapter adapter;
    private SmartBandDBHelper dbhelper;
    ArrayList<ListItem> listitem = new ArrayList<ListItem>();
    //private float width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartbeat_analysis_page);
        box=(FrameLayout)findViewById(R.id.heartbeatgraphbox);
        graphic=(AnalysisSpecificGraphic)findViewById(R.id.heartbeatAnalysisGui);
        dbhelper=new SmartBandDBHelper(getApplicationContext(), "smartinfo.db", null, 1);
        String heart=dbhelper.getheartbeat();

        int heartbeatrecently=Integer.parseInt(heart);
        graphic.getdata(70.0F, 150.0F, 100.0F, 140.0F, heartbeatrecently);

        recentheartbeat=(TextView)findViewById(R.id.recentheartbeat);
        recentheartbeat.setText("최근 측정 심박수 : "+Integer.toString(heartbeatrecently));

        useraverageheartbeat=(TextView)findViewById(R.id.usersnormalheartbeat);
        int averageheartbeat=dbhelper.getaverageheartbeat();
        useraverageheartbeat.setText("사용자 평균 심박수 : "+Integer.toString(averageheartbeat));

        heartbeatanalysisresult=(TextView)findViewById(R.id.heartbeatanalysisresult);
        String result="";
        if(heartbeatrecently>100&&heartbeatrecently<140)
            result="심박수가 정상 범위 입니다.";
        else if(heartbeatrecently==-1)
            result="측정된 결과가 없습니다";
        else if (heartbeatrecently < 100)
            result="심박수가 평균 심박수보다 낮습니다.";
        else if (heartbeatrecently > 140)
            result="심박수가 평균 심박수보다 높습니다.";

        heartbeatanalysisresult.setText("분석 결과"+"\n"+result);

        //리스트뷰
        heartbeatlistview=(ListView)findViewById(R.id.heartbeatlist);
        adapter = new HeartBeatAdapter();
        heartbeatlistview.setAdapter(adapter);
        fromdb=dbhelper.getResult();
        StringTokenizer str=new StringTokenizer(fromdb, "\n");
        while(str.hasMoreTokens()){
            String info=str.nextToken();
            StringTokenizer str2=new StringTokenizer(info, "-");
            String tim=str2.nextToken();
            String hear=str2.nextToken();
            listitem.add(new ListItem(tim, hear));
            int i=listitem.size()-1;
            adapter.addItem(listitem.get(i).getData(0), listitem.get(i).getData(1));
        }
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");
        adapter.addItem("2017/09/01 16:59:02", "85");

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
