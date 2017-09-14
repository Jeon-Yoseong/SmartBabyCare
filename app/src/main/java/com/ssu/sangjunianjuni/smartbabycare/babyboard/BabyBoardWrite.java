package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.NetworkUtil;
import com.ssu.sangjunianjuni.smartbabycare.PHPRequest;
import com.ssu.sangjunianjuni.smartbabycare.R;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 게시판 글 작성
 * Created by yoseong on 2017-05-02.
 */

public class BabyBoardWrite extends AppCompatActivity {

    private EditText boardTitle;
    private EditText boardContext;
    private Button registContent;
    private Button cancelContent;

    private String USER_ID;

    private Context context = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babyboard_write);
        NetworkUtil.setNetworkPolicy();



        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("USER_ID");

        //현재 시각 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String getTime = simpleDateFormat.format(date);

        boardTitle = (EditText) findViewById(R.id.board_title);
        boardContext = (EditText) findViewById(R.id.board_context);

        registContent = (Button) findViewById(R.id.btnBoardRegister);

        // 게시글 등록 버튼 클릭
        registContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 제목을 입력했을때
                if ((boardTitle.getText().toString().length()) != 0 && (boardContext.getText().toString().length() != 0)) {
                    // 저장 확인 alertdialog
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // 제목 셋팅
                    alertDialogBuilder.setTitle("게시글 등록");

                    // alertdialog 셋팅
                    alertDialogBuilder
                            .setMessage("게시글을 등록하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //서버에 저장
                                            try {
                                                PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/boardwrite.php");
                                                // flag가 0이면 글쓴이가 돼서 알람이 울리지 않도록 한다.
                                                String result = phpRequest.PhPtest(String.valueOf(boardTitle.getText()), String.valueOf(USER_ID), String.valueOf(getTime), String.valueOf(boardContext.getText()), String.valueOf("author"), Integer.valueOf(0));

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            Intent write_to_board = new Intent(BabyBoardWrite.this, BabyBoardPage.class);
                                            write_to_board.putExtra("USER_ID", USER_ID);
                                            startActivity(write_to_board);
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
                } else if (boardTitle.getText().toString().length() == 0) {
                    Toast.makeText(BabyBoardWrite.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (boardContext.getText().toString().length() == 0) {
                    Toast.makeText(BabyBoardWrite.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 취소 버튼 클릭
        cancelContent = (Button) findViewById(R.id.btnBoardCancel);
        cancelContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 취소버튼 클릭시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("게시글 등록 취소");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("취소 버튼 클릭 시 게시글이 등록되지 않습니다. 취소하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(BabyBoardWrite.this, BabyBoardPage.class);
                                        intent.putExtra("USER_ID", USER_ID);
                                        startActivity(intent);
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
    }

    // back 버튼 클릭 시 게시판 리스트로 돌아가기
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            // 게시글 등록 시 back 버튼 클릭시 alertdialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // 제목 셋팅
            alertDialogBuilder.setTitle("게시글 등록 취소");

            // alertdialog 셋팅
            alertDialogBuilder
                    .setMessage("Back 버튼 클릭 시 게시글이 등록되지 않습니다. 취소하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(BabyBoardWrite.this, BabyBoardPage.class);
                                    intent.putExtra("USER_ID", USER_ID);
                                    startActivity(intent);
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
