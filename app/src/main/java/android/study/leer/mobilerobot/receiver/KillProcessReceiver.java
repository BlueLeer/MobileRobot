package android.study.leer.mobilerobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.study.leer.mobilerobot.engine.ProcessInfoProvider;

/**
 * Created by Leer on 2017/3/13.
 */

public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killProcessAll(context);
    }
}
