package com.ssu.sangjunianjuni.smartbabycare;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryPage;
import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.BlueToothDBHelper;
import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.BlunoLibrary;
import com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth.SmartBandDBHelper;
import com.ssu.sangjunianjuni.smartbabycare.babyboard.BabyBoardPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.symm.LEA;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainPage extends BlunoLibrary {

    private String USER_ID;
    private String NAME;
    private String HEIGHT;
    private String WEIGHT;

    Button measureBtn;
    //블루투스 수신 테스트용 텍스트들
    TextView connecttext, receivetext;
    SimpleDateFormat dateformat, timeformat;
    String date, time;
    TextView poopcount, heartbeatcount, temperaturecount;
    boolean ChooseDevice = true;//true이면 기저귀센서, false이면 스마트밴드
    boolean IsDeviceConnected = false;//true이면 어느 디바이스가 블루투스로 연결이 된 상태
    BlueToothDBHelper dbhelper;
    SmartBandDBHelper smartdbhelper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Firebase Message
//        FirebaseMessaging.getInstance().subscribeToTopic("news");
//        FirebaseInstanceId.getInstance().getToken();
        Intent firebaseintent = new Intent(this, DeleteFirebaseTokenService.class);
        startService(firebaseintent);

        //intent로 넘어온 USER_ID 값
        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("USER_ID");
        NAME = intent.getStringExtra("NAME");
        HEIGHT = intent.getStringExtra("HEIGHT");
        WEIGHT = intent.getStringExtra("WEIGHT");
        //블루투스 관련 설정
        onCreateProcess();                                                        //onCreate Process by BlunoLibrary
        serialBegin(115200);
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+ Permission APIs
            fuckMarshMallow();
        }
        onResumeforOnCreate();

        //툴바 초기화
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar1.setNavigationIcon(R.drawable.ic_drawer_2);
        toolbar1.setTitle("메인");
        setSupportActionBar(toolbar1);
        //NavigationDrawer 초기화
        final DrawerLayout drawerlayout1 = (DrawerLayout) findViewById(R.id.drawerlayout_main);//??? ??????? ????? drawerlayout
        ActionBarDrawerToggle drawertoggle1 = new ActionBarDrawerToggle(this, drawerlayout1, toolbar1, R.string.app_name, R.string.app_name) {
            //drawerlayout open시 구동할 함수 있으면 여기에 삽입
        };
        drawerlayout1.setDrawerListener(drawertoggle1);//drawerlayout
        drawertoggle1.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //DrawerLayout 설정
        String[] navItems = {"메인", "분석", "게시판", "육아일기", "설정"};//NavigationDrawer???ο?? ????
        ListView drawerlist1 = (ListView) findViewById(R.id.drawerlist_main);
        drawerlist1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        drawerlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent main_to_analysis = new Intent(getApplicationContext(), AnalysisPage.class);
                        main_to_analysis.putExtra("USER_ID", USER_ID);
                        main_to_analysis.putExtra("NAME", NAME);
                        main_to_analysis.putExtra("HEIGHT", HEIGHT);
                        main_to_analysis.putExtra("WEIGHT", WEIGHT);
                        startActivity(main_to_analysis);
                        break;
                    case 2:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent main_to_board = new Intent(getApplicationContext(), BabyBoardPage.class);
                        main_to_board.putExtra("USER_ID", USER_ID);
                        startActivity(main_to_board);
                        break;
                    case 3:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent main_to_diary = new Intent(getApplicationContext(), BabyDiaryPage.class);
                        main_to_diary.putExtra("USER_ID", USER_ID);
                        startActivity(main_to_diary);
                        break;
                    case 4:
                        drawerlayout1.closeDrawer(GravityCompat.START);
                        Intent main_to_setting = new Intent(getApplicationContext(), SettingPage.class);
                        main_to_setting.putExtra("USER_ID", USER_ID);
                        startActivity(main_to_setting);
                        break;
                }
            }
        });
        measureBtn = (Button) findViewById(R.id.measure_btn);
        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtil.setNetworkPolicy();
                Toast.makeText(getApplicationContext(), "button", Toast.LENGTH_SHORT).show();
                connectDirectSmartBand();
                //test용 코드
                //IsDeviceConnected=true;
                //블루투스
                //아두이노 디바이스 측에서 연결이 끊길 경우 IsDeviceConnected 변수를 false로 갱신해주는 방법이 필요
                /*if (!IsDeviceConnected) {
                    //onResumeProcess()에서 블루투스가 켜진경우, 꺼진경우 각각 다르게 반응,
                    //블루투스가 켜져있는 경우 onResumeProcess()내에서 buttonScanOnClickProcess()실행
                    onResumeProcess();
                    //buttonScanOnClickProcess();
                } else {
                    //Toast.makeText(getApplicationContext(), "send", Toast.LENGTH_SHORT).show();
                    serialSend("start");
                }*/


                //임시 데이터 삽입 코드
                /*String tempdate="";
                String temptime="";
                dateformat=new SimpleDateFormat("YYYY/MM/dd");
                timeformat = new SimpleDateFormat("HH:mm:ss");
                tempdate=dateformat.format(new Date(System.currentTimeMillis()));
                temptime = timeformat.format(new Date(System.currentTimeMillis()));
                dbhelper.insert("2017/09/13", "01:01:01");
                dbhelper.insert("2017/09/13", "02:02:02");
                dbhelper.insert("2017/09/14", "03:03:03");
                dbhelper.insert("2017/09/14", "04:01:01");
                dbhelper.insert("2017/09/14", "05:02:02");
                dbhelper.insert("2017/09/14", "06:03:03");
                dbhelper.insert("2017/09/15", "01:01:01");
                dbhelper.insert("2017/09/15", "02:02:02");
                dbhelper.insert("2017/09/15", "03:03:03");
                dbhelper.insert("2017/09/15", "04:01:01");
                dbhelper.insert("2017/09/15", "05:02:02");
                dbhelper.insert("2017/09/16", "03:03:03");
                dbhelper.insert("2017/09/16", "04:03:03");
                dbhelper.insert("2017/09/16", "05:03:03");
                dbhelper.insert("2017/09/17", "06:03:03");
                dbhelper.insert("2017/09/17", "07:03:03");
                dbhelper.insert("2017/09/18", "07:03:03");
                dbhelper.insert("2017/09/18", "08:03:03");
                dbhelper.insert("2017/09/18", "09:03:03");
                dbhelper.insert(tempdate, temptime);*/
            }
        });
        dbhelper = new BlueToothDBHelper(getApplicationContext(), "poopinfo.db", null, 1);
        smartdbhelper = new SmartBandDBHelper(getApplicationContext(), "smartinfo.db", null, 1);
        //데이터를 읽어다가 메인 화면에 표시
        updateCounts();
        updateHeartbeatCounts();

        //sharedpreference가 null일 경우 기본값을 삽입(벨소리/진동은 진동, 푸쉬알람은 on
        SharedPreferences sharedPreferences=getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        if(sharedPreferences.getString("alarm", "").equals("")){
            edit.putString("alarm", "off");
            edit.commit();
        }
        if(sharedPreferences.getString("pushalarm", "").equals("")) {
            edit.putString("pushalarm", "on");
            edit.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bar_bluetooth) {
            IsDeviceConnected = false;
            //onResumeProcess()에서 블루투스가 켜진경우, 꺼진경우 각각 다르게 반응,
            //블루투스가 켜져있는 경우 onResumeProcess()내에서 buttonScanOnClickProcess()실행
            onResumeProcess();
            //buttonScanOnClickProcess();
        }
        if (id == R.id.action_bar_setting) {
            Intent intent = new Intent(MainPage.this, SettingPage.class);
            intent.putExtra("USER_ID", USER_ID);
            startActivity(intent);
        }
        if (id == R.id.action_bar_alarm) {
            Intent intent = new Intent(MainPage.this, UserAlarmPage.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            AlertDialog.Builder backchoice = new AlertDialog.Builder(this);
            backchoice.setMessage("종료하시겠습니까?");
            backchoice.setPositiveButton("네", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            backchoice.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = backchoice.create();
            //alert.setTitle("????");
            alert.show();
        }
        return false;
    }

    public void updateCounts() {
        timeformat = new SimpleDateFormat("YYYY/MM/dd");
        time = timeformat.format(new Date(System.currentTimeMillis()));
        int poopcounttoday = dbhelper.getcount(time);
        poopcount = (TextView) findViewById(R.id.textView5);
        poopcount.setText(Integer.toString(poopcounttoday) + " 번");
    }

    public void updateHeartbeatCounts() {
        //데이터에 개행이 같이 들어와서 토크나이저로 개행 분리
        String heartbeatrecently = smartdbhelper.getheartbeat();
        StringTokenizer str = new StringTokenizer(heartbeatrecently, "\n");
        heartbeatcount = (TextView) findViewById(R.id.textView6);
        heartbeatcount.setText(str.nextToken() + " bpm");
    }

    //blunoLibrary 클래스 상속으로 인해 구현해야 하는 메소드
    //현재 연결 상태에 따라 각기 다른 설정
    /*@Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        switch (theConnectionState) {                                 //Four connection state
            case isConnected:
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                IsDeviceConnected = true;
                //connecttext.setText("Connected");
                break;
            case isConnecting:
                //connecttext.setText("Connecting");
                break;
            case isToScan:
                Toast.makeText(getApplicationContext(), "Ready to connected", Toast.LENGTH_SHORT).show();
                IsDeviceConnected = false;
                //connecttext.setText("Scan");
                break;
            case isScanning:
                //connecttext.setText("Scanning");
                break;
            case isDisconnecting:
                IsDeviceConnected = false;
                //connecttext.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }*/

    //블루투스 디바이스로부터 데이터 수신시
    @Override
    public void onSerialReceived(String string) {
        Character c=string.charAt(0);
        Toast.makeText(getApplicationContext(), "getdata: "+(int)c, Toast.LENGTH_SHORT).show();
        String theString=Integer.toString((int)c);
        // TODO Auto-generated method stub
        //receivetext.append(theString+"\n");                     //append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        //((ScrollView)receivetext.getParent()).fullScroll(View.FOCUS_DOWN);
        //스마트 밴드로부터 받아오는 코드
        if (mDeviceAddress.equals("C9:8C:C2:90:F8:9B")) {//스마트밴드
            //Toast.makeText(getApplicationContext(), "heartbeat"+theString, Toast.LENGTH_SHORT).show();
            timeformat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
            time = timeformat.format(new Date(System.currentTimeMillis()));
            smartdbhelper.insert(time, theString);
            updateHeartbeatCounts();
        }//기저귀센서로부터 받아오는 코드
        else {
            //Toast.makeText(getApplicationContext(), "poop"+theString, Toast.LENGTH_SHORT).show();
            Intent service = new Intent(getApplicationContext(), BluetoothAlarmService.class);
            startService(service);
            dateformat=new SimpleDateFormat("YYYY/MM/dd");
            timeformat = new SimpleDateFormat("HH:mm:ss");
            date=dateformat.format(new Date(System.currentTimeMillis()));
            time = timeformat.format(new Date(System.currentTimeMillis()));
            dbhelper.insert(date, time);
            updateCounts();
        }
        //Toast.makeText(getApplicationContext(), "before:"+thestring, Toast.LENGTH_SHORT).show();

        /*BlockCipherMode cipher = new LEA.ECB();
        Log.e("TAG", "before:"+string+": "+(int)string.charAt(0));
        //Toast.makeText(getApplicationContext(), "before:"+string+": "+(int)string.charAt(0), Toast.LENGTH_SHORT).show();
        byte[] key = {0x7f, 0x7e, 0x7d, 0x7c, 0x7b, 0x7a, 0x79, 0x78, 0x77, 0x76, 0x75, 0x74, 0x73, 0x72, 0x71, 0x70};
        byte[] test;
        //test=theint;
        //test = inttobyte(theint);
        test=string.getBytes();
        int a=(int)string.charAt(0);
        test=inttobyte(a);
        Log.e("TAG", "before decrypt:"+test);
        //Toast.makeText(getApplicationContext(), "before decrypt:"+test, Toast.LENGTH_SHORT).show();
        System.out.println(test.length);
        /*cipher.init(BlockCipher.Mode.ENCRYPT, key);
        byte[] enc = cipher.update(test);
        String text = new String(enc);
        Toast.makeText(getApplicationContext(), "encrypt:"+text, Toast.LENGTH_SHORT).show();
        cipher.init(BlockCipher.Mode.DECRYPT, key);
        byte[] dec = cipher.update(test);
        String text = new String(dec);
        Log.e("TAG", "decrypt:"+dec);
        //Toast.makeText(getApplicationContext(), "decrypt:"+dec, Toast.LENGTH_SHORT).show();
        //Character c=text.charAt(0);
        //Toast.makeText(getApplicationContext(), "getdata: "+(int)c, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "after:"+text, Toast.LENGTH_SHORT).show();
        */

        BlockCipherMode cipher = new LEA.ECB();
        Log.e("TAG", "before:"+string+": "+(int)string.charAt(0));
        byte[] key = {0x7f, 0x7e, 0x7d, 0x7c, 0x7b, 0x7a, 0x79, 0x78, 0x77, 0x76, 0x75, 0x74, 0x73, 0x72, 0x71, 0x70};
        cipher.init(BlockCipher.Mode.DECRYPT, key);
        byte[] test;
        int a=(int)string.charAt(0);
        test=inttobyte(a);
        Log.e("TAG", "before decrypt:"+test);
        System.out.println(test.length);
        byte[] dec = cipher.update(test);
        //int b=bytetoint(dec);
        Log.e("TAG", "decrypt:"+dec);
    }

    public  byte[] inttobyte(int a)
    {
        byte b[]=new byte[4];

        b[0]=(byte)(a & 0x000000ff);
        b[1]=(byte)((a & 0x0000ff00)>>8);
        b[2]=(byte)((a & 0x00ff0000)>>16);
        b[3]=(byte)((a & 0xff000000)>>24);
        return b;
    }
    public  int bytetoint(byte b[])
    {
        return b[0]&0xff;
        //return ((b[3]&0xff)<<24) |((b[2]&0xff)<<16) | ((b[1]&0xff)<<8) | ((b[0]&0xff)) ;
    }

    //블루투스 관련
    protected void onResume() {
        super.onResume();
        //getSharedPreferences()
        //onResumeProcess();                                          //onResume Process by BlunoLibrary
        //onResumeProcess()메소드:앱에 블루투스권한이 없는 경우 블루투스 권한 허용 다이얼로그를 출력
        //->onResume()메소드에 넣을 경우 시도때도 없이 출력됨->필요한 자리에 적재적소 배치할 것
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                          //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                          //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                          //onDestroy Process by BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);               //onActivityResult Process by lunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    //마시멜로 이후부터는 권한설정 필요
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    // Permission Denied)
                } else {
                    // Permission Denied
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void fuckMarshMallow() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        1);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    1);
            return;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainPage.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    public String getUSER_ID() {
        return USER_ID;
    }
}