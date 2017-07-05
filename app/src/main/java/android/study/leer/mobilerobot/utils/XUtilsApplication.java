package android.study.leer.mobilerobot.utils;

import android.app.Application;
import android.study.leer.mobilerobot.global.MyApplication;

import org.xutils.x;

/**初始化xutils
 * Created by Leer on 2017/1/3.
 */

public class XUtilsApplication extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
