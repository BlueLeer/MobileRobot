package android.study.leer.mobilerobot.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Created by Leer on 2017/2/21.
 */

public class MessageUtil {
    /**
     * @param intent 传递携带有短信数据的Intent
     * @param key 想要在短信中查询的内容
     * @return
     */
    public static boolean isKeyContained(Intent intent, String key){
        //从intent中获取数据
        Bundle data = intent.getExtras();
        //从数据中获取短信数组,其中,"pdus"是获取短信的秘钥
        Object[] pdus = (Object[]) data.get("pdus");
        for(Object message:pdus){
            SmsMessage sm = SmsMessage.createFromPdu((byte[]) message);
            //如果短信中包含有要查询的内容,则直接返回true
            String smStr = sm.getMessageBody();
            if(smStr.contains(key)){
                return true;
            }
        }


        return false;
    }
}
