package com.hodiernalweather.app.hodiernalweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hodiernalweather.app.hodiernalweather.R;
import com.hodiernalweather.app.hodiernalweather.db.HodiernalWeatherDB;
import com.hodiernalweather.app.hodiernalweather.model.City;
import com.hodiernalweather.app.hodiernalweather.model.County;
import com.hodiernalweather.app.hodiernalweather.model.Province;
import com.hodiernalweather.app.hodiernalweather.util.HttpCollbackListener;
import com.hodiernalweather.app.hodiernalweather.util.HttpUtil;
import com.hodiernalweather.app.hodiernalweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichnagbo on 2016/5/28.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HodiernalWeatherDB hodiernalWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private Province selectedProvince;
    private City selectedCity;
    private int currrentLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);
        getSupportActionBar().hide();
        hodiernalWeatherDB = HodiernalWeatherDB.getInstance(this);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currrentLevel) {
                    case LEVEL_PROVINCE:
                        selectedProvince = provinces.get(position);
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity = cities.get(position);
                        queryCounties();
                        break;
                    case LEVEL_COUNTY:
                        String countyCode = counties.get(position).getCountyCode();
                        Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                        intent.putExtra("county_code", countyCode);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces () {
        provinces = hodiernalWeatherDB.loadProvinces();
        if (provinces.size() > 0) {
            dataList.clear();
            for (Province province : provinces) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currrentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities () {
        cities = hodiernalWeatherDB.loadCities(selectedProvince.getId());
        if (cities.size() > 0) {
            dataList.clear();
            for (City city : cities) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currrentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties () {
        counties = hodiernalWeatherDB.loadCounties(selectedCity.getId());
        if (counties.size() > 0) {
            dataList.clear();
            for (County county : counties) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currrentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer (final String code, final String type) {
        String address;
        if (code != null) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCollbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(hodiernalWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(hodiernalWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(hodiernalWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cloaseProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cloaseProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog () {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void cloaseProgressDialog () {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currrentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currrentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
