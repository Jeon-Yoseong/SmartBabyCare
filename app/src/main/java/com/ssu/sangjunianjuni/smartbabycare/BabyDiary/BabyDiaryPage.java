package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ssu.sangjunianjuni.smartbabycare.AnalysisPage;
import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.MainPage;
import com.ssu.sangjunianjuni.smartbabycare.R;
import com.ssu.sangjunianjuni.smartbabycare.SettingPage;
import com.ssu.sangjunianjuni.smartbabycare.babyboard.BabyBoardPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoseong on 2017-07-04.
 */

public class BabyDiaryPage extends AppCompatActivity {

    // 프로필 사진 불러오기 위한 선언
    private ArrayList<ListItem> photoListItem = new ArrayList<ListItem>();
    PHPDown task;

    // adapter에서 종료 시키기 위해 선언
    public static Activity BabyDiaryPageActivity;

    private String USER_ID;

    // 내부 db에서 육아일기 정보 가져오기
    private BabyDiaryDBHelper babyDiaryDBHelper;
    private ArrayList<BabyDiaryItem> listitem = new ArrayList<BabyDiaryItem>();

    // UI 관련
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DiaryListAdapter adapter;
    private List<BabyDiaryItem> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babydiary_page);

        // adapter에서 종료 시키기 위해 선언
        BabyDiaryPageActivity = BabyDiaryPage.this;

        Intent getUSER_ID = getIntent();
        USER_ID = getUSER_ID.getStringExtra("USER_ID");
        // 글 쓰고 난 뒤 사용자 아이디 받기
        if(USER_ID.equals("")) {
            Intent write_to_page = getIntent();
            USER_ID = write_to_page.getStringExtra("USER_ID");
        }

        //툴바 초기화
        Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar_babydiary);
        toolbar1.setNavigationIcon(R.drawable.ic_drawer_2);
        toolbar1.setTitle("육아일기");
        setSupportActionBar(toolbar1);
        //NavigationDrawer 초기화
        final DrawerLayout drawerlayout1=(DrawerLayout)findViewById(R.id.drawerlayout_babydiary);//??? ??????? ????? drawerlayout
        ActionBarDrawerToggle drawertoggle1=new ActionBarDrawerToggle(this, drawerlayout1, toolbar1, R.string.app_name, R.string.app_name)
        {
            //drawerlayout open시 구동할 함수 있으면 여기에 삽입
        };
        drawerlayout1.setDrawerListener(drawertoggle1);//drawerlayout
        drawertoggle1.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //DrawerLayout 클릭 이벤트 설정
        String[] navItems ={"메인", "분석","게시판", "육아일기", "설정"};//NavigationDrawer???ο?? ????
        ListView drawerlist1=(ListView)findViewById(R.id.drawerlist_diary);
        drawerlist1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        drawerlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent diary_to_main=new Intent(getApplicationContext(), MainPage.class);
                        diary_to_main.putExtra("USER_ID", USER_ID);
                        startActivity(diary_to_main);
                        finish();
                        break;
                    case 1:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent diary_to_analysis=new Intent(getApplicationContext(), AnalysisPage.class);
                        diary_to_analysis.putExtra("USER_ID", USER_ID);
                        startActivity(diary_to_analysis);
                        finish();
                        break;
                    case 2:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent diary_to_board=new Intent(getApplicationContext(), BabyBoardPage.class);
                        diary_to_board.putExtra("USER_ID", USER_ID);
                        startActivity(diary_to_board);
                        finish();
                        break;
                    case 3:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        break;
                    case 4:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent diary_to_setting=new Intent(getApplicationContext(), SettingPage.class);
                        diary_to_setting.putExtra("USER_ID", USER_ID);
                        startActivity(diary_to_setting);
                        finish();
                        break;
                }
            }
        });
        task = new PHPDown();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getmemberinfo.php");

    }

    private class PHPDown extends AsyncTask<String, Integer, String> {

        private String USER_ID_PHP;
        private String PASSWORD;
        private String PHOTO_URI;

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
                    USER_ID_PHP = jo.getString("USER_ID");
                    PASSWORD = jo.getString("PASSWORD");
                    PHOTO_URI = jo.getString("PHOTO_URI");
                    photoListItem.add(new ListItem(USER_ID_PHP, PASSWORD, PHOTO_URI));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // recyclerview에 적용시키기 위해 내부 DB에서 불러온 육아일기 내용과 서버 회원정보에서 불러온 프로필사진 추가

            babyDiaryDBHelper = new BabyDiaryDBHelper(getApplicationContext(), "BABYDIARY.db", null, 1);
            listitem = babyDiaryDBHelper.getInfo(USER_ID);
            System.out.println("USER_ID: "+USER_ID);
            data = new ArrayList<>();
            for(int i = listitem.size() - 1; i >= 0 ; i--) {
                for(int j = 0; j <photoListItem.size(); j++) {
                    if (photoListItem.get(j).getData(0).equals(USER_ID)) {
                        data.add(new BabyDiaryItem(listitem.get(i).getIndex(), listitem.get(i).getUSER_ID(), listitem.get(i).getDate(), listitem.get(i).getTitle(), listitem.get(i).getAuthor(), listitem.get(i).getContent(), listitem.get(i).getDiaryPhoto(), photoListItem.get(j).getData(2)));
                    }
                }
            }

            adapter = new DiaryListAdapter(getApplicationContext() ,data);

            mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView = (RecyclerView) findViewById(R.id.babydiary_recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLinearLayoutManager);

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 게시글 추가 버튼 클릭 시 게시물 작성으로 이동
        if (id == R.id.board_add) {
            Intent intent = new Intent(BabyDiaryPage.this, BabyDiaryWrite.class);
            intent.putExtra("USER_ID", USER_ID);
            startActivity(intent);
            finish();
        }

        // 게시물 검색 클릭 시 게시물 검색으로 이동
        if (id == R.id.board_search) {

        }

        return super.onOptionsItemSelected(item);
    }
}
