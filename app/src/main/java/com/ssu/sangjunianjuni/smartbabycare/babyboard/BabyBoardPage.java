package com.ssu.sangjunianjuni.smartbabycare.babyboard;

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
import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryPage;
import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.MainPage;
import com.ssu.sangjunianjuni.smartbabycare.R;
import com.ssu.sangjunianjuni.smartbabycare.SettingPage;

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
 * 게시판 리스트
 * Created by yoseong on 2017-04-29.
 */

public class BabyBoardPage extends AppCompatActivity {

    // adapter에서 종료 시키기 위해 선언
    public static Activity BabyBoardPageActivity;

    private ListView babyBoardListView;
    private BabyBoardAdapter adapter;

    private String USER_ID;

    // UI 관련------------------------------------------------
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private BoardListAdapter recyclerAdapter;
    private List<BabyBoardItem> data;
    private List<BabyBoardItem> profile_data;


    // DB에서 불러온 게시판 내용 저장
//    PHPDown task;
//    PHPProfileDown taskPhoto;
    PHODownPhoto task;
    ArrayList<ListItem> listItem = new ArrayList<ListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babyboard_page);

        // adapter에서 종료 시키기 위해 선언
        BabyBoardPageActivity = BabyBoardPage.this;

        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("USER_ID");
        // 글 쓰고 난 뒤 사용자 아이디 받기
        if(USER_ID.equals("")) {
            Intent write_to_page = getIntent();
            USER_ID = write_to_page.getStringExtra("USER_ID");
        }

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar_babyboard);
        toolbar1.setNavigationIcon(R.drawable.ic_drawer_2);
        toolbar1.setTitle("육아 게시판");
        setSupportActionBar(toolbar1);

        //NavigationDrawer 설정
        final DrawerLayout drawerlayout1 = (DrawerLayout) findViewById(R.id.drawerlayout_babyboard);//각 액티비티마다 drawerlayout 지정 필요
        ActionBarDrawerToggle drawertoggle1 = new ActionBarDrawerToggle(this, drawerlayout1, toolbar1, R.string.app_name, R.string.app_name) {
            ///여기는 drawerlayout open시 실행해야 하는 함수 있으면 넣어줄것!
        };
        drawerlayout1.setDrawerListener(drawertoggle1);//drawerlayout 리스너 지정
        drawertoggle1.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DrawerLayout 클릭 이벤트 설정
        String[] navItems ={"메인", "분석","게시판", "육아일기", "설정"};//NavigationDrawer???ο?? ????
        ListView drawerlist1=(ListView)findViewById(R.id.drawerlist_babyboard);
        drawerlist1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        drawerlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent board_to_main=new Intent(getApplicationContext(), MainPage.class);
                        board_to_main.putExtra("USER_ID", USER_ID);
                        startActivity(board_to_main);
                        finish();
                        break;
                    case 1:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent board_to_analysis=new Intent(getApplicationContext(), AnalysisPage.class);
                        board_to_analysis.putExtra("USER_ID", USER_ID);
                        startActivity(board_to_analysis);
                        finish();
                        break;
                    case 2:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        break;
                    case 3:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent board_to_diary=new Intent(getApplicationContext(), BabyDiaryPage.class);
                        board_to_diary.putExtra("USER_ID", USER_ID);
                        startActivity(board_to_diary);
                        finish();
                        break;
                    case 4:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent board_to_setting=new Intent(getApplicationContext(), SettingPage.class);
                        board_to_setting.putExtra("USER_ID", USER_ID);
                        startActivity(board_to_setting);
                        finish();
                        break;
                }
            }
        });
        babyBoardListView = (ListView) findViewById(R.id.babyBoardListview);

        // JOIN을 사용해 프로필 사진까지 불러오는 php 불러오기
        task = new PHODownPhoto();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getmemberphoto.php");

//        task = new PHPDown();
//        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getboardinfo.php");

//        taskPhoto = new PHPProfileDown();
//        taskPhoto.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getmemberinfo.php");

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

            Intent intent = new Intent(BabyBoardPage.this, BabyBoardWrite.class);
            intent.putExtra("USER_ID", USER_ID);
            startActivity(intent);
            finish();
        }
/*
        // 게시물 검색 클릭 시 게시물 검색으로 이동
        if (id == R.id.board_search) {

        }
*/
        return super.onOptionsItemSelected(item);
    }
/*
    private class PHPDown extends AsyncTask<String, Integer, String> {

        private int RegisterNumber;
        private String Title;
        private String Author;
        private String Date;
        private String Context;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            try{
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
        protected void onPostExecute(String str) {
            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    RegisterNumber = jo.getInt("RegisterNumber");
                    Title = jo.getString("Title");
                    Author = jo.getString("Author");
                    Date = jo.getString("Date");
                    Context = jo.getString("Context");
                    listItem.add(new ListItem(String.valueOf(RegisterNumber), Title, Author, Date, Context));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
            adapter = new BabyBoardAdapter();
            babyBoardListView.setAdapter(adapter);

            // 역순으로 게시판 리스트를 뜨게 하기 위해서 이런식으로 함
            for (int i = listItem.size() - 1; i >= 0 ; i--) {
                adapter.addItem(listItem.get(i).getData(1), listItem.get(i).getData(2), listItem.get(i).getData(3));
            }

            //게시글 클릭
            babyBoardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //리스트뷰 아이템 전체의 사이즈
                    int tmpsize = listItem.size();

                    Intent intent = new Intent(BabyBoardPage.this, BabyBoardContents.class);
                    intent.putExtra("BoardUSER_ID", listItem.get(tmpsize-position-1).getData(2));
                    intent.putExtra("BoardDate", listItem.get(tmpsize-position-1).getData(3));
                    intent.putExtra("USER_ID", USER_ID);
                    startActivity(intent);
                    finish();
                }
            });
*/
            /**
             * RecyclerView를 이용해 list 구성
             */
 /*           data = new ArrayList<>();
            for(int i = listItem.size() - 1; i >= 0 ; i--) {
                data.add(new BabyBoardItem(listItem.get(i).getData(1), listItem.get(i).getData(2), listItem.get(i).getData(3), listItem.get(i).getData(4), USER_ID));
            }

//            recyclerAdapter = new BoardListAdapter(getApplicationContext() ,data, profile_data);
            recyclerAdapter = new BoardListAdapter(getApplicationContext(), data);
            mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView = (RecyclerView) findViewById(R.id.babyboard_recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLinearLayoutManager);

            recyclerView.setAdapter(recyclerAdapter);
//            adapter.notifyDataSetChanged();


        }

    }*/

    /**
     * JOIN 을 사용해 프로필 사진까지 불러옴
     */
    private class PHODownPhoto extends AsyncTask<String, Integer, String> {

        private int RegisterNumber;
        private String Title;
        private String Author;
        private String Date;
        private String Context;
        private String PHOTO_URI;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            try{
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
        protected void onPostExecute(String str) {
            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    RegisterNumber = jo.getInt("RegisterNumber");
                    Title = jo.getString("Title");
                    Author = jo.getString("Author");
                    Date = jo.getString("Date");
                    Context = jo.getString("Context");
                    PHOTO_URI = jo.getString("PHOTO_URI");
                    listItem.add(new ListItem(String.valueOf(RegisterNumber), Title, Author, Date, Context, PHOTO_URI));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**
             * RecyclerView를 이용해 list 구성
             */
            data = new ArrayList<>();
            for(int i = listItem.size() - 1; i >= 0 ; i--) {
                data.add(new BabyBoardItem(listItem.get(i).getData(1), listItem.get(i).getData(2), listItem.get(i).getData(3), listItem.get(i).getData(4), USER_ID, listItem.get(i).getData(5)));
            }

            recyclerAdapter = new BoardListAdapter(getApplicationContext(), data);
            mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView = (RecyclerView) findViewById(R.id.babyboard_recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLinearLayoutManager);

            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.getRecycledViewPool().setMaxRecycledViews(5, 10);
//            adapter.notifyDataSetChanged();


        }
    }

}