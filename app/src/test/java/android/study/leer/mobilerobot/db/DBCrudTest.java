package android.study.leer.mobilerobot.db;

import android.test.ActivityTestCase;

/**
 * Created by Leer on 2017/3/1.
 */
public class DBCrudTest extends ActivityTestCase {
    public void testGetCount() throws Exception {

    }

    public void testDelete() throws Exception {
        BlackNumberCrudDB blackNumberCrudDB = BlackNumberCrudDB.getDBCrud(getInstrumentation().getContext());
        blackNumberCrudDB.delete("129");
    }

    public void testUpdate() throws Exception {
        BlackNumberCrudDB blackNumberCrudDB = BlackNumberCrudDB.getDBCrud(getInstrumentation().getContext());
        blackNumberCrudDB.update("110","1");
    }

    public void testQueryAll() throws Exception {

    }

    public void testInsert() throws Exception {
        BlackNumberCrudDB blackNumberCrudDB = BlackNumberCrudDB.getDBCrud(getInstrumentation().getContext());
        blackNumberCrudDB.insert("110","3");
    }

}