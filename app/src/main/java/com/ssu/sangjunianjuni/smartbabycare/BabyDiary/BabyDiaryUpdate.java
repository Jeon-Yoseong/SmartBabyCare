package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.R;

/**
 * Created by yoseong on 2017-07-07.
 */

public class BabyDiaryUpdate extends AppCompatActivity {

    Context context = this;

    // 기본 선언
    private String USER_ID;

    private String Date;
    private String Title;
    private String Content;
    private String DiaryPhoto;

    private TextView diaryAuthor;
    private TextView diaryDate;
    private TextView diaryTitle;
    private TextView diaryContent;
    private ImageView diaryPhoto;

    private Button update;
    private Button cancel;

    // 수정을 위한 DB 선언
    private BabyDiaryDBHelper babyDiaryDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babydiary_update);

        // 수정할 육아일기 내용 받아오기
        Intent content_to_update = getIntent();
        USER_ID = content_to_update.getStringExtra("USER_ID");
        Date = content_to_update.getStringExtra("Date");
        Title = content_to_update.getStringExtra("Title");
        Content = content_to_update.getStringExtra("Content");
        DiaryPhoto = content_to_update.getStringExtra("DiaryPhoto");

        // 기존의 내용 셋팅
        diaryTitle = (TextView) findViewById(R.id.diary_update_title);
        diaryContent = (TextView) findViewById(R.id.diary_update_context);
        diaryPhoto = (ImageView) findViewById(R.id.diary_update_photo);

        diaryTitle.setText(Title);
        diaryContent.setText(Content);
        diaryPhoto.setImageURI(Uri.parse(DiaryPhoto));

        // DB 선언
        babyDiaryDBHelper = new BabyDiaryDBHelper(getApplicationContext(), "BABYDIARY.db", null, 1);

        // 수정 후 DB 저장
        update = (Button) findViewById(R.id.diary_update_register);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 수정 시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("육아일기 수정");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("수정하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("수정",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Title = diaryTitle.getText().toString();
                                        Content = diaryContent.getText().toString();
                                        babyDiaryDBHelper.update(Date, Title, Content, DiaryPhoto);
                                        Toast.makeText(BabyDiaryUpdate.this, "육아일기 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        Intent update_to_content = new Intent(BabyDiaryUpdate.this, BabyDiaryContents.class);
                                        update_to_content.putExtra("Date", Date);
                                        update_to_content.putExtra("Title", Title);
                                        update_to_content.putExtra("Content", Content);
                                        update_to_content.putExtra("DiaryPhoto", DiaryPhoto);
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
        cancel = (Button) findViewById(R.id.diary_update_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 취소버튼 클릭시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("육아일기 수정 취소");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("취소 버튼 클릭 시 변경사항이 저장되지 않습니다. 취소하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent update_to_content = new Intent(BabyDiaryUpdate.this, BabyDiaryContents.class);
                                        update_to_content.putExtra("Date", Date);
                                        update_to_content.putExtra("Title", Title);
                                        update_to_content.putExtra("Content", Content);
                                        update_to_content.putExtra("DiaryPhoto", DiaryPhoto);
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

    }

    // 백버튼 클릭 시
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            // 수정 시 back 버튼 클릭시 alertdialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // 제목 셋팅
            alertDialogBuilder.setTitle("육아일기 수정 취소");

            // alertdialog 셋팅
            alertDialogBuilder
                    .setMessage("Back 버튼 클릭 시 변경사항이 저장되지 않습니다. 취소하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent update_to_content = new Intent(BabyDiaryUpdate.this, BabyDiaryContents.class);
                                    update_to_content.putExtra("Date", Date);
                                    update_to_content.putExtra("Title", Title);
                                    update_to_content.putExtra("Content", Content);
                                    update_to_content.putExtra("DiaryPhoto", DiaryPhoto);
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
