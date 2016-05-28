package com.hodiernalweather.app.hodiernalweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lichnagbo on 2016/5/28.
 */
public class HodiernalWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_PROVINCE = "create table province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    public static final String CREATE_CITY = "create table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";

    public static final String CREATE_COUNTY = "create table county(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";

    public HodiernalWeatherOpenHelper (Context context, String nname, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nname, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
