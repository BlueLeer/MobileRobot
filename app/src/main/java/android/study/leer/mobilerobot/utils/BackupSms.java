package android.study.leer.mobilerobot.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Leer on 2017/3/7.
 */

public class BackupSms {
    public static void backUpSms(Context context, String path, Callback callback){
        //1.读取短信内容
        Cursor cursor = context.getContentResolver().
                query(Uri.parse("content://sms"),new String[]{"address","date","type","body"},
                        null,null,null);

        //获取游标中读取的内容总数
        int count = cursor.getCount();

        //给ProgressBar设置Progress最大值
        callback.setMax(count);

        //2.将短信写入到xml文件当中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(path));
            XmlSerializer xmlSerializer = Xml.newSerializer();

            //指定该xml文件的所在的目录和编码格式
            xmlSerializer.setOutput(fos,"utf-8");

            //DTD(xml规范)
            xmlSerializer.startDocument("utf-8",true);

            xmlSerializer.startTag(null,"smss");
            int index = 0;
            while(cursor.moveToNext()){
                //设定每一个标签的内容,这里的标签都是自定义的
                xmlSerializer.startTag(null,"address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null,"address");

                xmlSerializer.startTag(null,"date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null,"date");

                xmlSerializer.startTag(null,"type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null,"type");

                xmlSerializer.startTag(null,"body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null,"body");

                index++;
                callback.setProgressIndex(index);
                Thread.sleep(500);
            }
            xmlSerializer.endTag(null,"smss");

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fos != null && cursor != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
        }
    }

    public interface Callback{
        void setMax(int max);
        void setProgressIndex(int index);
    }
}
