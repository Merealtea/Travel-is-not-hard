package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class search extends AppCompatActivity {
    private ImageButton searchbutton;
    private EditText searchlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initview();

        searchbutton.setOnClickListener(v->{
            String locationname = searchlocation.getText().toString();
            int ret = checklocation(locationname);
            switch (ret){
                case 0:
                    break;
                case 1:
                    Toast.makeText(search.this,"请输入地点", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(search.this,"搜索失败", Toast.LENGTH_LONG).show();
                    break;
            }
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
                toast= Toast.makeText(search.this,"你已经在搜索界面啦",Toast.LENGTH_LONG);
                toast.show();
                break;
            case R.id.calender:
                Intent intent2cal =  new Intent(search.this, calender.class);
                startActivity(intent2cal);
                break;
            case  R.id.myself:
                Intent intent2my =  new Intent(search.this, myself.class);
                startActivity(intent2my);
                break;
            case R.id.exit:
                Intent intent2log =  new Intent(search.this, MainActivity.class);
                startActivity(intent2log);
                break;
            default:
                return super.onOptionsItemSelected(Item);
        }

        return true;
    }

    private void initview(){
        searchbutton=findViewById(R.id.searchbutton);
        searchlocation=findViewById(R.id.searchplace);
    }

    private int checklocation(String locationname){
        if(locationname.length() == 0)
            return 1;
        return 0;
    }
}