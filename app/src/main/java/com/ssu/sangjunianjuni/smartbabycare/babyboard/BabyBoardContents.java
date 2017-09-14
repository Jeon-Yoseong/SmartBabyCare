package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.ListItem;
import com.ssu.sangjunianjuni.smartbabycare.PHPRequest;
import com.ssu.sangjunianjuni.smartbabycare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 게시판 내용
 * Created by yoseong on 2017-05-18.
 */

public class BabyBoardContents extends AppCompatActivity {

    // 게시판 제목 시간 내용 설정
    private TextView boardContentTitle;
    private TextView boardContentAuthor;
    private TextView boardContentDate;
    private TextView boardContentContent;

    // 게시판 페이지에서 불러온 아이디와 날짜 저장
    private String BoardUSER_ID;
    private String BoardDate;

    // DB에서 불러온 게시판 내용 저장
    private PHPDown task;
    private ArrayList<ListItem> listItem = new ArrayList<ListItem>();

    // DB에서 불러온 댓글 내용 저장
    private PHPReplyDown replyTask;
    private ArrayList<ListItem> replyListItem = new ArrayList<ListItem>();

    // 댓글 설정 변수
    private EditText replyContents;
    private Button replyRegist;

    // 사용자 아이디
    private String USER_ID;

    // RecyclerView ExpandableListView
    private RecyclerView recyclerView;
    List<BabyBoardReplyExpandableListAdapter.Item> data = new ArrayList<>();
    BabyBoardReplyExpandableListAdapter adapter;

    // alertdialog
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babyboard_contents);

        // 게시판 제목 시간 내용 설정
        boardContentTitle = (TextView) findViewById(R.id.board_content_title);
        boardContentAuthor = (TextView) findViewById(R.id.board_content_author);
        boardContentDate = (TextView) findViewById(R.id.board_content_date);
        boardContentContent = (TextView) findViewById(R.id.board_content_content);

        //php mysql 연결
        // 게시판 글
        task = new PHPDown();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getboardinfo.php");

        // 게시판 글의 댓글
        replyTask = new PHPReplyDown();
        replyTask.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getreplyinfo.php");


        // intent 데이터 저장
        Intent board_to_content = getIntent();
        BoardUSER_ID = board_to_content.getStringExtra("BoardUSER_ID");
        BoardDate = board_to_content.getStringExtra("BoardDate");
        USER_ID = board_to_content.getStringExtra("USER_ID");

        //현재 시각 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String getTime = simpleDateFormat.format(date);

        // 댓글 등록
        replyContents = (EditText) findViewById(R.id.reply_contents);
        replyRegist = (Button) findViewById(R.id.reply_regist);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(BabyBoardContents.this, LinearLayoutManager.VERTICAL, false));

        replyRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 댓글 입력창이 null이 아닐경우 등록
                if (replyContents.getText().toString().length() != 0) {
                    try {
                        PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/replywrite.php");
                        String result = phpRequest.PhPtest(String.valueOf(BoardUSER_ID), String.valueOf(BoardDate), String.valueOf(USER_ID), String.valueOf(getTime), String.valueOf(replyContents.getText()));
                        replyContents.setText("");
                        Toast.makeText(BabyBoardContents.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BabyBoardContents.this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //수정 페이지로 넘어가기
        Button updateBtn = (Button) findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BoardUSER_ID.equals(USER_ID)) {
                    Intent intent = new Intent(BabyBoardContents.this, BabyBoardUpdate.class);
                    intent.putExtra("BoardUSER_ID", BoardUSER_ID);
                    intent.putExtra("BoardDate", BoardDate);
                    intent.putExtra("USER_ID", USER_ID);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BabyBoardContents.this, "본인의 글만 수정이 가능합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //삭제 버튼
        Button deleteBtn = (Button) findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 삭제 시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("게시글 삭제");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (BoardUSER_ID.equals(USER_ID)) {
                                            try {
                                                PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/deleteboard.php");
                                                String result = phpRequest.PhPtest(String.valueOf(boardContentTitle.getText()), String.valueOf(BoardUSER_ID), String.valueOf(BoardDate), String.valueOf(boardContentContent.getText()));
                                                PHPRequest replyRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/deleteAllreply.php");
                                                result = phpRequest.PhPtest(String.valueOf(BoardUSER_ID), String.valueOf(BoardDate), String.valueOf(USER_ID), String.valueOf(getTime), String.valueOf(replyContents.getText()));

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            Intent contents_to_page = new Intent(BabyBoardContents.this, BabyBoardPage.class);
                                            contents_to_page.putExtra("USER_ID", USER_ID);
                                            finish();
                                            startActivity(contents_to_page);
                                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                                        } else {
                                            Toast.makeText(BabyBoardContents.this, "본인의 글만 삭제가 가능합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                // alertdialog 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // alertdialog 보여주기
                alertDialog.show();

            }
        });
    }

//php mysql 연동
private class PHPDown extends AsyncTask<String, Integer, String> {

    private int RegisterNumber;
    private String Title;
    private String Author;
    private String Date;
    private String Context;

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

        for (int i = 0; i < listItem.size(); i++) {
            if (listItem.get(i).getData(2).equals(BoardUSER_ID) && listItem.get(i).getData(3).equals(BoardDate)) {
                boardContentTitle.setText(listItem.get(i).getData(1));
                boardContentAuthor.setText(listItem.get(i).getData(2));
                boardContentDate.setText(listItem.get(i).getData(3));
                boardContentContent.setText(listItem.get(i).getData(4));
            }
        }

    }
}

// php mysql reply 연동
private class PHPReplyDown extends AsyncTask<String, Integer, String> {

    private String ContentAuthor;
    private String ContentDate;
    private String ReplyAuthor;
    private String ReplyDate;
    private String ReplyContext;

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
                ContentAuthor = jo.getString("ContentAuthor");
                ContentDate = jo.getString("ContentDate");
                ReplyAuthor = jo.getString("ReplyAuthor");
                ReplyDate = jo.getString("ReplyDate");
                ReplyContext = jo.getString("ReplyContext");
                replyListItem.add(new ListItem(ContentAuthor, ContentDate, ReplyAuthor, ReplyDate, ReplyContext));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.add(new BabyBoardReplyExpandableListAdapter.Item(BabyBoardReplyExpandableListAdapter.HEADER, "댓글"));

        for (int i = 0; i < replyListItem.size() ; i++) {
            if(!replyListItem.isEmpty()){
                if (replyListItem.get(i).getData(0).equals(BoardUSER_ID) && replyListItem.get(i).getData(1).equals(BoardDate)) {
                    data.add(new BabyBoardReplyExpandableListAdapter.Item(BabyBoardReplyExpandableListAdapter.CHILD, replyListItem.get(i).getData(4), replyListItem.get(i).getData(3), replyListItem.get(i).getData(2)));
                }
            }
        }
        // adapter로 recyclerview 연결
        adapter = new BabyBoardReplyExpandableListAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
    // back 버튼 클릭 시 게시판 리스트로 돌아가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BabyBoardContents.this, BabyBoardPage.class);
        intent.putExtra("USER_ID", USER_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

}
