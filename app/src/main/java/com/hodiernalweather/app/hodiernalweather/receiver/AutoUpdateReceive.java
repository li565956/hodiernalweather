package com.hodiernalweather.app.hodiernalweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hodiernalweather.app.hodiernalweather.service.AutoUpdateService;

/**
 * Created by lichnagbo on 2016/5/29.
 */
public class AutoUpdateReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
