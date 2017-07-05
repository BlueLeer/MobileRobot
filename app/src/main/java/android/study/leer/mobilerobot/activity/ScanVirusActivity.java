package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.db.VirusCrudDB;
import android.study.leer.mobilerobot.utils.MD5util;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Leer on 2017/3/14.
 */
public class ScanVirusActivity extends Activity{

    private static final int SCANNING = 100;
    private static final int SCAN_FINISH = 101;
    private ImageView iv_scan_icon;
    private TextView tv_scan_app_name;
    private ProgressBar pb_scan;
    private LinearLayout ll_scan;
    private List<String> mVirusMD5FormDB;
    private List<VirusInfo> mVirusInfoList;
    private List<VirusInfo> mScanVirusList;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANNING:
                    VirusInfo scanApp = (VirusInfo) msg.obj;
                    String appName = scanApp.appName;
                    tv_scan_app_name.setText(appName);

                    TextView textView = new TextView(getApplicationContext());
                    if(scanApp.isVirus){
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒:"+scanApp.appName);
                    }else{
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全:"+scanApp.appName);
                    }
                    ll_scan.addView(textView,0);

                    break;

                case SCAN_FINISH:
                    tv_scan_app_name.setText("扫描完成");
                    //扫描的动画结束
                    iv_scan_icon.clearAnimation();

                    //如果发现了病毒,应该启动卸载应用的界面
//                    <action android:name="android.intent.action.VIEW" />
//                    <action android:name="android.intent.action.DELETE" />
//                    <category android:name="android.intent.category.DEFAULT" />
//                    <data android:scheme="package" />
                    for(VirusInfo virusInfo:mVirusInfoList){
                        String appPackageName = virusInfo.appPackageName;

                        //启动卸载识别为病毒的应用
                        Intent i = new Intent();
                        i.setAction("android.intent.action.VIEW");
                        i.setAction("android.intent.action.DELETE");
                        i.addCategory("android.intent.category.DEFAULT");
                        i.setData(Uri.parse("package:"+appPackageName));
                        startActivity(i);
                    }
                    break;
            }
        }
    };
    private RotateAnimation mRotateAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_virus);

        //初始化UI界面
        initUI();

        //初始化动画
        initAnimation();

        //进行病毒查杀
        killVirus();

    }

    private void initAnimation() {
        mRotateAnimation = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        //动画的显示时间是永久显示
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setDuration(1000);
    }

    private void killVirus() {
        new Thread(){
            @Override
            public void run() {
                //开启动画
                iv_scan_icon.startAnimation(mRotateAnimation);
                //创建扫描出病毒的应用列表
                mVirusInfoList = new ArrayList<>();

                //创建处已经扫描过的所有的应用
                mScanVirusList = new ArrayList<>();

                mVirusMD5FormDB = VirusCrudDB.getVirus();
                //获取所用的应用
                PackageManager pm = getPackageManager();
                //获取所有已经安装的和未安装的应用的列表
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);

                //设置ProgressBar的最大值
                pb_scan.setMax(packageInfoList.size());


                int scanIndex = 0;
                for(PackageInfo info:packageInfoList){
                    Signature[] signatures = info.signatures;
                    Signature signature = signatures[0];
                    //将signature转化成字符串
                    String sinStr = signature.toCharsString();
                    //将此字符串转化成MD5码
                    String fromAppSignare = MD5util.encoder(sinStr);

                    VirusInfo scanedApp = new VirusInfo();
                    //维护已经扫描的应用的应用名称
                    scanedApp.appName = info.applicationInfo.loadLabel(pm).toString();
                    //维护已经扫描的应用的应用包名
                    scanedApp.appPackageName = info.packageName;

                    if(scanedApp.appPackageName.equals(getPackageName())){
                        Log.i("xxxxxxxxxx",fromAppSignare);
                    }

                    //将此MD5码和数据库中的已经存在的MD5码进行匹配,如果数据库中包含了此MD5码
                    //说明此应用是一个病毒
                    if(mVirusMD5FormDB.contains(fromAppSignare)){
                        scanedApp.isVirus = true;
                        //存在数据库中,说明是一个病毒,将其添加到病毒列表中
                        mVirusInfoList.add(scanedApp);
                    }else{
                        scanedApp.isVirus = false;
                    }

                    scanIndex++;
                    pb_scan.setProgress(scanIndex);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mScanVirusList.add(scanedApp);
                    //每扫描完一个应用,就将该应用的信息以消息的形式发送出去,通知更新UI
                    Message message = new Message();
                    message.what = SCANNING;
                    message.obj = scanedApp;
                    mHandler.sendMessage(message);
                }

                Message scanOverMessage = new Message();
                scanOverMessage.what = SCAN_FINISH;
                mHandler.sendMessage(scanOverMessage);

            }
        }.start();

    }

    private class VirusInfo{
        String appName;
        String appPackageName;
        boolean isVirus;
    }

    private void initUI() {
        iv_scan_icon = (ImageView) findViewById(R.id.iv_scan_icon);
        tv_scan_app_name = (TextView) findViewById(R.id.tv_scan_app_name);
        pb_scan = (ProgressBar) findViewById(R.id.pb_scan);
        ll_scan = (LinearLayout) findViewById(R.id.ll_scan);

    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ScanVirusActivity.class);
        return i;
    }
}
