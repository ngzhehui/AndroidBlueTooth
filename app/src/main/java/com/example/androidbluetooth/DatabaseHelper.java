package com.example.androidbluetooth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final  String DB_NAME = "MDPDB";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context,DB_NAME, null,DB_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlTable = "CREATE TABLE shortcut(id INTEGER PRIMARY KEY, text VARCHAR);";
        db.execSQL(sqlTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS shortcut");
        onCreate(db);
    }

    public boolean insertData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",0);
        contentValues.put("text","null");
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("id",1);
        contentValues2.put("text","null");
        db.insert("shortcut",null,contentValues);
        long result = db.insert("shortcut",null,contentValues2);
        if(result == -1)
        {
            return false;
        }
        else return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from shortcut",null);
        return res;
    }

    public boolean updateData(String id, String text)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("text",text);
        long result = db.update("shortcut",contentValues, "id = ?",new String[] {id});
        if(result == -1)
        {
            return false;
        }
        else return true;
    }
}
