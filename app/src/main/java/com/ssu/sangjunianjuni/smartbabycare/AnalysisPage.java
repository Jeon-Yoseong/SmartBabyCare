package com.ssu.sangjunianjuni.smartbabycare;

        import android.content.Intent;
        import android.icu.text.SimpleDateFormat;
        import android.os.Build;
        import android.support.annotation.RequiresApi;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.RelativeLayout;
        import android.widget.Toast;

        import com.ssu.sangjunianjuni.smartbabycare.Analysis.AnalysisSpecificGraphic;
        import com.ssu.sangjunianjuni.smartbabycare.Analysis.HeartBeatAnalysis;
        import com.ssu.sangjunianjuni.smartbabycare.Analysis.HeightWeightAnalysis;
        import com.ssu.sangjunianjuni.smartbabycare.Analysis.PoopAnalysis;
        import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryPage;
        import com.ssu.sangjunianjuni.smartbabycare.babyboard.BabyBoardPage;
        import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.BlueToothDBHelper;
        import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.SmartBandDBHelper;

        import java.util.Date;
        import java.util.StringTokenizer;
//분석 기본 페이지
public class AnalysisPage extends AppCompatActivity {
    private AnalysisSpecificGraphic heartbeatgraphic, poopgraphic;

    private String USER_ID;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_page);

        // 사용자 아이디 저장
        Intent getUSER_ID = getIntent();
        USER_ID = getUSER_ID.getStringExtra("USER_ID");

        Toast.makeText(getApplicationContext(), ""+USER_ID, Toast.LENGTH_SHORT).show();

        //툴바를 액션바 대신 사용
        Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar_analysis);
        toolbar1.setTitle("분석");
        toolbar1.setNavigationIcon(R.drawable.ic_drawer_2);
        setSupportActionBar(toolbar1);

        //DrawerLayout 설정
        final DrawerLayout drawerlayout1=(DrawerLayout)findViewById(R.id.drawerlayout_analysis);
        ActionBarDrawerToggle drawertoggle1=new ActionBarDrawerToggle(this, drawerlayout1, toolbar1, R.string.app_name, R.string.app_name)
        {
            //drawerlayout open시 실행해야 하는 함수 있으면 넣어줄것
        };
        drawerlayout1.setDrawerListener(drawertoggle1);
        drawertoggle1.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DrawerLayout 클릭 이벤트 설정
        String[] navItems ={"메인", "분석","게시판", "육아일기", "설정"};//NavigationDrawer???ο?? ????
        ListView drawerlist1=(ListView)findViewById(R.id.drawerlist_analysis);
        drawerlist1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        drawerlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent analysis_to_main=new Intent(getApplicationContext(), MainPage.class);
                        analysis_to_main.putExtra("USER_ID", USER_ID);
                        startActivity(analysis_to_main);
                        finish();
                        break;
                    case 1:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        break;
                    case 2:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent analysis_to_board=new Intent(getApplicationContext(), BabyBoardPage.class);
                        analysis_to_board.putExtra("USER_ID", USER_ID);
                        startActivity(analysis_to_board);
                        finish();
                        break;
                    case 3:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent analysis_to_diary=new Intent(getApplicationContext(), BabyDiaryPage.class);
                        analysis_to_diary.putExtra("USER_ID", USER_ID);
                        startActivity(analysis_to_diary);
                        finish();
                        break;
                    case 4:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent analysis_to_setting=new Intent(getApplicationContext(), SettingPage.class);
                        analysis_to_setting.putExtra("USER_ID", USER_ID);
                        startActivity(analysis_to_setting);
                        finish();
                        break;
                }
            }
        });
        //각 relativelayout에 그래픽 설정
        heartbeatgraphic=(AnalysisSpecificGraphic)findViewById(R.id.heartbeatAnalysisGuiOutside);
        poopgraphic=(AnalysisSpecificGraphic)findViewById(R.id.poopAnalysisGuiOutside);
        BlueToothDBHelper dbhelper=new BlueToothDBHelper(getApplicationContext(), "poopinfo.db", null, 1);
        SimpleDateFormat timeformat=new SimpleDateFormat("YYYY/MM/dd");
        String time=timeformat.format(new Date(System.currentTimeMillis()));
        int poopcounttoday=dbhelper.getcount(time);
        poopgraphic.getdata(0.0F, 6.0F, 0.5F, 4.0F, (float)poopcounttoday);
        SmartBandDBHelper dbhelper2=new SmartBandDBHelper(getApplicationContext(), "smartinfo.db", null, 1);
        String heart=dbhelper2.getheartbeat();
        StringTokenizer str=new StringTokenizer(heart, "\n");
        heart=str.nextToken();
        int heartbeatrecently=Integer.parseInt(heart.trim());
        heartbeatgraphic.getdata(70.0F, 150.0F, 100.0F, 140.0F, heartbeatrecently);
        //각 relativelayout에 대해 listener 설정, 터치시 상세분석 액티비티로 넘어간다
        RelativeLayout heartbeat=(RelativeLayout)findViewById(R.id.heartbeat);
        RelativeLayout poop=(RelativeLayout)findViewById(R.id.poop);
        RelativeLayout heightweight=(RelativeLayout)findViewById(R.id.heightweight);
        RelativeLayout totalall=(RelativeLayout)findViewById(R.id.totalall);

        heartbeat.setOnClickListener(new View.OnClickListener() {//심박수
            @Override
            public void onClick(View view) {
                drawerlayout1.closeDrawer(GravityCompat.START);
                Intent heartbeatanalysis=new Intent(getApplicationContext(), HeartBeatAnalysis.class);
                startActivity(heartbeatanalysis);
            }
        });

        poop.setOnClickListener(new View.OnClickListener() {//배변
            @Override
            public void onClick(View view) {
                drawerlayout1.closeDrawer(GravityCompat.START);
                Intent poopanalysis=new Intent(getApplicationContext(), PoopAnalysis.class);
                startActivity(poopanalysis);
            }
        });

        heightweight.setOnClickListener(new View.OnClickListener() {//신장체중
            @Override
            public void onClick(View view) {
                drawerlayout1.closeDrawer(GravityCompat.START);
                Intent heartbeatanalysis=new Intent(getApplicationContext(), HeightWeightAnalysis.class);
                heartbeatanalysis.putExtra("USER_ID", USER_ID);
                startActivity(heartbeatanalysis);//시간표 액티비티로 넘어감
            }
        });

        totalall.setOnClickListener(new View.OnClickListener() {//전체분석
            @Override
            public void onClick(View view) {
                drawerlayout1.closeDrawer(GravityCompat.START);
            }
        });
    }
}