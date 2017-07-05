package android.study.leer.mobilerobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.MessageUtil;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.SimUtil;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;


/**
 * Created by Leer on 2017/2/21.
 */

public class SimChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "SimChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(SPUtil.getBoolean(context,ContantValues.ISMOBILESAFESETTED)){
            //发送报警短信给指定的手机
            sendMessage(context, intent);
        }
    }



    /**
     * 发送报警短信给
     */
    private void sendMessage(Context context, Intent intent) {
        //1.获取存贮在sp当中的sim卡序列号
        String spSimId = SPUtil.getString(context, ContantValues.SIM_ID);
        //2.获取当前手机的sim卡序列号
        //加了"xx"是为了测试用
        String simId = SimUtil.getSIMId(context) + "xx";
        //3.如果当前手机sim卡的序列号和存贮在sp当中的不一致,
        if (!spSimId.equals(simId)) {
            // 4.就从sp中获取紧急联系人的电话号码
            String s = SPUtil.getString(context, ContantValues.BOUND_PHONE_NUM);
            //因为sp当中存贮了联系人信息包括联系人电话号码和联系人姓名,这里将联系人的电话号码提取出来
            String contactNum = s.replaceAll("[a-zA-Z]", "").trim();
            //5.给紧急联系人发送一条短信
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(contactNum, null, "报警!报警!SIM已变更!设备已重启!", null, null);
        }
    }
}
