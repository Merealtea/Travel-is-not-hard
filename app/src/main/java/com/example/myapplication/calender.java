package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class calender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
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
                Intent intent2search =  new Intent(calender.this, search.class);
                startActivity(intent2search);
                break;
            case R.id.calender:
                toast= Toast.makeText(calender.this,"你已经在日程界面啦",Toast.LENGTH_LONG);
                toast.show();
                break;
            case  R.id.myself:
                Intent intent2my =  new Intent(calender.this, myself.class);
                startActivity(intent2my);
                break;
            case R.id.exit:
                Intent intent2log =  new Intent(calender.this, MainActivity.class);
                startActivity(intent2log);
                break;
            default:
                return super.onOptionsItemSelected(Item);
        }

        return true;
    }
}