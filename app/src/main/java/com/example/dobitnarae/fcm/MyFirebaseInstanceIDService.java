package com.example.dobitnarae.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
       // super.onTokenRefresh();
        // 푸시알림을 보낼때 토큰이 필요
        // 앱 설치, 삭제 또는 유효기간 만료 등의 다양한 이유로 토큰은 계속해서 바뀐다.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // 생성한 토큰을 서버로 보내 저장하기 위함
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        // 여기서 바뀐 토큰 데이터를 저장
        // db에 저장해서 필요할때 불러서 쓰거나
        // 앱자체 SharedPreferences 저장해서 불러 쓰면 된다.

        // OKHttp를 이용해 웹서버로 토큰값을 날려준다.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();
        // request
        Request request = new Request.Builder()
                // "http://서버주소/fcm/register.php"
                .url("http://13.209.89.187:3443")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
