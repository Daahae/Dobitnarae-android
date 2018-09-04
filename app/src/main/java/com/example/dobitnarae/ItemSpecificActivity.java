package com.example.dobitnarae;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ItemSpecificActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    private Uri imageURI;
    private Uri photoURI, albumURI;
    private ImageView imageView_store;
    private String mCurrentPhotoPath;

    private int iv_width;
    private int iv_height;

    private DecimalFormat dc;
    private LinearLayout btnReduce, btnAdd;
    private TextView selectCnt;
    private Store store;

    private Context context;

    private int index;
    private Clothes item;
    private ArrayList<Clothes> items;

    private ArrayList<String> categoryList;
    private int categoryData;

    private ArrayList<String> sexList;
    private int sexData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        context = this;

        Intent intent = getIntent();
        index = intent.getIntExtra("clothesid", 0);
        store = (Store) intent.getSerializableExtra("store");

        items = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        for (Clothes item: items) {
            if(item.getCloth_id() == index)
                this.item = item;
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        TextView titleName = (TextView)findViewById(R.id.toolbar_title);
        titleName.setText(store.getName());

        ((TextView)findViewById(R.id.tv_cloth_title)).setText("옷 정보 변경");

        // 이미지
        imageView_store = findViewById(R.id.reserve_clothes_img);
        ServerImg.getClothesImageGlide(getApplicationContext(), item.getCloth_id(), imageView_store);
        iv_width = imageView_store.getMaxWidth();
        iv_height = imageView_store.getMaxHeight();

        imageView_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureCamera();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAlbum();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(context)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        checkPermission();

        // 카테고리 선택
        categoryList = new ArrayList<String>();
        categoryList.add("상   의");
        categoryList.add("하   의");
        categoryList.add("모   자");
        categoryList.add("신   발");
        categoryList.add("장신구");
        categoryData = 1;
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_clothes_category);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.component_spin, categoryList);
        //adapter.setDropDownViewResource(R.layout.component_spin_dropdown);

        spinner.setAdapter(adapter);
        spinner.setSelection(item.getCategory()-1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryData = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 옷 이름
        final EditText name = findViewById(R.id.reserve_clothes_name);
        name.setText(item.getName());

        // 옷 설명
        final EditText description = findViewById(R.id.reserve_clothes_introduction);
        description.setText(item.getIntro());

        // 옷 가격
        dc = new DecimalFormat("###,###,###,###");
        final EditText price = findViewById(R.id.reserve_clothes_price);
        String str = dc.format(item.getPrice());
        price.setText(str);

        btnReduce = findViewById(R.id.counting_btn_reduce);
        btnAdd = findViewById(R.id.counting_btn_add);
        selectCnt = findViewById(R.id.reserve_clothes_cnt);
        selectCnt.setText(""+item.getCount());

        // 수량 추가, 감소 버튼 이벤트
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) - 1;
                if (cnt == 0)
                    btnReduce.setClickable(false);
                selectCnt.setText( "" + cnt);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) + 1;
                if (cnt == 1)
                    btnReduce.setClickable(true);
                selectCnt.setText("" + cnt);
            }
        });

        // 성별 선택
        sexList = new ArrayList<String>();
        sexList.add("남");
        sexList.add("여");


        final Spinner spinner_sex = (Spinner) findViewById(R.id.spinner_clothes_sex);

        ArrayAdapter adapter_sex = new ArrayAdapter(getApplicationContext(), R.layout.component_spin, sexList);

        spinner_sex.setAdapter(adapter_sex);

        spinner_sex.setSelection(item.getSex()-1);
        spinner_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexData = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout dataUpdate = (LinearLayout)findViewById(R.id.order_clothes_basket);
        dataUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;
                try{
                    value = Integer.parseInt(price.getText().toString());

                    if(name.getText().toString().getBytes().length <= 0){
                        Toast.makeText(getApplicationContext(), "에러: 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        if(description.getText().toString().getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "에러: 설명을 입력하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            if(value <= 0){
                                Toast.makeText(getApplicationContext(), "에러: 올바른 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                            } else {
                                item = new Clothes(0, store.getId(), categoryData, name.getText().toString(), description.getText().toString(), Integer.parseInt(price.getText().toString()), Integer.parseInt(selectCnt.getText().toString()), sexData);

                                // 데이터 초기화
                                name.setText("");
                                description.setText("");
                                price.setText("0 원");
                                selectCnt.setText("1");
                                spinner_sex.setSelection(0);
                                sexData = 1;
                                spinner.setSelection(0);
                                categoryData = 1;

                                JSONTask.getInstance().insertCloth(item, store.getId());
                                Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
                                ItemManagementFragment.changeFlg = true;
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "에러: 올바른 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout dataDelete = (LinearLayout)findViewById(R.id.order_clothes_reserve);
        dataDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public String deleteDC(String data){
        return data.replaceAll("\\,","");
    }

    public String deleteWon(String data){
        return data.replaceAll(" 원", "");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
    }

    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        // 외장메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex ){
                    Log.e("captureCamera Error", ex.toString());
                }
                if(photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provider authorites와 일치해야함

                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageURI = providerURI;

                    // 인텐트에 전달할 때는 FileProvider의 Return값인 content로만, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws  IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "img");

        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void getAlbum() {
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화
        File f = new File(mCurrentPhotoPath);
        Uri contentURI = Uri.fromFile(f);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "PhotoURI: " + photoURI + " / albumURI: " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50X50픽셀 미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", iv_width); // CROP한 이미지의 x축 크기
        cropIntent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", iv_width/200); // CROP 박스의 X축 비율
        cropIntent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP); // CROP_FROM_CAMERA case문 이동
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        // 갤러리에 추가만 시킴
                        galleryAddPic();
                        //imageView_store.setImageURI(imageURI);
                    } catch (Exception e){
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if(resultCode == RESULT_OK){
                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            //cropImage();
                            cropSingleImage(photoURI);
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode == RESULT_OK) {
                    galleryAddPic();
                    imageView_store.setImageURI(photoURI);
                    Toast.makeText(this, photoURI + "주소", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{...}의 요청으로 넘어감
            if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))){
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case MY_PERMISSION_CAMERA:
                for(int i = 0; i< grantResults.length; i++){
                    // grantResult[]: 허용된 권한은 0, 거부한 권한은 -1
                    if(grantResults[i] < 0){
                        Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면
                break;
        }
    }

    // 카메라 전용 크랍(앨범엔 크롭된 이미지만 저장시키기 위해)
    public void cropSingleImage(Uri photoURIPath) {
        Log.i("cropSingleImage", "Call");
        Log.i("cropSingleImage", "PhotoURIPath: " + photoURIPath);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50X50픽셀 미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", iv_width); // CROP한 이미지의 x축 크기
        cropIntent.putExtra("outputY", iv_height); // CROP한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", iv_width/iv_height); // CROP 박스의 X축 비율
        cropIntent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoURIPath);

        // 같은 photoURIPath에 저장하려면 아래가 있어야함
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(cropIntent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoURIPath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent i = new Intent(cropIntent);
        ResolveInfo res =  list.get(0);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        grantUriPermission(res.activityInfo.packageName, photoURIPath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }
}