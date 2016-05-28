package com.hodiernalweather.app.hodiernalweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hodiernalweather.app.hodiernalweather.db.HodiernalWeatherDB;
import com.hodiernalweather.app.hodiernalweather.model.City;
import com.hodiernalweather.app.hodiernalweather.model.County;
import com.hodiernalweather.app.hodiernalweather.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lichnagbo on 2016/5/28.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse (HodiernalWeatherDB hodiernalWeatherDB,
                                                                String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    hodiernalWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized  static boolean handleCitiesResponse (HodiernalWeatherDB hodiernalWeatherDB,
                                                              String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setPrivinceId(provinceId);
                    hodiernalWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse (HodiernalWeatherDB hodiernalWeatherDB,
                                                               String respinse, int cityId) {
        if (!TextUtils.isEmpty(respinse)) {
            String[] allCounties = respinse.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    hodiernalWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse (Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo (Context context, String cityName, String weatherCode, String temp1,
                                 String temp2, String weatherDesp, String publishTime) {
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日");
        editor.putString("cityName", cityName);
        editor.putString("weatherCode", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weatherDesp", weatherDesp);
        editor.putString("publishTime", publishTime);
        editor.putString("currentTime", format.format(new Date()));
        editor.commit();
    }

}
