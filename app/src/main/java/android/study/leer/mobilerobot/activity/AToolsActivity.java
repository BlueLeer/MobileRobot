package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.BackupSms;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Leer on 2017/2/24.
 */
public class AToolsActivity extends AppCompatActivity{

    private TextView mQueryPhoneTV;
    private TextView tv_backup_sms;
    private TextView tv_common_number_query;
    private TextView tv_app_lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_tool_activity_layout);

        //电话号码归属地查询
        initQueryAddress();
        //短信备份
        initBackUpSms();
        //常用号码查询
        initCommonNumber();
        //程序锁
        initApplock();

    }

    private void initApplock() {
        tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = AppLockActivity.newIntent(getApplicationContext());
                startActivity(i);
            }
        });
    }

    private void initCommonNumber() {
        tv_common_number_query = (TextView) findViewById(R.id.tv_common_number_query);
        tv_common_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到差用号码查询界面
                Intent i = CommonNumberQueryActivity.newIntent(AToolsActivity.this);
                startActivity(i);
            }
        });
    }

    /**
     * 备份短信的工具
     */
    private void initBackUpSms() {
        tv_backup_sms = (TextView) findViewById(R.id.tv_backup_sms);
        tv_backup_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
            }
        });
    }

    private void showProgress() {
        //展示progressBar,并且跟随短信备份的进度备份短信
        final ProgressBar pb_backup_sms = (ProgressBar) findViewById(R.id.pb_backup_sms);

        final String path = Environment.getExternalStorageDirectory()+ File.separator+"smss.xml";
        new Thread(){
            @Override
            public void run() {

                BackupSms.backUpSms(getApplicationContext(), path, new BackupSms.Callback() {
                    @Override
                    public void setMax(int max) {
                        pb_backup_sms.setMax(max);
                    }

                    @Override
                    public void setProgressIndex(int index) {
                        pb_backup_sms.setProgress(index);
                    }
                });
            }
        }.start();

    }

    /**
     * 查询号码归属地的工具
     */
    private void initQueryAddress() {
        mQueryPhoneTV = (TextView) findViewById(R.id.tv_query_phone_add);
        mQueryPhoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = QueryPhoneAddActivity.newIntent(AToolsActivity.this); ;
                startActivity(i);
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context,AToolsActivity.class);
        return i;

    }
}
