package android.study.leer.mobilerobot.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.engine.ProcessInfoProvider;
import android.study.leer.mobilerobot.receiver.MyAppWidgetProvider;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Leer on 2017/3/13.
 */
public class AppWidgetService extends Service {

    private static final String TAG = "AppWidgetService";
    private Timer mTimer;
    private TimerTaskReceiver mTimerTaskReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        startTimerTask();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mTimerTaskReceiver = new TimerTaskReceiver();

        registerReceiver(mTimerTaskReceiver, filter);
    }

    private void startTimerTask() {
        mTimer = new Timer();
        //三个参数的意思分别是:一个用于执行定时任务的任务,什么时候第一次执行0代表立刻执行,执行任务的间隔数
        mTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        updateWidget();
                        Log.i(TAG, "定时更新任务正在运行.............");
                    }
                },
                0, 5000);
    }

    private class TimerTaskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //开启桌面小部件的定时更新任务
                startTimerTask();
            } else {
                //关闭桌面小部件的定时更新任务
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }
    }


    private void updateWidget() {
        //获取WidgetManager对象
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        //使用RemoteViews远程更新控件
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteViews.setTextViewText(R.id.process_count, "进程总数:" + ProcessInfoProvider.processCount(this));
        String spaceMem = Formatter.formatFileSize(this, ProcessInfoProvider.getSpaceMemory(this));
        remoteViews.setTextViewText(R.id.process_memory, "可用内存:" + spaceMem);

        //点击小部件的进程总数和剩余控件总数的textview处,跳转到应用的主界面处
        Intent i = new Intent("android.intent.action.HOME");
        i.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_clear, pendingIntent);

        //设置桌面小部件上面的一键清理按钮的清理逻辑
        //发送一条清理内存的广播
        Intent intent = new Intent("android.action.KILL_PROCESS");

        //设置延迟意图
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent1);
        //让AppWidgetManager更新小部件的内容
        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        awm.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onDestroy() {
        if (mTimerTaskReceiver != null) {
            unregisterReceiver(mTimerTaskReceiver);
        }
        mTimer.cancel();
        mTimer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
