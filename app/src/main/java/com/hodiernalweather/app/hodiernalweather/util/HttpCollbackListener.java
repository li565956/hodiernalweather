package com.hodiernalweather.app.hodiernalweather.util;

/**
 * Created by lichnagbo on 2016/5/28.
 */
public interface HttpCollbackListener {

    void onFinish (String response);

    void onError (Exception e);

}
