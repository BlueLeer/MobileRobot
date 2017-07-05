package android.study.leer.mobilerobot.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leer on 2017/3/1.
 */

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TABEL_NAME = "blacknumber";
    private static final String CREATE_TABLE = "create table " + TABEL_NAME +
            " (_id integer primary key " + "autoIncrement,phone varchar(20),mode varchar(5))";
    private static final String CREATE_TABLE_APP_LOCK = "create table applock(_id integer primary key autoincrement,packagename varchar(20))";

    public MyDatabaseOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_LOCK);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("create table applock(_id integer primary key autoincrement,packagename varchar(20))");
            default:
                break;
        }
    }

}
