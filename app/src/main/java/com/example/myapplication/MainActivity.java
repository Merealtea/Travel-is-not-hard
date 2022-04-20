package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonreg;
    private Button buttonlog;
    private EditText usernameiput;
    private EditText passwordinput;
    private UserDataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化用户数据库
        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        dbHelper.getWritableDatabase();
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

            int check = checkinput(dbHelper,username,password);
            switch (check){
                case 0:
                    SQLiteDatabase Userdb = dbHelper.getWritableDatabase();
//                    Log.d("SceneList","BuildingList");
//                    String_to_Bytes UserScene = new String_to_Bytes(Userdb,username,"scene_num","scene_list");
//                    Log.d("SceneList","GetList");
//                    UserScene.Add_item(Userdb,"LoginScene");
//                    Log.d("SceneList","AddFinish");

                    Intent intent = new Intent(getApplicationContext(), search.class);
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

    private int checkinput(UserDataBaseHelper dbHelper,String username, String password) {
        SQLiteDatabase Userdb = dbHelper.getWritableDatabase();
        Cursor cursor = Userdb.query("UserInfo", new String[]{"id","name", "password","email","phone_number"}, "name == ?", new String[]{username}, null, null, null);
        Log.d("database", "cursor found");
        if (cursor.moveToFirst() == false) {
            cursor.close();
            return 2;
        }
        String dbUser = cursor.getString(cursor.getColumnIndex("name"));
        String dbPassword = cursor.getString(cursor.getColumnIndex("password"));
        if (password.equals(dbPassword)) {
            int UserID = cursor.getInt(cursor.getColumnIndex("id"));
            String Email = cursor.getString(cursor.getColumnIndex("email"));
            String Phone_number = cursor.getString(cursor.getColumnIndex("phone_number"));

            SharedPreferences.Editor editor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
            editor.putInt("id",UserID);
            editor.putString("email",Email);
            editor.putString("phone_number",Phone_number);
            editor.putString("name",dbUser);
            editor.apply();
            cursor.close();
            return 0;
        }
        cursor.close();
        return 3;
    }
}