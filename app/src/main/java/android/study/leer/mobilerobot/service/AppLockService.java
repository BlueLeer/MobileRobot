package android.study.leer.mobilerobot.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.study.leer.mobilerobot.activity.AppLockPsdActivity;
import android.study.leer.mobilerobot.db.AppLockCrudDB;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Leer on 2017/3/13.
 */
public class AppLockService extends Service{

    private AppLockCrudDB mAppLockCrudDB;
    private boolean isWatch = true;
    private String mAllowedPackageNameOfApp;
    private InnerBroadcastReceiver mReceiver;
    private List<String> mLockAppList;
    private MyContentObserver mContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppLockCrudDB = AppLockCrudDB.getAppLockCrudDB(this);
        //开启一个看门狗服务
        watch();

        //开启一个广播接受者,接收来自密码输入界面的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ContantValues.BROADCAST_WATCH_SKIP);
        mReceiver = new InnerBroadcastReceiver();
        //注册该广播接收器
        registerReceiver(mReceiver,filter);

        mContentObserver = new MyContentObserver(new Handler());

        //注册内容观察者
        getContentResolver().registerContentObserver(
                Uri.parse(ContantValues.LOCKEDAPP_NOTIFY_CHANGED),true,mContentObserver);
    }

    private void watch() {
        new Thread(){
            @Override
            public void run() {
                //获取数据库中维护的加了程序锁的应用
                mLockAppList = mAppLockCrudDB.queryAll();

                while(isWatch) {

                    //获取正在运行应用
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //获取正在运行的应用的任务栈的集合
                    List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
                    //当一个应用开启的时候,该应用会自动添加到所获得的列表的首位,因此获取第一个就可以了
                    ActivityManager.RunningTaskInfo taskInfo = taskInfoList.get(0);
                    //获取想要开启应用的包名,包名是每个应用的唯一标识符
                    String startAppPackageName = taskInfo.topActivity.getPackageName();
                    //看将要开启的应用是否已经放入到了加锁的应用当中去
                    if (mLockAppList.contains(startAppPackageName)) {
                        if(!startAppPackageName.equals(mAllowedPackageNameOfApp)){
                            //如果包含,就跳转到程序锁密码框输入的界面
                            Intent intent = AppLockPsdActivity.newIntent(getApplicationContext(), startAppPackageName);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    }

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    //接收从加了锁的应用进入应用的密码输入界面传来的广播
    private class InnerBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mAllowedPackageNameOfApp = intent.getExtras().getString("packagename");
        }
    }

    //内容观察者,当加锁应用被添加或者被删除的时候就会更新此服务中被加锁的应用的列表
    private class MyContentObserver extends ContentObserver{
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mLockAppList = mAppLockCrudDB.queryAll();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //结束看门狗的无限不循环
        isWatch = false;
        //注销广播接受者
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
        //停止内容观察者的观察
        if(mContentObserver != null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,AppLockService.class);
        return i;
    }
}
