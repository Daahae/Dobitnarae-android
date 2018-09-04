package com.example.dobitnarae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AdminMyPageActivity extends AppCompatActivity {
    private Store store;

    private Context context;

    private String password, name, phone;
    private EditText passwordET, nameET, phoneET;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        context = this;

        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra("store");

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

        passwordET = (EditText)findViewById(R.id.myPage_password);
        nameET = (EditText)findViewById(R.id.myPage_name);
        phoneET = (EditText)findViewById(R.id.myPage_phone);

        account = Account.getInstance();

        TextView idTextView = (TextView)findViewById(R.id.myPage_id);
        idTextView.setText(account.getId());

        password = account.getPw();
        name = account.getName();
        phone = account.getPhone();

        nameET.setText(name);
        phoneET.setText(phone);

        CardView editBtn = (CardView)findViewById(R.id.myPage_edit_btn);
        editBtn.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(context);
            }
        });
    }

    private void showAlert(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("정보를 수정 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tmp = passwordET.getText().toString();
                        if(tmp.length() > 0)
                            password = tmp;
                        name = nameET.getText().toString();
                        phone = phoneET.getText().toString();

                        account.setName(name);
                        account.setPhone(phone);
                        account.setPw(password);

                        // 서버에 계정 정보 갱신
                        JSONTask.getInstance().updateAccount(account);

                        Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show();
                        passwordET.setText("");
                    }
                });
        builder.setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameET.setText(name);
                        phoneET.setText(phone);
                        passwordET.setText("");
                    }
                });
        builder.show();
    }
}