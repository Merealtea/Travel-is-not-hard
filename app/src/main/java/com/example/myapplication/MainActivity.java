package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonreg;
    private Button buttonlog;
    private EditText usernameiput;
    private EditText passwordinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化界面
        initview();

        //设置注册按钮
        buttonreg.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(),Register.class);
            startActivity(intent);
        });

        //设置登录按钮
        buttonlog.setOnClickListener(v -> {
            String username = usernameiput.getText().toString();
            String password = passwordinput.getText().toString();

            int check = checkinput(username,password);
            switch (check){
                case 0:
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);
                    break;
                case 1:
                    Toast.makeText(MainActivity.this,"请输入用户名和密码", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"用户不存在", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,"密码不正确", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this,"登陆失败", Toast.LENGTH_LONG).show();
                    break;
            }
        });

    }//onCreate 结束

    private void initview(){
        buttonreg = findViewById(R.id.toRegister);
        buttonlog= findViewById(R.id.Login);
        usernameiput=findViewById(R.id.inputusername);
        passwordinput=findViewById(R.id.Inputpassword);
    }

    private int checkinput(String username,String password){
        if(username.length() == 0||password.length() == 0)
            return 1;
        return  0;
    }


}