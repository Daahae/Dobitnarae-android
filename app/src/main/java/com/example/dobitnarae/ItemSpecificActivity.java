package com.example.dobitnarae;

import android.Manifest;
import android.app.Activity;
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

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ItemSpecificActivity extends AppCompatActivity {
    private CameraLoad camera;
    private Uri photoURI, resultUri;
    private ImageView imageViewStore;

    private DecimalFormat dc;
    private LinearLayout btnReduce, btnAdd;
    private TextView selectCnt;
    private Store store;

    private Activity activity;

    private int index;
    private Clothes item;
    private ArrayList<Clothes> items;

    private ArrayList<String> categoryList;
    private int categoryData;

    private ArrayList<String> sexList;
    private int sexData;

    public ItemSpecificActivity() {
        this.camera = new CameraLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        activity = this;

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
        imageViewStore = findViewById(R.id.reserve_clothes_img);
        ServerImg.getClothesImageGlide(getApplicationContext(), item.getCloth_id(), imageViewStore);

        imageViewStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera.captureCamera(activity);
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera.getAlbum(activity);
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(activity)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        camera.checkPermission(activity);

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case Constant.REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        // 갤러리에 추가만 시킴
                        camera.galleryAddPic(activity);
                    } catch (Exception e){
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            photoURI = data.getData();
                            InputStream i = getContentResolver().openInputStream(photoURI);
                            camera.createImageFile();
                            camera.copyFile(activity, i, camera.getmCurrentPhotoPath());
                            File f = new File(camera.getmCurrentPhotoPath());
                            resultUri = Uri.fromFile(f);
                            CropImage.activity(resultUri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setCropMenuCropButtonTitle("자르기")
                                    //.setActivityTitle("이미지 업로드")
                                    .setOutputUri(resultUri)
                                    .start(this);
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result= CropImage.getActivityResult(data);
                if(resultCode == Activity.RESULT_OK) {
                    imageViewStore.setImageURI(resultUri);
                    ServerImg.uploadFile(photoURI, String.valueOf(store.getId()), this);
                    camera.removeDir(activity,"Pictures/img");
                } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case Constant.MY_PERMISSION_CAMERA:
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
}