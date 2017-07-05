package android.study.leer.mobilerobot.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.study.leer.mobilerobot.utils.ContantValues;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Leer on 2017/3/12.
 */

public class CommonNumberCrudDB {

    public List<Group> getGroup(Context context){
        //获取数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                ContantValues.DATABASE_FILE_ROOT +"/commonnum.db",
                null,SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query("classlist",new String[]{"name","idx"},null,null,null,null,null);

        List<Group> groupList = new ArrayList<>();
        while(c.moveToNext()){
            Group group = new Group();
            group.name = c.getString(0);
            group.idx = c.getString(1);
            group.children = getChild(context,group.idx);
            groupList.add(group);
        }

        db.close();
        c.close();
        return groupList;
    }
    /** 从数据库当中读取子节点
     * @param context 上下文环境
     */
    public List<Child> getChild(Context context,String idx){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                ContantValues.DATABASE_FILE_ROOT +"/commonnum.db",
                null,SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query("table"+idx,new String[]{"number","name"},null,null,null,null,null);

        List<Child> childList = new ArrayList<>();
        while (c.moveToNext()){
            Child child = new Child();
            child.number = c.getString(0);
            child.name = c.getString(1);
            childList.add(child);
        }

        db.close();
        c.close();

        return childList;
    }

    public class Group{
        public String name;
        public String idx;
        public List<Child> children;
    }

    public class Child{
        public String number;
        public String name;
    }
}
