package com.ssu.sangjunianjuni.smartbabycare;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.BabyDiary.BabyDiaryWrite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 회원가입 페이지
 * Created by yoseong on 2017-05-02.
 */

public class RegisterPage extends AppCompatActivity {
    // 겸이 이미지 크롭
    static final int MY_PERMISSON_CAMERA = 1;
    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    boolean isAlbum = false; // crop시 사진을 찍은 것인지, 앨범에서 가져온 것인지 확인하는 플래그

    private Context mContext = this;

    // 서버 사진 path
    String imageFileName;

    private Button registerCompleteBtn;
    private Button registerCancelBtn;
    private Button registID;
    private EditText registPassword;
    private EditText confirmPassword;
    private EditText registName;
    private EditText registBabyAge;
    private EditText registBabyHeight;
    private EditText registBabyWeight;
    private Button registBirthday;
    private Button registBirthtime;
    private RadioGroup sexType;
    private String sex;
    private RadioGroup mealType;
    private String meal;
    private ImageView profilePhoto;
    private Button profileButton;

    // id 중복 체크
    private ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    private Context context = this;
    PHPDown task;

    //추가 버튼
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        NetworkUtil.setNetworkPolicy();

        // 아이디 중복 확인 위해 php코드 불러오기
        task = new PHPDown();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getmemberinfo.php");

        registID = (Button) findViewById(R.id.register_id);
        registPassword = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        registName = (EditText) findViewById(R.id.baby_name);
        registBabyAge = (EditText) findViewById(R.id.register_baby_age);
        registBabyHeight = (EditText) findViewById(R.id.register_baby_height);
        registBabyWeight = (EditText) findViewById(R.id.register_baby_weight);
        registBirthday = (Button) findViewById(R.id.baby_birthday);
        registBirthtime = (Button) findViewById(R.id.baby_birthtime);
        sexType = (RadioGroup) findViewById(R.id.baby_sex);
        mealType = (RadioGroup) findViewById(R.id.meal_select_group);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        profileButton = (Button) findViewById(R.id.profile_photo_button);

        // 프로필 사진 선택
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("업로드할 사진 선택");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("사진을 가져올 방법을 선택하세요.")
                        .setCancelable(false)
                        .setPositiveButton("앨범에서 선택",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getAlbum();
                                    }
                                })
                        .setNeutralButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("카메라로 촬영",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        captureCamera();
                                    }
                                });
                // alertdialog 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // alertdialog 보여주기
                alertDialog.show();
            }
        });

        // 비밀번호 일치 검사
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = registPassword.getText().toString();
                String confirm = confirmPassword.getText().toString();

                if (password.equals(confirm))   {
                    registPassword.setBackgroundColor(Color.GREEN);
                    confirmPassword.setBackgroundColor(Color.GREEN);
                } else {
                    registPassword.setBackgroundColor(Color.RED);
                    confirmPassword.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //태어난 날짜 시각 입력

        GregorianCalendar calendar = new GregorianCalendar();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        registBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(RegisterPage.this,  dateSetListener, mYear, mMonth, mDay);
                dpd.getDatePicker().setCalendarViewShown(false);
                dpd.show();
            }
        });

        registBirthtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tdp = new TimePickerDialog(RegisterPage.this, timeSetListener, mHour, mMinute, true);
                tdp.show();
            }
        });


        // 회원가입 등록 버튼
        registerCompleteBtn = (Button) findViewById(R.id.btn_register_complete);
        registerCompleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력이 다 됐는지 확인하는 변수
                int flag = 0;
                // 아이디 입력 확인
                if (registID.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    registID.requestFocus();
                    flag = 1;
                }
                // 비밀번호 입력 확인
                if (registPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "Password를 입력하세요.", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                // 이름 입력 확인
                if (registName.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "아기 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                // 아기 개월 수 입력 확인
                if (registBabyAge.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "아기 개월 수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                // 아기 키 입력 확인
                if (registBabyHeight.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "아기 키를 입력하세요.", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                // 아기 몸무게 입력 확인
                if (registBabyWeight.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPage.this, "아기 몸무게를 입력하세요.", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }

                //아기 성별 결정
                int sexId = sexType.getCheckedRadioButtonId();
                RadioButton sexCheck = (RadioButton) findViewById(sexId);
                sex = sexCheck.getText().toString();


                //아기 식사 타입 결정
                int id = mealType.getCheckedRadioButtonId();
                RadioButton check = (RadioButton) findViewById(id);
                meal = check.getText().toString();

                if (flag == 0) {
                    try {
//                        PHPRequest request = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/login.php");
                        PHPRequest request = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/member_info_photo.php");
/*                        String result = request.PhPtest(String.valueOf(registID.getText()),String.valueOf(registPassword.getText()),String.valueOf(registName.getText())
                                ,Integer.valueOf(Integer.parseInt(registBabyAge.getText().toString())),Double.valueOf(Double.parseDouble(registBabyHeight.getText().toString())),Double.valueOf(Double.parseDouble(registBabyWeight.getText().toString()))
                                ,String.valueOf(registBirthday.getText()),String.valueOf(registBirthtime.getText()),String.valueOf(sex),String.valueOf(meal));
*/                        String result = request.PhPtest(String.valueOf(registID.getText()),String.valueOf(registPassword.getText()),String.valueOf(registName.getText())
                                ,Integer.valueOf(Integer.parseInt(registBabyAge.getText().toString())),Double.valueOf(Double.parseDouble(registBabyHeight.getText().toString())),Double.valueOf(Double.parseDouble(registBabyWeight.getText().toString()))
                                ,String.valueOf(registBirthday.getText()),String.valueOf(registBirthtime.getText()),String.valueOf(sex),String.valueOf(meal), String.valueOf("http://rkdehdduq92.cafe24.com/smart_babycare/profilePhoto/"+imageFileName));

                        if( !result.equals("")) {
                            Toast.makeText(RegisterPage.this, "회원가입 완료.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RegisterPage.this, "회원가입 실패.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    setResult(RESULT_OK);
                    Intent register_to_login = new Intent(RegisterPage.this, LoginPage.class);
                    startActivity(register_to_login);
                    finish();
                }


            }
        });

        // 회원가입 취소 버튼
        registerCancelBtn = (Button) findViewById(R.id.btn_register_cancel);
        registerCancelBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_to_login = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(register_to_login);
                finish();
            }
        });

    }

    //날짜 선택 다이얼로그
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String birthdayStr = String.format("%d-%d-%d", year, month+1, dayOfMonth);
            registBirthday.setText(birthdayStr);
        }
    };

    //시각 선택 다이얼로그
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String timeStr = String.format("%d : %d", hourOfDay, minute);
            registBirthtime.setText(timeStr);
        }
    };

    // mysql에서 데이터 불러오기
    private class PHPDown extends AsyncTask<String, Integer, String> {
        private String USER_ID;

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
                    USER_ID = jo.getString("USER_ID");
                    listItem.add(new ListItem(USER_ID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            registID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    alertDialog.setTitle("ID 중복 확인");

                    final EditText id = new EditText(context);
                    alertDialog.setView(id);

                    alertDialog
                            .setMessage("ID를 입력해 중복 확인을 해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

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
                    final AlertDialog dialog = alertDialog.create();
                    // alertdialog 보여주기
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int flag = 0;
                            for(int i = 0; i < listItem.size(); i++) {
                                if(id.getText().toString().equals(listItem.get(i).getData(0))) {
                                    flag++;
                                }
                            }
                            if(flag == 0) {
                                registID.setText(id.getText().toString());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(RegisterPage.this, "동일한 ID가 존재합니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                                id.setText("");
                            }
                        }
                    });
                }
            });

        }
    }

    /**
     * 프로필 사진 등록 함수
     */

    /**
     * 겸이 이미지 가져오기 소스
     */

    public boolean isCheck(String permission) {
        switch (permission) {
            case "Camera":
                Log.i("Camera Permission", "CALL");
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("알림")
                                .setMessage("저장소 권한은 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 헝ㅇ하셔야 합니다.")
                                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:com.ssu.sangjunianjuni.smartbabycare"));
                                        mContext.startActivity(intent);
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity) mContext).finish();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSON_CAMERA);
                    }
                } else {
                    return true;
                }
                break;
        }
        return true;
    }

    /**
     * 사진 찍기
     */
    public void captureCamera() {
        if(isCheck("Camera")) {

            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this, "com.ssu.sangjunianjuni.smartbabycare", photoFile);

                        Log.i("photoFile", photoFile.toString());
                        Log.i("photoURI", photoURI.toString());

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            } else {
                Toast.makeText(RegisterPage.this, "외장 메모리 미 지원", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(RegisterPage.this, "저장소 권한 설정에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 파일 생성
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        /*
        String imageFileName = timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartBabyDiary/"+ imageFileName);

        mCurrentPhotoPath = storageDir.getAbsolutePath();
        Log.i("mCurrentPhotoPath", mCurrentPhotoPath);
        return storageDir;*/
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFileName = image.getName();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /**
     * 앨범 열기
     */
    public void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }
    /**
     * 이미지 crop하기
     */
    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", 200); // CROP한이미지의 X축 크기
        cropIntent.putExtra("outputY", 200); // CROP한이미지의 Y축 크기
        cropIntent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
        cropIntent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if(isAlbum == false) {
            cropIntent.putExtra("output", photoURI); // 크롭된 이미지를 해당 경로에 저장
        } else if (isAlbum==true) {
            cropIntent.putExtra("output", albumURI); // 크롭된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }
    /**
     * 동기화 갤러리 새로고침
     * 앨범이나 카메라로 사진 찍고 crop한 이후에 앨벙가면 해당 이미지가 안들어와 있는 경우가 많다.
     * 이 기능은 저장을 하고 Commint, 확정을 짓는 것과 같다. Uri 주소를 통해 현재 패스, 경로에 저장된 파일의 위치를 가져와 해당 Uri 위치를 브로드캐스트하여 보내주면 된다.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "CALL");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                isAlbum = false;
                cropImage();
                break;
            case REQUEST_TAKE_ALBUM:
                isAlbum = true;
                File albumFile = null;
                try {
                    albumFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (albumFile != null) {
                    albumURI = Uri.fromFile(albumFile);
                }
                photoURI = data.getData();
                cropImage();
                break;
            case REQUEST_IMAGE_CROP:
                profilePhoto.setImageURI(Uri.parse(mCurrentPhotoPath));
                galleryAddPic();
                // 업로드
                uploadFile(mCurrentPhotoPath);
                break;

        }
    }

    public void uploadFile(String filePath) {

        String url = "http://rkdehdduq92.cafe24.com/smart_babycare/test.php";
        try {
            UploadFile uploadFile = new UploadFile(RegisterPage.this);
            uploadFile.setPath(filePath);
            uploadFile.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // back 버튼 클릭 시 게시판 리스트로 돌아가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterPage.this, LoginPage.class);
        startActivity(intent);
    }

}
