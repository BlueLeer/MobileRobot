package android.study.leer.mobilerobot.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.util.Log;

/**
 * Created by Leer on 2017/2/24.
 */

public class QueryPhoneAddress {

    private static SQLiteDatabase mDB;
    private static final String TAG = "QueryPhoneAddress";
    private static String mAddress;

    public static String getAddress(final String phoneNum){
        mDB = SQLiteDatabase.openDatabase(ContantValues.DATABASE_FILE_ROOT + "/address.db",
                null, SQLiteDatabase.OPEN_READONLY);
        mAddress = "未知号码";
        String regularExpression = "^1[3-8]\\d{9}";
        if(phoneNum.matches(regularExpression)) {
            String phoneNum1 = phoneNum.substring(0, 7);
            Cursor cursor = mDB.query("data1", new String[]{"outkey"}, "id = ?",
                    new String[]{phoneNum1}, null, null, null);
            if(cursor.moveToFirst()) {
                String outkey = cursor.getString(0);
                Log.d(TAG, "outkey = " + outkey);
                Cursor addCursor = mDB.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                addCursor.moveToFirst();
                mAddress = addCursor.getString(0);
                cursor.close();
                addCursor.close();
            }else{
                mAddress = "未知号码";
            }
            Log.d(TAG, "address = " + mAddress);

            return mAddress;
        }else if(phoneNum.length() == 3){
            mAddress = "报警电话";
        }else if(phoneNum.length() == 4){
            mAddress = "模拟器电话";
        }else if(phoneNum.length() == 5){
            mAddress = "服务电话";
        }else if(phoneNum.length() == 7){
            mAddress = "本地电话";
        }else if(phoneNum.length() == 11){
            String tphone = phoneNum.substring(1,3);
            Cursor pCursor = mDB.query("data2",new String[]{"location"},
                    "area = ?",new String[]{tphone},null,null,null);
            if(pCursor.moveToFirst()){
                mAddress = pCursor.getString(0);
            }else{
                mAddress = "未知号码";
            }
            pCursor.close();
        }else{
            mAddress = "未知号码";
        }
        return mAddress;
    }
}
