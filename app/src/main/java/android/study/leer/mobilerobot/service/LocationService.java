package android.study.leer.mobilerobot.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import java.util.List;

/**
 * Created by Leer on 2017/2/22.
 */

public class LocationService extends Service {
    private LocationManager mLm;
    private String mProvider;
    private Location mLc;

    @Override
    public void onCreate() {
        super.onCreate();

        //2,获取当前的位置
        mLm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    //方式一:这种方式是比较繁琐的方式
        //2.1获取当前的可用的位置提供器
//        List<String> providerList = mLm.getProviders(true);
//        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//            mProvider = LocationManager.GPS_PROVIDER;
//        } else if (providerList.contains(LocationManager.PASSIVE_PROVIDER)) {
//            mProvider = LocationManager.PASSIVE_PROVIDER;
//        } else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
//            mProvider = LocationManager.NETWORK_PROVIDER;
//        }else {
//            //当没用可用的位置提供器的时候提示用户没有可用的位置提供器
//            ToastUtil.show(this, "对不起,当前的位置不可用!");
//            return;
//        }
//
        //这行代码是根据要求添加的代码
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        mLc = mLm.getLastKnownLocation(mProvider);

    //方式二:利用google提供的 Criteria 类
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        //Criteria.ACCURACY_FINE 表示以最优的方式获取location的位置给提供provider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mProvider = mLm.getBestProvider(criteria,true);
       if(mLc != null){
            showLocation(this);
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                showLocation(getApplicationContext());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        mLm.requestLocationUpdates(mProvider,1000,1,locationListener);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showLocation(Context context) {
        ToastUtil.show(context,"纬度: "+mLc.getLatitude()+" 经度: "+mLc.getLongitude());
        //给紧急联系人发送一条短信
        String contactNum = SPUtil.getString(context, ContantValues.BOUND_PHONE_NUM);
        String num = contactNum.replaceAll("[a-zA-Z]","").trim();
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(num,null,"纬度: "+mLc.getLatitude()+" 经度: "+mLc.getLongitude(),null,null);

        //当短信发送出去以后,及时的销毁服务,以免服务过于耗电
        onDestroy();

    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,LocationService.class);
        return i;
    }

}
