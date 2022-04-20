package com.example.myapplication;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoShow extends AppCompatActivity {

    private TextView ShowTitle;
    private TextView Showname;
    private TextView ShowID;
    private TextView Showemail;
    private TextView Showphonenumber;
    private Button EditButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_show);

        SharedPreferences sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        initview();

        String Username = sp.getString("name","UNK");
        String Useremail = sp.getString("email","UNK");
        String Userphonenumber = sp.getString("phone_number","UNK");
        int UserID = sp.getInt("id",0);
        Log.d("Userinfo",Username);
        Log.d("Userinfo",Useremail);
        Showname.setText("用户名:" + Username);
        Showemail.setText("用户邮箱:" + Useremail);
        ShowID.setText("用户ID:" + UserID);
        Showphonenumber.setText("用户电话号:" + Userphonenumber);

        EditButton.setOnClickListener(v -> {
            Intent intent=new Intent(UserInfoShow.this,UserInfoControl.class);
            startActivity(intent);
            finish();
        });

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mi=getMenuInflater();/*把文件变成Menu对象*/
        mi.inflate(R.menu.menu,menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem Item)
    {
        Toast toast;
        switch (Item.getItemId())
        {
            case R.id.search:
                Intent intent2search =  new Intent(UserInfoShow.this, search.class);
                startActivity(intent2search);
                break;
            case R.id.calender:
                Intent intent2cal =  new Intent(UserInfoShow.this, calender.class);
                startActivity(intent2cal);
                break;
            case  R.id.myself:
                toast= Toast.makeText(UserInfoShow.this,"你已经在我的界面啦",Toast.LENGTH_LONG);
                toast.show();
                break;
            case R.id.exit:
                Intent intent2log =  new Intent(UserInfoShow.this, MainActivity.class);
                startActivity(intent2log);
                break;
            default:
                return super.onOptionsItemSelected(Item);
        }
        return true;
    }

    private void initview(){
        ShowTitle = findViewById(R.id.UserInfoTitle);
        Showname = findViewById(R.id.Username);
        ShowID = findViewById(R.id.UserID);
        Showemail = findViewById(R.id.Useremail);
        Showphonenumber = findViewById(R.id.Userphonenumber);
        EditButton = findViewById(R.id.EditButton);
    }
}