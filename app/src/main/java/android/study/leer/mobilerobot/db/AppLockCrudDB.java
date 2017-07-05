package android.study.leer.mobilerobot.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.study.leer.mobilerobot.utils.ContantValues;

import java.util.ArrayList;
import java.util.List;

/**单例模式
 * Created by Leer on 2017/3/13.
 */

public class AppLockCrudDB {
    private Context mContext;
    MyDatabaseOpenHelper myDatabaseOpenHelper;
    //私有化构造方法
    private AppLockCrudDB(Context context){
        myDatabaseOpenHelper = new MyDatabaseOpenHelper(context);
        this.mContext = context;

    }
    //创建一个当前类的实例
    private static AppLockCrudDB mAppLockCrudDB;

    //提供一个静态方法,获取当前类的实例
    public static AppLockCrudDB getAppLockCrudDB(Context context){
        if(mAppLockCrudDB == null){
            mAppLockCrudDB = new AppLockCrudDB(context);
        }
        return mAppLockCrudDB;
    }

    //提供插入数据库数据的方法
    public void insert(String packageName){
        SQLiteDatabase db = myDatabaseOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename",packageName);
        db.insert("applock",null,values);

        db.close();

        //提供给内容观察者,当删除或者添加一条数据的时候就会被内容观察者观察到
        mContext.getContentResolver().notifyChange(
                Uri.parse(ContantValues.LOCKEDAPP_NOTIFY_CHANGED),null);
    }

    public void delete(String packageName){
        SQLiteDatabase db = myDatabaseOpenHelper.getWritableDatabase();
        db.delete("applock","packagename = ?",new String[]{packageName});

        db.close();

        mContext.getContentResolver().notifyChange(
                Uri.parse(ContantValues.LOCKEDAPP_NOTIFY_CHANGED),null);
    }

    public List<String> queryAll(){
        List<String> lockAppList = new ArrayList<>();
        SQLiteDatabase db = myDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("applock",new String[]{"packagename"},null,null,null,null,null);
        while (cursor.moveToNext()){
            lockAppList.add(cursor.getString(0));
        }

        db.close();

        return lockAppList;
    }
}
