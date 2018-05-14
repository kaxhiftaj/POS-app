package com.techease.posapp.ui.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pos.db";
    public static final String TABLE_NAME = "job_details";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

//        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,COMMENT TEXT,IMAGES_PATH TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
      onCreate(db);
    }

    public boolean insertData(String comment,String images_path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("COMMENT",comment);
        contentValues.put("IMAGES_PATH",images_path);
        long insert = database.insert(TABLE_NAME, null, contentValues);
        if(insert == -1){
           return false;
        }
        else {
            return true;
        }
    }

    public Cursor getData(){
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    String query = "SELECT * FROM "+TABLE_NAME;
    Cursor cursor  = sqLiteDatabase.rawQuery(query,null);
    return cursor;
    }
}
