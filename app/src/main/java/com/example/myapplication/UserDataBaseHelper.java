package com.example.myapplication;
import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class UserDataBaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_UserInfo = "CREATE TABLE UserInfo ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT(32), "
            + "password TEXT(32), "
            + "email TEXT(32), "
            + "phone_number TEXT(32))";



    private Context mContext;

    public UserDataBaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_UserInfo);
        Log.d("database","UserInfo create");
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
