package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class calender extends AppCompatActivity {
    private ListView calenderlist;
    private List<String> calenders = new ArrayList<String>();
    private List<String> item_list = new ArrayList<String>();
    private FloatingActionButton addSchedule;
    private SharedPreferences sp;
    private UserDataBaseHelper dbHelper;
    private SQLiteDatabase Userdb;
    private String_to_Bytes UserScene;
    private String_to_Bytes UserSchedule;
    private  EditText name;
    private Button comfirm_button;
    private LinearLayout fillname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        initveiw();

        fillname.setVisibility(View.GONE);
        addSchedule.setOnClickListener(v->{
            fillname.setVisibility(View.VISIBLE);
        });
        comfirm_button.setOnClickListener(v->{
            String sence_name =  name.getText().toString();
            if (sence_name.length() == 0)
                Toast.makeText(calender.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            else
            {
                if(UserSchedule.Search_title(sence_name) != -1)
                {Toast.makeText(calender.this, "??????????????????", Toast.LENGTH_SHORT).show();}
                else{
                    UserSchedule.Add_item(Userdb,sence_name+"#0");
                    Toast.makeText(calender.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    flush();
                    fillname.setVisibility(View.GONE); }
                }
        });
        flush();

        calenderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = calenders.get(position);
                Intent intent=new Intent(getApplicationContext(),Schedule.class);
                intent.putExtra("title",item);
                Log.d("Jump", "onItemClick: ");
                startActivity(intent);
            }
        });

        calenderlist.setOnItemLongClickListener(new  AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = item_list.get(position);
                String title = item.split("#")[0];
                AlertDialog dialog = new AlertDialog.Builder(calender.this)
                        .setMessage("?????????????????????")//????????????????????????
                        //????????????????????????
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserSchedule.Delete_by_title(Userdb,title);
                                dialog.dismiss();
                                flush();
                            }
                        }).create();
                dialog.show();
                return true;
            }
        });
    }

    private void flush() {
        String ScheduleData[] = UserSchedule.GetAllItems();
        calenders.clear();
        if(ScheduleData!=null)
        {for (int i = 0; i < ScheduleData.length; i++) {
            item_list.add(ScheduleData[i]);
            String[] schedule_info = ScheduleData[i].split("#");
            calenders.add(schedule_info[0]);
        }}
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(calender.this, android.R.layout.simple_list_item_1, calenders);
        calenderlist.setAdapter(adapter);
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mi=getMenuInflater();/*???????????????Menu??????*/
        mi.inflate(R.menu.menu,menu);
        return true;
    }
<<<<<<< HEAD

=======
>>>>>>> d11a32e94d55a24b554bfbbf60f291b3164cff7d
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
                toast= Toast.makeText(calender.this,"???????????????????????????",Toast.LENGTH_LONG);
                toast.show();
                break;
            case  R.id.myself:
                Intent intent2my =  new Intent(calender.this, UserInfoShow.class);
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

    private void initveiw(){
        calenderlist =findViewById(R.id.calenderlist);
        comfirm_button = findViewById(R.id.confirm);
        name = findViewById(R.id.schedulename);
        fillname = findViewById(R.id.fillname);

        sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        addSchedule = findViewById(R.id.addShedule);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(calender.this, android.R.layout.simple_list_item_1, calenders);
        calenderlist.setAdapter(adapter);

        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        Userdb = dbHelper.getWritableDatabase();
        UserScene = new String_to_Bytes(Userdb, sp.getString("name", "UNK"), "scene_num", "scene_list");
        UserSchedule = new String_to_Bytes(Userdb, sp.getString("name", "UNK"), "schedule_num", "schedule_list");
    }

}