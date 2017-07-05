package android.study.leer.mobilerobot.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Leer on 2017/2/25.
 */
public class ServiceUtil {

    private static final String TAG = "ServiceUtil";

    /**
     * @param serviceName 服务的名称
     * @return 服务是否开启的状态 true:服务已经开启并且在运行,false:服务没有开启并且没有运行
     */
    public static boolean getServiceState(Context context,String serviceName) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>  serviceInfos = mAM.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo in:serviceInfos){
            Log.d(TAG,in.service.getClassName());
            if(in.service.getClassName().equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
