package com.ssu.sangjunianjuni.smartbabycare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 아기 정보 수정
 * Created by yoseong on 2017-06-25.
 */

public class BabyInfoUpdate extends AppCompatActivity {

    private String USER_ID_INFO;

    private EditText babyHeightUpdate;
    private EditText babyWeightUpdate;

    // 데이터 받아오기
    private PHPDown task;

    // 데이터 수정
    Button updateBtn;
    Button cancelBtn;
    Context context = this;
    private RadioGroup mealType;
    private String meal;

    // 겸이 이미지 크롭
    static final int MY_PERMISSON_CAMERA = 1;
    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    boolean isAlbum = false; // crop시 사진을 찍은 것인지, 앨범에서 가져온 것인지 확인하는 플래그

    private Context mContext = this;

    private ImageView profilePhoto;
    private Button profileButton;

    // server에 프로필 사진 저장 위해
    String imageFileName;

    private ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_info_update);

        // USER_ID 받아오기
        Intent intent = getIntent();
        USER_ID_INFO = intent.getStringExtra("USER_ID");

        profilePhoto = (ImageView) findViewById(R.id.profile_photo_update);
        profileButton = (Button) findViewById(R.id.profile_photo_update_button);

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


        // 수정 내용 셋팅
        babyHeightUpdate = (EditText) findViewById(R.id.register_baby_height_update);
        babyWeightUpdate = (EditText) findViewById(R.id.register_baby_weight_update);

        // 수정 내용 저장

        //아기 식사 타입 결정
        mealType = (RadioGroup) findViewById(R.id.meal_select_group_update);
        int id = mealType.getCheckedRadioButtonId();
        RadioButton check = (RadioButton) findViewById(id);
        meal = check.getText().toString();

        updateBtn = (Button) findViewById(R.id.btn_update_complete);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("아기 정보 수정");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("수정하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("수정",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            PHPRequest phpRequest = new PHPRequest("http://rkdehdduq92.cafe24.com/smart_babycare/updateuserinfo.php");
                                            String result = phpRequest.PhPtest(String.valueOf(USER_ID_INFO), Double.valueOf(Double.parseDouble(babyHeightUpdate.getText().toString())), Double.valueOf(Double.parseDouble(babyWeightUpdate.getText().toString())), String.valueOf(meal), String.valueOf("http://rkdehdduq92.cafe24.com/smart_babycare/profilePhoto/"+imageFileName));

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(BabyInfoUpdate.this, "아기 정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        Intent update_to_page = new Intent(BabyInfoUpdate.this, SettingPage.class);
                                        update_to_page.putExtra("USER_ID", USER_ID_INFO);
                                        finish();
                                        startActivity(update_to_page);
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

        cancelBtn = (Button) findViewById(R.id.btn_update_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 취소버튼 클릭시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("아기 정보 수정 취소");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("취소 버튼 클릭 시 변경사항이 저장되지 않습니다. 취소하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1 = new Intent(BabyInfoUpdate.this, SettingPage.class);
                                        intent1.putExtra("USER_ID", USER_ID_INFO);
                                        startActivity(intent1);
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

        // 데이터 받아오기
        task = new PHPDown();
        task.execute("http://rkdehdduq92.cafe24.com/smart_babycare/getupdatebabyinfo.php");
    }

    // mysql에서 데이터 불러오기
    private class PHPDown extends AsyncTask<String, Integer, String> {
        private String USER_ID;
        private String HEIGHT;
        private String WEIGHT;
        private String MEAL;
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
                    USER_ID = jo.getString("USER_ID");
                    HEIGHT = jo.getString("HEIGHT");
                    WEIGHT =jo.getString("WEIGHT");
                    MEAL = jo.getString("MEAL");
                    PHOTO_URI = jo.getString("PHOTO_URI");
                    listItem.add(new ListItem(USER_ID, HEIGHT, WEIGHT, MEAL, PHOTO_URI));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String serverPhotoPath = null;
            final Bitmap[] bitmap = new Bitmap[1];

            for(int i = 0; i < listItem.size(); i++){

                if(USER_ID_INFO.equals(listItem.get(i).getData(0))) {
                    serverPhotoPath = listItem.get(i).getData(4);
           //         profilePhoto.setImageURI(Uri.parse(listItem.get(i).getData(4)));
                    babyHeightUpdate.setText(listItem.get(i).getData(1));
                    babyWeightUpdate.setText(listItem.get(i).getData(2));
                }
            }

            // glide 라이브러리를 사용해서 이미지 로딩 속도를 현저하게 줄여 RecyvlerView의 스크롤 속도 느려짐 해결
            Glide.with(getApplicationContext()).load(serverPhotoPath).into(profilePhoto);

/*
            String finalServerPhotoPath = serverPhotoPath;
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(finalServerPhotoPath);

                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream inputStream = conn.getInputStream();
                        bitmap[0] = BitmapFactory.decodeStream(inputStream);
                    } catch (IOException e){

                    }
                }
            };
            mThread.start();

            try {
                mThread.join();

                profilePhoto.setImageBitmap(bitmap[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
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
                Toast.makeText(BabyInfoUpdate.this, "외장 메모리 미 지원", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(BabyInfoUpdate.this, "저장소 권한 설정에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
            UploadFile uploadFile = new UploadFile(BabyInfoUpdate.this);
            uploadFile.setPath(filePath);
            uploadFile.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                                    Intent intent = new Intent(BabyInfoUpdate.this, SettingPage.class);
                                    intent.putExtra("USER_ID", USER_ID_INFO);
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
