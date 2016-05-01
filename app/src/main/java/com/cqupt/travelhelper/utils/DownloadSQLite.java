package com.cqupt.travelhelper.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DownloadSQLite extends SQLiteOpenHelper {
    public DownloadSQLite(Context context) {
        super(context, "download.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "download" + " ("
                + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "category" + " VARCHAR(20),"
                + "fileName" + " VARCHAR(150)"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static long add(Context context, String category, String fileName) {
        DownloadSQLite downloadSQLite = new DownloadSQLite(context);
        SQLiteDatabase db = downloadSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("fileName", fileName);
        long re = db.insert("download", null, contentValues);
        db.close();
        return re;
    }

    public static int delete(Context context, String fileName) {
        DownloadSQLite downloadSQLite = new DownloadSQLite(context);
        SQLiteDatabase db = downloadSQLite.getWritableDatabase();
        int re = db.delete("download", "fileName=?", new String[]{fileName});
        db.close();
        return re;
    }

    public static List<String> query(Context context, String category, int index) {
        DownloadSQLite downloadSQLite = new DownloadSQLite(context);
        SQLiteDatabase db = downloadSQLite.getReadableDatabase();
        Cursor cursor = db.query("download", new String[]{"fileName"},
                "category=?", new String[]{category}, null, null, null
                , index + "8");       //跳过index行，取8行
        List<String> fileNames = new ArrayList<>();
        cursor.move(index);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            fileNames.add(fileName);
        }
        cursor.close();
        db.close();
        return fileNames;
    }

}
