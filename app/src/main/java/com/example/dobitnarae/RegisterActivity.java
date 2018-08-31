package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText idTxt;
    private EditText passwordTxt;
    private EditText nameTxt;
    private EditText hpTxt;
    private CheckBox privBox;
    private LinearLayout registerBtn;
    private int flag = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        registerBtn = (LinearLayout) findViewById(R.id.login_btn);
        idTxt = (EditText) findViewById(R.id.register_id);
        passwordTxt = (EditText) findViewById(R.id.register_password);
        nameTxt = (EditText) findViewById(R.id.register_name);
        hpTxt = (EditText) findViewById(R.id.register_phone);
        privBox = (CheckBox) findViewById(R.id.register_check);

        registerBtn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if(privBox.isChecked())
                        flag = 0;

                    Toast.makeText(getApplicationContext(),idTxt.getText().toString(), Toast.LENGTH_LONG).show();
                    Account account = new Account(idTxt.getText().toString(), passwordTxt.getText().toString(), nameTxt.getText().toString(), hpTxt.getText().toString(), flag);
                    JSONTask.getInstance().insertAccount(account);
                    LoginActivity.setLogOut();

                    finish();

                }
        });
    }
}
