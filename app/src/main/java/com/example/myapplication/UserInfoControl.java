package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoControl extends AppCompatActivity {

    private TextView ShowTitle;
    private TextView Showname;
    private TextView ShowID;
    private TextView Showemail;
    private TextView Showphonenumber;
    private Button EditFinishButton;
    private EditText usernameiput;
    private EditText userIDinput;
    private EditText phonenumberinput;
    private EditText emailinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_control);

        initview();

        EditFinishButton.setOnClickListener(v -> {
            Toast.makeText(UserInfoControl.this,"修改成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),UserInfoShow.class);
            startActivity(intent);
        });

    }

    void initview(){
        usernameiput = findViewById(R.id.EditUsername);
        userIDinput = findViewById(R.id.EditUserID);
        phonenumberinput = findViewById(R.id.EditUserphonenumber);
        emailinput = findViewById(R.id.EditUseremail);
        ShowTitle = findViewById(R.id.UserInfoTitle);
        Showname = findViewById(R.id.Username);
        ShowID = findViewById(R.id.UserID);
        Showemail = findViewById(R.id.Useremail);
        Showphonenumber = findViewById(R.id.Userphonenumber);
        EditFinishButton = findViewById(R.id.EditButton);
    }
}