package android.study.leer.mobilerobot.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**单例模式
 * Created by Leer on 2017/3/1.
 */

public class BlackNumberCrudDB {

    private final MyDatabaseOpenHelper databaseOpenHelper;
    private static final String TABEL_NAME = "blacknumber";

    //1.私有化构造方法
    private BlackNumberCrudDB(Context context){
        databaseOpenHelper = new MyDatabaseOpenHelper(context);
    }
    //2.声明一个当前类的对象
    private static BlackNumberCrudDB blackNumberCrudDB = null;
    //3.提供静态的方法,如果当前的类的对象是null的,就创建一个新的对象,并且返回
    public static BlackNumberCrudDB getDBCrud(Context context){
        if(blackNumberCrudDB == null){
            blackNumberCrudDB = new BlackNumberCrudDB(context);
        }

        return blackNumberCrudDB;
    }

    //向数据库增加数据的方法
    public void insert(String phone,String mode){
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert(TABEL_NAME,null,values);

        db.close();
    }

    //从数据库中删除数据的方法
    public void delete(String phone){
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        db.delete(TABEL_NAME,"phone = ?",new String[]{phone});
        db.close();
    }

    //更改数据库中已有的数据
    public void update(String phone,String mode){
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.update(TABEL_NAME,values,"phone = ?",new String[]{phone});

        db.close();
    }

    //返回数目库当中的所有的数据
    public List<BlackNumInfo> queryAll(){
        List<BlackNumInfo> blackNumberList = new ArrayList<>();
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABEL_NAME,null,null,null,null,null,"_id desc");
        while(cursor.moveToNext()) {
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumInfo blackInfo = new BlackNumInfo();
            blackInfo.setPhoneNum(phone);
            blackInfo.setMode(mode);

            blackNumberList.add(blackInfo);

        }
        return blackNumberList;
    }

    /** 次查询按照_id的倒序排列,每次返回的数据为20条
     * @param offset 表示查询的起始位置
     * @return
     */
    public List<BlackNumInfo> query(String offset){
        List<BlackNumInfo> blackNumInfoList = new ArrayList<>();
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber order by _id desc limit 20 offset "+offset,null);
        while(cursor.moveToNext()){
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumInfo blackNumInfo = new BlackNumInfo();
            blackNumInfo.setPhoneNum(phone);
            blackNumInfo.setMode(mode);
            blackNumInfoList.add(blackNumInfo);
        }
        cursor.close();
        return blackNumInfoList;
    }

    /**
     * @return 返回数据当中所有数据的总条数
     */
    public int getCount(){
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        int count = db.rawQuery("select count(*) from blacknumber",null).getCount();
        return count;
    }

    /**返回此电话号码的拦截模式
     * @return
     */
    public String getMode(String phone){
        String mode = "0";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacknumber where phone = ?"
                ,new String[]{phone});
        if(cursor.moveToFirst()){
            mode = cursor.getString(0);
        }
        return mode;
    }

}
