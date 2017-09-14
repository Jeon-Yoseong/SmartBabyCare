package com.ssu.sangjunianjuni.smartbabycare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryPage;
import com.ssu.sangjunianjuni.smartbabycare.babyboard.BabyBoardPage;

/**
 * Created by kang on 2017-07-17.
 */

public class SettingPage extends AppCompatActivity {

    private String USER_ID;
    private SharedPreferences sharedPreferences;
    private CheckBox setalarmcheckbox;
    private Switch setpushalarmswitch;
    private TextView setalarmtext, setpushalarmtext;
    private RelativeLayout changebabyinfo, setalarmsound;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        //툴바를 액션바 대신 사용
        Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar_setting);
        toolbar1.setTitle("분석");
        toolbar1.setNavigationIcon(R.drawable.ic_drawer_2);
        setSupportActionBar(toolbar1);

        // 사용자 아이디 저장
        Intent getUSER_ID = getIntent();
        USER_ID = getUSER_ID.getStringExtra("USER_ID");

        //DrawerLayout 설정
        final DrawerLayout drawerlayout1=(DrawerLayout)findViewById(R.id.drawerlayout_setting);
        ActionBarDrawerToggle drawertoggle1=new ActionBarDrawerToggle(this, drawerlayout1, toolbar1, R.string.app_name, R.string.app_name)
        {
            //drawerlayout open시 실행해야 하는 함수 있으면 넣어줄것
        };
        drawerlayout1.setDrawerListener(drawertoggle1);
        drawertoggle1.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[] navItems ={"메인", "분석","게시판", "육아일기", "설정"};//NavigationDrawer???ο?? ????
        ListView drawerlist1=(ListView)findViewById(R.id.drawerlist_setting);
        drawerlist1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        drawerlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent setting_to_main=new Intent(getApplicationContext(), MainPage.class);
                        setting_to_main.putExtra("USER_ID", USER_ID);
                        startActivity(setting_to_main);
                        finish();
                        break;
                    case 1:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent setting_to_analysis=new Intent(getApplicationContext(), AnalysisPage.class);
                        setting_to_analysis.putExtra("USER_ID", USER_ID);
                        startActivity(setting_to_analysis);
                        finish();
                        break;
                    case 2:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent setting_to_board=new Intent(getApplicationContext(), BabyBoardPage.class);
                        setting_to_board.putExtra("USER_ID", USER_ID);
                        startActivity(setting_to_board);
                        finish();
                        break;
                    case 3:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent setting_to_diary=new Intent(getApplicationContext(), BabyDiaryPage.class);
                        setting_to_diary.putExtra("USER_ID", USER_ID);
                        startActivity(setting_to_diary);
                        finish();
                        break;
                    case 4:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });

        //마시멜로 이후 버전부터는 WRITE_SETTINGS permission에 대해서 런타임에 사용자로부터 권한을 인가받도록 해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                Toast.makeText(this, "onCreate: Already Granted", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                AlertDialog dialog=new AlertDialog.Builder(SettingPage.this)
                        .setTitle("권한 설정")
                        .setMessage("사용자 권한이 필요합니다")
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        startActivity(intent);
                    }
                });
            }
        }

        //처음 실행시 프레퍼런스에서 데이터를 가져와 체크박스 등을 설정한다
        sharedPreferences=getSharedPreferences("setting", MODE_PRIVATE);
        String setalarm=sharedPreferences.getString("alarm", "");
        String setpushalarm=sharedPreferences.getString("pushalarm", "");


        //체크박스에 따라 setalarmtext는 변한다.
        setalarmtext=(TextView)findViewById(R.id.setalarmtext);
        //프리퍼런스에서 가져온 값으로 체크박스 초기 설정
        setalarmcheckbox=(CheckBox)findViewById(R.id.setalarmcheckBox);
        if(setalarm.equals("on")) {
            setalarmcheckbox.setChecked(true);
            setalarmtext.setText("벨소리를 사용합니다");
        }
        else {
            setalarmcheckbox.setChecked(false);
            setalarmtext.setText("진동을 사용합니다");
        }

        AudioManager maudiomanager=(AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        //알람 on/off 체크박스 리스너
        setalarmcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences=getSharedPreferences("setting", MODE_PRIVATE);
                if(isChecked){//체크
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("alarm", "on");
                    setalarmtext.setText("벨소리를 사용합니다");
                    edit.commit();
                    /*if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT||maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
                        maudiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }*/
                }
                else if(!isChecked){//체크 해제
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("alarm", "off");
                    setalarmtext.setText("진동을 사용합니다");
                    edit.commit();
                    /*if(maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_SILENT||maudiomanager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){
                        maudiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }*/
                }
            }
        });

        //벨소리 설정
        setalarmsound=(RelativeLayout)findViewById(R.id.setalarmsound);
        setalarmsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(i, 0);
            }
        });

        setpushalarmtext=(TextView)findViewById(R.id.setpushalarmtext);
        setpushalarmswitch=(Switch)findViewById(R.id.setpushalarmswitch);
        if(setpushalarm.equals("on")){
            setpushalarmswitch.setChecked(true);
            setpushalarmtext.setText("푸쉬 알람 on");
        }
        else{
            setpushalarmswitch.setChecked(false);
            setpushalarmtext.setText("푸쉬 알람 off");
        }
        setpushalarmswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("pushalarm", "on");
                    setpushalarmtext.setText("푸쉬 알람 on");
                    edit.commit();
                }
                else if(!isChecked){
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("pushalarm", "off");
                    setpushalarmtext.setText("푸쉬 알람 off");
                    edit.commit();
                }
            }
        });

        changebabyinfo=(RelativeLayout)findViewById(R.id.changebabyinfo);
        changebabyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SettingPage.this, BabyInfoUpdate.class);
                intent1.putExtra("USER_ID", USER_ID);
                startActivity(intent1);
                finish();
            }
        });
    }

    //벨소리 설정에서 받아온 값 적용
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 0:
                if(data!=null){
                    Uri uri=data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, uri);
                }
                break;
        }
    }
}
