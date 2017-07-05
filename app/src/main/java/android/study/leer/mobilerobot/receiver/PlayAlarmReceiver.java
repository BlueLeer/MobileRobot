package android.study.leer.mobilerobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.MessageUtil;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.util.Log;

import java.io.IOException;


/**
 * Created by Leer on 2017/2/21.
 */

public class PlayAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "PlayAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收到"#*alarm*#"的短信就会播放音乐
        playAlarm(context,intent);
    }

    private void playAlarm(Context context,Intent intent) {
        boolean keyContained = MessageUtil.isKeyContained(intent, "#*alarm*#");
        if(keyContained && SPUtil.getBoolean(context, ContantValues.ISMOBILESAFESETTED)){
            MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
//            AssetManager am = context.getAssets();
//                String[] sounds = am.list("sound");
//                String alarmStr = "sound/"+sounds[0];
                mp.setLooping(true);
                mp.start();


        }
    }
}
