package android.study.leer.mobilerobot.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Leer on 2017/1/3.
 */

public class ToastUtil {
    public static void show(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
