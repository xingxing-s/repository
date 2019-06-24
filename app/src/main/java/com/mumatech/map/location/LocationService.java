package com.mumatech.map.location;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.lang.ref.WeakReference;

public class LocationService extends Service {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            double la = aMapLocation.getLatitude();
            double lo = aMapLocation.getLongitude();
//            aMapLocation.getBuildingId()
            Message message = handler.obtainMessage();
            try {
                Bundle bundle = new Bundle();
                bundle.putDouble("Latitude", la);
                bundle.putDouble("Longitude", lo);
                message.setData(bundle);
                message.what = 0;
                handler.replyTo.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public LocationService() {
    }

    private MyHandler handler;

    private Messenger messenger;

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new MyHandler(this);
        messenger = new Messenger(handler);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    private static class MyHandler extends Handler {
        // Activity的弱引用
        WeakReference<LocationService> reference;
        private Messenger replyTo;

        private MyHandler(LocationService service) {
            this.reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            replyTo = msg.replyTo;
            LocationService service = reference.get();
            if (service != null) {
                switch (msg.what) {
                    case 0:
                        service.mLocationClient.startLocation();
                        break;
                }
            }
        }
    }
}
