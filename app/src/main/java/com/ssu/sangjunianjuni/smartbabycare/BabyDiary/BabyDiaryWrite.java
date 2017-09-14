package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yoseong on 2017-07-04.
 */

public class BabyDiaryWrite extends AppCompatActivity {
    // 겸이 이미지 크롭
    static final int MY_PERMISSON_CAMERA = 1;
    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    boolean isAlbum = false; // crop시 사진을 찍은 것인지, 앨범에서 가져온 것인지 확인하는 플래그

    private Context context = this;
    private Context mContext = this;

    //    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri imageCaptureUri;
    private ImageView diaryPhoto;
    private int id_view;
    private String absoultePath;

    private EditText diaryTitle;
    private EditText diaryContent;

    private String filePath;

    private Button diaryRegist;
    private Button diaryCancel;

    private String USER_ID;

    // 내부 db
    BabyDiaryDBHelper babyDiaryDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babydiary_write);

        Intent getUSER_ID = getIntent();
        USER_ID = getUSER_ID.getStringExtra("USER_ID");

        // db 설정
        babyDiaryDBHelper = new BabyDiaryDBHelper(getApplicationContext(), "BABYDIARY.db", null, 1);

        diaryPhoto = (ImageView) findViewById(R.id.diary_photo);

        diaryPhoto.setOnClickListener(new View.OnClickListener() {
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

        //현재 시각 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String getTime = simpleDateFormat.format(date);

        // 제목 내용 설정
        diaryTitle = (EditText) findViewById(R.id.diary_title);
        diaryContent = (EditText) findViewById(R.id.diary_context);


        // 육아일기 등록 버튼
        diaryRegist = (Button) findViewById(R.id.diary_register);
        diaryRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 제목을 입력했을때
                if ((diaryTitle.getText().toString().length()) != 0) {
                    // 저장 확인 alertdialog
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // 제목 셋팅
                    alertDialogBuilder.setTitle("육아일기 저장");

                    // alertdialog 셋팅
                    alertDialogBuilder
                            .setMessage("육아일기를 저장하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String title = diaryTitle.getText().toString();
                                            String content = diaryContent.getText().toString();
                                            babyDiaryDBHelper.insert(getTime, title, USER_ID, content, mCurrentPhotoPath);
//                                            System.out.println("저장위치: "+mCurrentPhotoPath.toString());
                                            Intent write_to_page = new Intent(BabyDiaryWrite.this, BabyDiaryPage.class);
                                            write_to_page.putExtra("USER_ID", USER_ID);
                                            startActivity(write_to_page);
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
                } else if (diaryTitle.getText().toString().length() == 0) {
                    Toast.makeText(BabyDiaryWrite.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 육아일기 취소 버튼
        diaryCancel = (Button) findViewById(R.id.diary_cancel);
        diaryCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 취소버튼 클릭시 alertdialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목 셋팅
                alertDialogBuilder.setTitle("육아일기 저장 취소");

                // alertdialog 셋팅
                alertDialogBuilder
                        .setMessage("취소 버튼 클릭 시 육아일기가 저장되지 않습니다. 취소하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(BabyDiaryWrite.this, BabyDiaryPage.class);
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



        ///babyDiaryDBHelper.insert(getTime, title, content, filePath);


    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * 카메라에서 사진촬영 후 이미지 가져오기
     */
  /*  public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        imageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
*/

    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM:
                // 이후의 처리가 카메라와 같아서 break없이 진행
                imageCaptureUri = data.getData();
                Log.d("SmartBabyDiary", imageCaptureUri.getPath().toString());
                //          case PICK_FROM_CAMERA:
                // 이미지를 가져온 후 리사이즈할 이미지 크기를 결정
                // 이후에 이미지 크롭 어플리케이션을 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageCaptureUri, "image/*");
                intent.putExtra("outputX", 1600); // CROP한이미지의 X축 크기
                intent.putExtra("outputY", 1200); // CROP한이미지의 Y축 크기
                intent.putExtra("aspectX", 4); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 3); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            case CROP_FROM_IMAGE:
                // 크롭이 된 후의 이미지를 넘겨 받음
                // 이미지뷰에 이미지를 보여줌, 임시파일 삭제
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                // CROP된 이미지를 저장하기 위한 file 경로
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/SmartBabyDiary/" + System.currentTimeMillis() + ".jpg";

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP 된 BITMAP
                    diaryPhoto.setImageBitmap(photo); // 레이아웃 이미지칸에 CROP된 BITMAP 보여줌

                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장
                    absoultePath = filePath;
                    break;
                }
                // 임시 파일 삭제
                File f = new File(imageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
        }
    }*/

    /**
     * Bitmap 저장하는 부분
     */
    private void storeCropImage(Bitmap bitmap, String filePath) {
        // SmartBabyDiary 폴더를 생성하여 이미지를 저장
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartBabyDiary";
        File directory_SmartBabyDiary = new File(dirPath);

        if (!directory_SmartBabyDiary.exists()) // SmartBabyDiary 디렉터리에 폴더가 없으면
            directory_SmartBabyDiary.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
                Toast.makeText(BabyDiaryWrite.this, "외장 메모리 미 지원", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(BabyDiaryWrite.this, "저장소 권한 설정에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

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
                diaryPhoto.setImageURI(Uri.parse(mCurrentPhotoPath));
                galleryAddPic();
                // 업로드
                break;

        }
    }

    // back 버튼 클릭 시 게시판 리스트로 돌아가기
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==event.KEYCODE_BACK)//뒤로가기 키 누른 경우
        {
            // 수정 시 back 버튼 클릭시 alertdialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // 제목 셋팅
            alertDialogBuilder.setTitle("육아일기 저장 취소");

            // alertdialog 셋팅
            alertDialogBuilder
                    .setMessage("Back 버튼 클릭 시 육아일기가 저장되지 않습니다. 취소하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(BabyDiaryWrite.this, BabyDiaryPage.class);
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
