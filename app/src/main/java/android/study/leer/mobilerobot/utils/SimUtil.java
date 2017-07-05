package android.study.leer.mobilerobot.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Leer on 2017/2/19.
 */

public class SimUtil {
    public static String getSIMId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simId = tm.getSubscriberId();
        return simId;
    }
}
