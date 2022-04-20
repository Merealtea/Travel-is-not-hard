package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private UserDataBaseHelper dbHelper;
    private SQLiteDatabase Userdb;
    private ContentValues Userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        Userdb = dbHelper.getWritableDatabase();
        setContentView(R.layout.activity_user_info_control);

        initview();

        EditFinishButton.setOnClickListener(v -> {
            Userinfo = new ContentValues();
            SharedPreferences sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
            String Username = sp.getString("name","UNK");
            Userinfo.put("id",Integer.parseInt(userIDinput.getText().toString()));
            Userinfo.put("email",emailinput.getText().toString());
            Userinfo.put("phone_number",phonenumberinput.getText().toString());
            SharedPreferences.Editor editor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
            editor.putInt("id",Integer.parseInt(userIDinput.getText().toString()));
            editor.putString("email",emailinput.getText().toString());
            editor.putString("phone_number",phonenumberinput.getText().toString());
            editor.apply();

            Userdb.update("UserInfo",Userinfo,"name == ?", new String[]{Username});
            Log.d("Update","finish_update");
            Toast.makeText(UserInfoControl.this,"修改成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),UserInfoShow.class);
            startActivity(intent);
        });

    }

    private void initview(){
        usernameiput = findViewById(R.id.EditUsername);
        userIDinput = findViewById(R.id.EditUserID);
        phonenumberinput = findViewById(R.id.EditUserphonenumber);
        emailinput = findViewById(R.id.EditUseremail);
        ShowTitle = findViewById(R.id.UserInfoTitle);
        Showname = findViewById(R.id.Username);
        ShowID = findViewById(R.id.UserID);
        Showemail = findViewById(R.id.Useremail);
        Showphonenumber = findViewById(R.id.Userphonenumber);
        EditFinishButton = findViewById(R.id.FinishEditButton);
    }
}