package com.example.myapplication;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class String_to_Bytes {
    public String Username;
    public int num;
    public byte[] byte_data;
    public String index;//
    public String num_index;
    public String raw_string_data = "";
    public String[] string_data;

    public String_to_Bytes(SQLiteDatabase db,String username,String ind_num,String ind){
        Username = username;
        index = ind;
        num_index = ind_num;

        Cursor cursor = db.query("UserInfo", new String[]{num_index,index}, "name == ?", new String[]{Username}, null, null, null);
        cursor.moveToFirst();
        num = cursor.getInt(cursor.getColumnIndex(num_index));
        if(num > 0)
        {
            byte_data = cursor.getBlob(cursor.getColumnIndex(index));
            raw_string_data = byte2string(byte_data);
            string_data = raw_string_data.split(",");
        }
    }

    public void Add_item(SQLiteDatabase db,String data){
        Cursor cursor = db.query("UserInfo", new String[]{num_index,index}, "name == ?", new String[]{Username}, null, null, null);
        cursor.moveToFirst();
        num = cursor.getInt(cursor.getColumnIndex(num_index));
        byte_data = cursor.getBlob(cursor.getColumnIndex(index));

        num += 1;
        if(num > 1)
            raw_string_data = raw_string_data + "," + data;
        else
            raw_string_data = data;
        byte_data = string2byte(raw_string_data);
        string_data = raw_string_data.split(",");

        ContentValues new_value = new ContentValues();
        new_value.put(num_index,num);
        new_value.put(index,byte_data);
        db.update("UserInfo",new_value,"name == ?", new String[]{Username});
    }

    public int Search_title(String data){
        string_data = raw_string_data.split(",");
        for(int i = 0;i < string_data.length;i++) {
            String[] schedule_data = string_data[i].split("#");
            if (schedule_data[0].equals(data)) {
                return i;
            }
        }
        return -1;
    }

    public void Delete_by_title(SQLiteDatabase db,String data){
        int index = Search_title(data);
        if(index == -1) return;
        Delete_item(db,string_data[index]);
    }

    public void Update_list(SQLiteDatabase db,String data){
        raw_string_data = data;
        string_data = raw_string_data.split(",");
        byte_data = string2byte(raw_string_data);

        ContentValues new_value = new ContentValues();
        new_value.put(index,byte_data);
        db.update("UserInfo",new_value,"name == ?", new String[]{Username});
    }

    public void Update_schedule_list(SQLiteDatabase db,String data){
        String real_title = data.split("#")[0];
        string_data = raw_string_data.split(",");
        raw_string_data = "";
        num = 0;
        for(int i = 0;i < string_data.length;i++){
            String data_title = string_data[i].split("#")[0];
            if(data_title.equals(real_title))
                string_data[i] = data;
            if(num > 0)
                raw_string_data = raw_string_data + "," + string_data[i];
            else
                raw_string_data = string_data[i];
            num += 1;
        }

        byte_data = string2byte(raw_string_data);

        ContentValues new_value = new ContentValues();
        new_value.put(index,byte_data);
        db.update("UserInfo",new_value,"name == ?", new String[]{Username});
    }

    public String Search_schedule_list(String data){
        string_data = raw_string_data.split(",");
        for(int i = 0;i < string_data.length;i++){
            String data_title = string_data[i].split("#")[0];
            if(data_title.equals(data))
                return string_data[i];
        }

        return "UNK";
    }

    public void Delete_item(SQLiteDatabase db,String data){
        Cursor cursor = db.query("UserInfo", new String[]{num_index,index}, "name == ?", new String[]{Username}, null, null, null);
        cursor.moveToFirst();
        num = cursor.getInt(cursor.getColumnIndex(num_index));
        byte_data = cursor.getBlob(cursor.getColumnIndex(index));

        raw_string_data = "";
        num = 0;
        for(int i = 0;i < string_data.length;i++){
            Log.d("String",string_data[i]);
            if(string_data[i].equals(data)){
                Log.d("String",string_data[i]+"equal_found");
                continue;
            }

            if(num == 0){
                raw_string_data = string_data[i];
            }
            else{
                raw_string_data = raw_string_data + "," + string_data[i];
            }
            num += 1;
        }
        byte_data = string2byte(raw_string_data);
        if(num > 0)
            string_data = raw_string_data.split(",");

        Log.d("delete",raw_string_data);

        ContentValues new_value = new ContentValues();
        new_value.put(num_index,num);
        new_value.put(index,byte_data);
        db.update("UserInfo",new_value,"name == ?", new String[]{Username});
    }

    public int GetItemNumber(){
        return num;
    }

    public String[] GetAllItems(){
        if (num == 0)
        {
            return null;
        }
        return string_data;
    }

    private String byte2string(byte[] b){
        String s = new String(b);
        return s;
    }

    private byte[] string2byte(String s){
        return s.getBytes();
    }
}
