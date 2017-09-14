package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;


/**
 * Created by yoseong on 2017-07-04.
 */

public class BabyDiaryContents extends AppCompatActivity {

    private String USER_ID;
    private String Author;
    private String Date;
    private String Title;
    private String Content;
    private String DiaryPhoto;

    private TextView diaryAuthor;
    private TextView diaryDate;
    private TextView diaryTitle;
    private TextView diaryContent;
    private ImageView diaryPhoto;

    private Button diaryUpdate;
    private Button diaryDelete;

    private Context context = this;

    // 수정, 삭제를 위한 DB
    private BabyDiaryDBHelper babyDiaryDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babydiary_contents);

        Intent page_to_content = getIntent();
        Author = page_to_content.getStringExtra("Author");
        USER_ID = page_to_content.getStringExtra("USER_ID");
        Date = page_to_content.getStringExtra("Date");
        Title = page_to_content.getStringExtra("Title");
        Content = page_to_content.getStringExtra("Content");
        DiaryPhoto = page_to_content.getStringExtra("DiaryPhoto");

        diaryAuthor = (TextView) findViewById(R.id.diary_content_author);
        diaryDate = (TextView) findViewById(R.id.diary_content_date);
        diaryTitle = (TextView) findViewById(R.id.diary_content_title);
        diaryContent = (TextView) findViewById(R.id.diary_content_content);
        diaryPhoto = (ImageView) findViewById(R.id.diary_content_image);

        diaryAuthor.setText(Author);
        diaryDate.setText(Date);
        diaryTitle.setText(Title);
        diaryContent.setText(Content);
        diaryPhoto.setImageURI(Uri.parse(DiaryPhoto));

        babyDiaryDBHelper = new BabyDiaryDBHelper(getApplicationContext(), "BABYDIARY.db", null, 1);

        // 일기 수정 버튼
        diaryUpdate = (Button) findViewById(R.id.diary_update);
        diaryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent content_to_update = new Intent(BabyDiaryContents.this, BabyDiaryUpdate.class);
                content_to_update.putExtra("USER_ID", USER_ID);
                content_to_update.putExtra("Date", Date);
                content_to_update.putExtra("Title", Title);
                content_to_update.putExtra("Author", Author);
                content_to_update.putExtra("Content", Content);
                content_to_update.putExtra("DiaryPhoto", DiaryPhoto);
                startActivity(content_to_update);
                finish();
            }
        });

        // 일기 삭제 버튼
        diaryDelete = (Button) findViewById(R.id.diary_delete);
        diaryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("육아일기 삭제");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        babyDiaryDBHelper.delete(Date);
                                        Intent content_to_page = new Intent(BabyDiaryContents.this, BabyDiaryPage.class);
                                        content_to_page.putExtra("USER_ID", USER_ID);
                                        startActivity(content_to_page);
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
    }

    // back 버튼 클릭 시 게시판 리스트로 돌아가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BabyDiaryContents.this, BabyDiaryPage.class);
        intent.putExtra("USER_ID", USER_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

}
