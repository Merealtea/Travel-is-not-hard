package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText firstpassword;
    private EditText secondpassword;
    private EditText email;
    private EditText phonenum;
    private EditText username;
    private Button finishreg;
    private UserDataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initview();
        //获取用户数据库
        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        finishreg.setOnClickListener(v->{
            String new_password = firstpassword.getText().toString();
            String checkpasword = secondpassword.getText().toString();
            String new_phone = phonenum.getText().toString();
            String new_email = email.getText().toString();
            String new_username = username.getText().toString();

            int ret = checkinput(new_password,checkpasword,new_phone,new_email,new_username);
            switch (ret){
                case 0:
                    AddUser(dbHelper,new_username,new_password,new_email,new_phone);
                    Toast.makeText(Register.this,"注册成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    Toast.makeText(Register.this,"请填写完整信息", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(Register.this,"两次输入密码不一致", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(Register.this,"手机号格式不正确", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(Register.this,"注册失败", Toast.LENGTH_LONG).show();
                    break;
            }
                });


    }

    private void initview(){
        username=findViewById(R.id.setusername);
        firstpassword=findViewById(R.id.firstpassword);
        secondpassword=findViewById(R.id.secondpassword);
        email=findViewById(R.id.email);
        phonenum=findViewById(R.id.phonenum);
        finishreg=findViewById(R.id.finishreginster);
    }

    private int checkinput(String firstpassword, String secondpassword, String phonenum,String email
            ,String username){
        if(firstpassword.length()==0 || secondpassword.length()==0||phonenum.length()==0
            ||email.length()==0||username.length()==0)
            return 1;
        if(!firstpassword.equals(secondpassword))
            return 2;
        if(phonenum.length() != 11)
            return 3;
        return 0;
    }

    private void AddUser(UserDataBaseHelper dbHelper,String Username,String Password,String Email,String Phone_number){
        SQLiteDatabase Userdb = dbHelper.getWritableDatabase();
        ContentValues Userinfo = new ContentValues();
        Userinfo.put("name", Username);
        Userinfo.put("password",Password);
        Userinfo.put("email",Email);
        Userinfo.put("phone_number",Phone_number);
        Userdb.insert("UserInfo",null,Userinfo);
        Userinfo.put("scene_num",0);
        Userinfo.put("schedule_num",0);
        Userinfo.put("dist_info","");
    }
}