package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;

/**
 * 게시판 글 수정
 * Created by yoseong on 2017-06-23.
 */

public class BabyBoardUpdate extends AppCompatActivity {

    // 수정할 글 정보 받아오기
    private String BoardUSER_ID;
    private String BoardDate;
    private String USER_ID;

    // 게시글 제목, 내용 설정
    private EditText boardTitle;
    private EditText boardContents;

    // 수정, 취소 버튼
    private Button update;
    private Button cancel;

    // DB에서 받아온 정보 저장
    private ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    private PHPDown task;

    // alertdialog 셋팅
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babyboard_update);

        // 수정할 글 정보 받아오기
        Intent content_to_update= getIntent();
        BoardUSER_ID = content_to_update.getStringExtra("BoardUSER_ID");
        BoardDate = content_to_update.getStringExtra("BoardDate");
        USER_ID = content_to_update.getStringExtra("USER_ID");

        // 제목과 내용 셋팅
        boardTitle = (EditText) findViewById(R.id.board_title_update);
        boardContents = (EditText) findViewById(R.id.board_context_update);

        // 수정 후 DB 저장
        update = (Button) findViewById(R.id.btn_Board_Register_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 수정 시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("게시글 수정");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("수정하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("수정",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/updateboard.php");
                                            String result = phpRequest.PhPtest(String.valueOf(boardTitle.getText()), String.valueOf(BoardUSER_ID), String.valueOf(BoardDate), String.valueOf(boardContents.getText()));

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(BabyBoardUpdate.this, "게시글 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        Intent update_to_content = new Intent(BabyBoardUpdate.this, BabyBoardContents.class);
                                        update_to_content.putExtra("BoardUSER_ID", BoardUSER_ID);
                                        update_to_content.putExtra("BoardDate", BoardDate);
                                        update_to_content.putExtra("USER_ID", USER_ID);
                                        startActivity(update_to_content);
                                        finish();
                                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
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

        // 취소 클릭
        cancel = (Button) findViewById(R.id.btn_Board_Cancel_update);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 취소버튼 클릭시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("게시글 수정 취소");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("취소 버튼 클릭 시 변경사항이 저장되지 않습니다. 취소하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent update_to_content = new Intent(BabyBoardUpdate.this, BabyBoardContents.class);
                                        update_to_content.putExtra("BoardUSER_ID", BoardUSER_ID);
                                        update_to_content.putExtra("BoardDate", BoardDate);
                                        update_to_content.putExtra("USER_ID", USER_ID);
                                        startActivity(update_to_content);
                                        finish();
                                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                                    }
                                })
                        .setNegativeButton("아니오",
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

        // php연결 해 DB에서 정보 저장할 때 필요한 코드
        task = new PHPDown();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getboardinfo.php");
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
                    boardTitle.setText(listItem.get(i).getData(1));
                    boardContents.setText(listItem.get(i).getData(4));
                }
            }

        }
    }

    // 백버튼 클릭 시
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            // 수정 시 back 버튼 클릭시 alertdialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // 제목 셋팅
            alertDialogBuilder.setTitle("게시글 수정 취소");

            // alertdialog 셋팅
            alertDialogBuilder
                    .setMessage("Back 버튼 클릭 시 변경사항이 저장되지 않습니다. 취소하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent update_to_content = new Intent(BabyBoardUpdate.this, BabyBoardContents.class);
                                    update_to_content.putExtra("BoardUSER_ID", BoardUSER_ID);
                                    update_to_content.putExtra("BoardDate", BoardDate);
                                    update_to_content.putExtra("USER_ID", USER_ID);
                                    startActivity(update_to_content);
                                    finish();
                                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                                }
                            })
                    .setNegativeButton("아니오",
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
        return false;
    }
}
