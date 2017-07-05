package android.study.leer.mobilerobot.global;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Leer on 2017/3/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //将捕获的异常写入到文件当中去
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile()+
                        File.separator+"mobilesafeerror.log";
                try {
                    PrintWriter printWriter = new PrintWriter(new File(path));
                    //将异常信息写入到文件当中去
                    e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                //当出现错误的时候可以讲此异常打印出的错误日志上传到服务器上.然后针对该错误进行修改

                System.exit(0);
            }
        });
    }
}
