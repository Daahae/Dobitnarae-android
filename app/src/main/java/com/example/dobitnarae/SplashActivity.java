package com.example.dobitnarae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainActivity.class 자리에 다음에 넘어갈 액티비티를 넣어주기
        int priv = Account.getInstance().getPrivilege();
        Intent intent = null;

        switch (priv){
            case Constant.CLIENT:
                intent = new Intent(this, MainActivity.class);
                break;
            case Constant.ADMIN:
                intent = new Intent(this, AdminActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
