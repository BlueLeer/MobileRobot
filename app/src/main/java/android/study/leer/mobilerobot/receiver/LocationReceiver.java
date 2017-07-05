package android.study.leer.mobilerobot.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.study.leer.mobilerobot.service.LocationService;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.MessageUtil;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * Created by Leer on 2017/2/22.
 */

public class LocationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        //1,获取短信内容中是否有"#*location*#"字段
        boolean isKeyContained = MessageUtil.isKeyContained(intent, "#*location*#");
        if (isKeyContained & SPUtil.getBoolean(context,ContantValues.ISMOBILESAFESETTED)) {
            Intent i = LocationService.newIntent(context);
            context.startService(i);
        }
    }
}
