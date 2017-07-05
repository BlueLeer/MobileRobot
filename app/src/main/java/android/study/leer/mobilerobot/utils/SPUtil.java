package android.study.leer.mobilerobot.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Leer on 2017/2/16.
 */

public class SPUtil {
    static SharedPreferences sp;
    static String SP_FILE = "android.study.leer.mobilerobot.sharepreference";

    //读取存贮在sharepreferences当中的boolean值
    public static boolean getBoolean(Context context,String key){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        boolean isCheck = sp.getBoolean(key,false);
        return isCheck;
    }

    //存贮boolean值到sharepreferences中
    public static void storageBoolean(Context context,String key,boolean isCheck){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,isCheck).commit();
    }

    public static String getString(Context context,String key){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        String isCheck = sp.getString(key,"");
        return isCheck;
    }

    //存贮boolean值到sharepreferences中
    public static void storageString(Context context,String key,String psd){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,psd).commit();
    }

    /**
     * @param context 上下文
     * @param key 键值
     * @return 返回存贮在sp当中key值对应的int值
     */
    public static int getInt(Context context,String key){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        int location = sp.getInt(key,0);
        return location;
    }

    //存贮int值到sharepreferences中
    public static void storageInt(Context context,String key,int value){
        if(sp == null){
            sp = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).commit();
    }
}
