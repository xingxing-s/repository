package com.mumatech.map;

import android.app.Application;
import android.content.Intent;

import com.mumatech.map.location.LocationService;

public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, LocationService.class));
    }
}
