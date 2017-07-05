package android.study.leer.mobilerobot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.study.leer.mobilerobot.utils.ContantValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/3/14.
 */

public class VirusCrudDB {
    private static String path = ContantValues.DATABASE_FILE_ROOT+"/antivirus.db";
    public static List<String> getVirus(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable",new String[]{"md5"},null,null,null,null,null);

        List<String> virusMD5List = new ArrayList<>();
        while(cursor.moveToNext()){
            virusMD5List.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return virusMD5List;
    }

}
