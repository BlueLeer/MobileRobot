package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.service.AppLockService;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Leer on 2017/3/13.
 */
public class AppLockPsdActivity extends Activity{
    private static final String EXTRA_DATA = "apppackagename";

    private ImageView iv_app_icon;
    private TextView tv_app_name;
    private EditText et_app_lock_psd;
    private Button bt_app_lock_submit;

    private String mAppName;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从启动意图中获取启动的应用信息
        mPackageName = getIntent().getExtras().getString(EXTRA_DATA);

        PackageManager pm = getPackageManager();
        //获取想要启动应用的名称
        try {
            mAppName = pm.getApplicationInfo(mPackageName,0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_app_lock_psd);

        initUI();
    }

    private void initUI() {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(mPackageName,0);

            iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
            iv_app_icon.setImageDrawable(info.loadIcon(pm));

            tv_app_name = (TextView) findViewById(R.id.tv_app_name);
            tv_app_name.setText(mAppName);

            et_app_lock_psd = (EditText) findViewById(R.id.et_app_lock_psd);
            bt_app_lock_submit = (Button) findViewById(R.id.bt_app_lock_submit);

            //输入密码后判断密码的正误,正确的话就进入应用,否则退出应用
            bt_app_lock_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String psd = et_app_lock_psd.getText().toString();

                    if(!TextUtils.isEmpty(psd)){
                        if(psd.equals("123")){
                            //此时需要发送一条广播给看门狗服务,让它不要对此应用进行检测
                            Intent i = new Intent(ContantValues.BROADCAST_WATCH_SKIP);
                            i.putExtra("packagename",mPackageName);
                            sendBroadcast(i);

                            finish();

                        }else{
                            ToastUtil.show(getApplicationContext(),"输入密码错误");
                        }
                    }else{
                        ToastUtil.show(getApplicationContext(),"请输入密码");
                    }

                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        startActivity(i);
    }

    public static Intent newIntent(Context context, String appName){
        Intent i = new Intent(context, AppLockPsdActivity.class);
        i.putExtra(EXTRA_DATA,appName);
        return i;
    }
}
