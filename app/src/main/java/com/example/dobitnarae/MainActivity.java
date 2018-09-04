package com.example.dobitnarae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.dobitnarae.fcm.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String token, loginID;
    private MyFirebaseInstanceIDService ac;
    private Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // news라는 토픽에 등록
        // 앱이 실행되면 자동으로 news 토픽을 구독
        // 토픽에 등록되면 나중에 관리자가 메세지를 보낼때 주제별고 선택해서 알림을
        // 보낼수 있게 해주는 것이다.
        //FirebaseMessaging.getInstance().subscribeToTopic("news");
        // 앱이 실행될때 토큰정보
        token = (FirebaseInstanceId.getInstance().getToken()).toString();
        Log.e("token", token);
        loginID = JSONTask.getInstance().getLoginID();
        account = JSONTask.getInstance().getAccountAll(loginID).get(0);
        //ac = new MyFirebaseInstanceIDService();
        //ac.onTokenRefresh();
        //ac.updateToken(account, token);
        JSONTask.getInstance().updateFcmToken(account, token);

        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        //고객
        Button gotoStore = (Button)findViewById(R.id.gotoStore);
        gotoStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //관리자
        Button gotoAdmin = (Button)findViewById(R.id.gotoAdmin);
        gotoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
        //데이터베이스 조회
        Button gotoDatabase = (Button)findViewById(R.id.gotoDatabase);
        gotoDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DBstoreActivity.class);
                startActivity(intent);
            }
        });
    }
}