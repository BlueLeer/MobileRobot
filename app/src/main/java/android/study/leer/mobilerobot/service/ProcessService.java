package android.study.leer.mobilerobot.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.net.Inet4Address;
import java.util.List;

/**
 * Created by Leer on 2017/3/11.
 */

public class ProcessService extends Service {
    private InnerReceiver mInnerReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mInnerReceiver = new InnerReceiver();

        registerReceiver(mInnerReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mInnerReceiver != null){
            unregisterReceiver(mInnerReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ProcessService.class);
        return i;
    }

    class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //杀死除了本应用以外的其他应用
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info:infos){
                if(!info.processName.equals(context.getPackageName())){
                    am.killBackgroundProcesses(info.processName);
                }
            }
        }
    }
}
