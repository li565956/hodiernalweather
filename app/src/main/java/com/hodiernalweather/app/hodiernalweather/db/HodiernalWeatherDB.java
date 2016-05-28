package com.hodiernalweather.app.hodiernalweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hodiernalweather.app.hodiernalweather.model.City;
import com.hodiernalweather.app.hodiernalweather.model.County;
import com.hodiernalweather.app.hodiernalweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichnagbo on 2016/5/28.
 */
public class HodiernalWeatherDB {

    public static final String DB_NAME = "hodiernal_weather";

    public static final int VERSION = 1;

    private SQLiteDatabase db;

    private static HodiernalWeatherDB hodiernalWeatherDB;

    private HodiernalWeatherDB (Context context) {
        HodiernalWeatherOpenHelper openHelper =
                new HodiernalWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = openHelper.getWritableDatabase();
    }

    public synchronized static HodiernalWeatherDB getInstance (Context context) {
        if (hodiernalWeatherDB != null) {
            hodiernalWeatherDB = new HodiernalWeatherDB(context);
        }
        return hodiernalWeatherDB;
    }

    public void saveProvince (Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }
    }

    public List<Province> loadProvinces () {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            list.add(province);
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveCity (City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("id", city.getId());
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getPrivinceId());
            db.insert("city", null, values);
        }
    }

    public List<City> loadCities (int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setPrivinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveCounty (County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("id", county.getId());
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("county", null, values);
        }
    }

    public List<County> loadCounties (int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("cunty_name")));
                list.add(county);
            } while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
