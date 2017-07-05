package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.HttpUtil;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TextView mVersionNameTX;
    private int mCurrentVersionCode;
    private int mNewVersionCode;

    private static final int VERSION_UPDATE = 100;
    private static final int JUMP_TO_HOME = 101;
    private static final int CHECK_ERROR = 102;

    private String mNewVersionName;
    private String mNewVersionDes;
    private String mNewVersionDownloadUrl;

    private Handler mVersionCheckHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case VERSION_UPDATE:
                    //弹出对话框,并且提示下载
                    appearALertDialog();
                    break;
                case JUMP_TO_HOME:
                    //无需下载,跳转到主界面
                    jumpToMain();
                    ToastUtil.show(SplashActivity.this,"已是最新版本");
                    break;
                case CHECK_ERROR:
                    //版本更新检查失败,直接跳转到主界面
                    jumpToMain();
                    ToastUtil.show(SplashActivity.this,"检查版本更新失败");
            }
        }
    };


    /**
     * 当从服务器检测到新版本的时候弹出对话框,可以来选择更新或者不更新
     */
    private void appearALertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新啦~~~");
        builder.setMessage("发现最新版本:"+mNewVersionName+"\n"+mNewVersionDes);

        //点击"下载更新"时,执行下载任务
        builder.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始下载APK
                downloadApk();
            }
        });

        //点击"下次再说"时,回到主界面
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //直接跳转到应用的主界面
                jumpToMain();
            }
        });

        //取消对话框的时候,返回主界面
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                jumpToMain();
            }
        });

        builder.show();
    }

    /**
     * 下载服务端的最新的apk
     */
    private void downloadApk() {
        RequestParams params = new RequestParams(mNewVersionDownloadUrl);
        ToastUtil.show(this,"开始下载");
        jumpToMain();

        //若外部的存贮卡可用,则将请求下载以后的apk放置在path目录的文件中
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String path = Environment.getExternalStorageDirectory().getPath()+File.separator+"mobilerobot.apk";
            params.setSaveFilePath(path);
            params.setAutoRename(true);
        }
        showNotification();
        x.http().get(params, new Callback.CommonCallback<File>() {
            @Override
            public void onSuccess(File result) {
                ToastUtil.show(SplashActivity.this,"apk下载成功!");
                //下载完成后自动安装Apk
                installApk(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.show(SplashActivity.this,"Apk下载失败!"+ex.getStackTrace());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                ToastUtil.show(SplashActivity.this,"Apk下载中断!");
            }

            @Override
            public void onFinished() {
                ToastUtil.show(SplashActivity.this,"Apk将要自动安装");
            }

        });

    }

    /**
     * 当启动下载时,会在状态栏弹出"正在下载的消息"
     */
    private void showNotification(){
        Notification notification = new Notification
                .Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_splash)
                .setTicker("应用正在下载...")
                .setContentTitle("请您稍等....")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);

    }

    /**
     * 安装下载的apk
     * @param file 提供安装apk的文件
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        //启动一个隐式的activity
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivityForResult(intent,0);
    }

    /**重写父类中的onActivityResult()方法
     * 1.在这个方法中,暂时所做的逻辑是:启动安装apk的activity,当这个界面结束时,返回主界面
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        jumpToMain();
    }

    /**
     * 跳转到主界面
     */
    public void jumpToMain() {
        HomeActivity.newIntent(this);
        //当跳转到主界面以后,将splashactivity从回退栈中杀掉
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //有米广告需要用到的代码
        AdManager.getInstance(this)
                .init("d01477e3926f3bbc","37fed73a4452877c",
        true,true);

        //初始化UI
        updateUI();
        //初始化启动界面的应该显示的数据
        initData();

//        if(!SPUtil.getBoolean(this,ContantValues.SHORT_CUT)){
//            //生成快捷方式
//            addShortCut();
//        }
    }

    /**
     * 生成快捷方式
     */
    private void addShortCut() {
        //发送一条生成快捷方式的广播
        Intent i = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护在桌面显示的应用名称
        i.putExtra(Intent.EXTRA_SHORTCUT_NAME,"Leer手机卫士");
        //维护在桌面显示的应用的图标
        i.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        //维护点击图标以后跳转到的应用主界面
        Intent shortIntent = new Intent("android.intent.action.HOME");
        shortIntent.addCategory("android.intent.category.DEFAULT");

        i.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortIntent);
        sendBroadcast(i);

        SPUtil.storageBoolean(this,ContantValues.SHORT_CUT,true);
    }

    /**
     * 初始化UI
     */
    private void updateUI() {
        mVersionNameTX = (TextView) findViewById(R.id.tv_version_name);

    }

    /**
     * 初始化启动界面的该显示额数据
     */
    private void initData() {
        //1.初始化版本名称
        mVersionNameTX.setText("版本:"+getVersionName());
        //2.检查更新
        //获取当前应用的版本号
        mCurrentVersionCode = getmCurrentVersionCode();

        //应用启动的时候会自动发送一条请求,用来查看当前服务端的最新的版本是否和当前应用的版本号一致
        //这里在tomcat中模拟了返回的json数据,数据会包含:新版本号,新版本的名称,新版本的描述信息,以及新版本的下载地址
        //获取服务器端的版本号
        if(SPUtil.getBoolean(this,ContantValues.UPPDARE_CB_ISCHECKED) && isInternetConnected()) {
            checkVersion();
        }else{
            //现在发送消息出去,但是2000毫秒后再执行
            mVersionCheckHandler.sendEmptyMessageDelayed(JUMP_TO_HOME,2000);
        }

        //将查询号码归属地的数据库写入到file当中去
        writePhoneDB();
        //将常用号码的数据库写入到file当中去
        writeCommonPhoneDB();
        //将病毒数据库写入到file文件当中去
        writeVirusDB();


    }

    private void writeVirusDB() {
        //获取assets的管理者:AssetManager
        AssetManager am = getAssets();
        //获取软件下的文件根目录
        File fl = getFilesDir();
        //在上面的文件根目录下创建一个名称为"addrsss.db"的文件
        File file = new File(fl,"antivirus.db");

        InputStream in = null;
        FileOutputStream fos = null;
        if (!file.exists()){
            try {
                in = am.open("antivirus.db");
                fos = new FileOutputStream(file);
                byte[] bs = new byte[1024];
                //temp:实际读取的字节数,字节数为-1说明已经读取完毕了
                int temp = -1;
                while((temp = in.read(bs)) != -1){
                    fos.write(bs,0,temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(in != null && fos != null){
                    try {
                        fos.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeCommonPhoneDB() {
        //获取assets的管理者:AssetManager
        AssetManager am = getAssets();
        //获取软件下的文件根目录
        File fl = getFilesDir();
        //在上面的文件根目录下创建一个名称为"addrsss.db"的文件
        File file = new File(fl,"commonnum.db");

        InputStream in = null;
        FileOutputStream fos = null;
        if (!file.exists()){
            try {
                in = am.open("commonnum.db");
                fos = new FileOutputStream(file);
                byte[] bs = new byte[1024];
                //temp:实际读取的字节数,字节数为-1说明已经读取完毕了
                int temp = -1;
                while((temp = in.read(bs)) != -1){
                    fos.write(bs,0,temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(in != null && fos != null){
                    try {
                        fos.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writePhoneDB() {
        //获取assets的管理者:AssetManager
        AssetManager am = getAssets();
        //获取软件下的文件根目录
        File fl = getFilesDir();
        //在上面的文件根目录下创建一个名称为"addrsss.db"的文件
        File file = new File(fl,"address.db");

        InputStream in = null;
        FileOutputStream fos = null;
        if (!file.exists()){
            try {
                in = am.open("address.db");
                fos = new FileOutputStream(file);
                byte[] bs = new byte[1024];
                int temp = -1;
                while((temp = in.read(bs)) != -1){
                    fos.write(bs,0,temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(in != null && fos != null){
                    try {
                        fos.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    /**
     * @return 当前已经安装的APK的版本号
     */
    private int getmCurrentVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            //0代表获得最基础的包里的信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return 获取服务端的最新的版本号
     */
    private void checkVersion() {
        final String urlString = "http://16tk329255.iok.la:31608/test.jsp";
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Message message = new Message();
                String response = HttpUtil.getResponse(urlString);
                Log.i(TAG,"获取返回结果为 : "+response);
                try {
                    if(!TextUtils.isEmpty(response)) {
                        JSONObject jsonobject = new JSONObject(response);


                        JSONObject versionInfo = jsonobject.getJSONObject("versionInfo");
                        //从json中获取版本号
                        mNewVersionCode = Integer.parseInt(versionInfo.getString("versionCode"));
                        //从json中获取版本名称
                        mNewVersionName = versionInfo.getString("versionName");
                        //从json中获取新版本的描述信息
                        mNewVersionDes = versionInfo.getString("versionDes");
                        //从json中获取新版本APK的下载地址
                        mNewVersionDownloadUrl = versionInfo.getString("downloadUrl");

                        if(mCurrentVersionCode < mNewVersionCode && mNewVersionCode != 0){
                            message.what = VERSION_UPDATE;
                        }else{
                            message.what = JUMP_TO_HOME;
                        }
                    } else{
                         message.what = JUMP_TO_HOME;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = CHECK_ERROR;
                }
                long endTime = System.currentTimeMillis();
                //如果检查更新时间超过4秒钟,则直接跳转到主界面
                if(endTime-startTime > 4000){
                    jumpToMain();
                }else{
                    try {
                        Thread.sleep(4000 - (endTime - startTime));
                    }catch (InterruptedException e){
                        message.what = CHECK_ERROR;
                    }
                }
              //发送消息
                mVersionCheckHandler.obtainMessage(message.what).sendToTarget();

            }

        }).start();

    }



    /**获取当前应用的版本(VersionName)
     * @return 当前的版本名称(versionName),如果出现异常则返回null
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            //0代表获得最基础的包里的信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 判断当前网络是否可用
     * @return true表示可用,false表示不可用
     */
    public boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"##################活动已经销毁......");
    }
}
