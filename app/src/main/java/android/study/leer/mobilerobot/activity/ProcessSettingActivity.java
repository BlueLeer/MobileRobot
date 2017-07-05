package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.service.ProcessService;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ServiceUtil;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by Leer on 2017/3/11.
 */
public class ProcessSettingActivity extends Activity {

    private CheckBox cb_show_system_pro;
    private CheckBox cb_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initShowSystem();
        initClearWhenScreenOff();
    }

    private void initClearWhenScreenOff() {
        cb_clear = (CheckBox) findViewById(R.id.cb_clear);

        //获取服务真实的运行状态
        boolean isClearOn = ServiceUtil.getServiceState(this,"android.study.leer.mobilerobot.service.ProcessService");
        cb_clear.setChecked(isClearOn);
        if(isClearOn){
            cb_clear.setText("锁屏清理已开启");
        }else{
            cb_clear.setText("锁屏清理已关闭");
        }


        cb_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_clear.setText("锁屏清理已开启");
                    //开启服务
                    startService(ProcessService.newIntent(ProcessSettingActivity.this));

                }else{
                    cb_clear.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(ProcessService.newIntent(ProcessSettingActivity.this));
                }
                SPUtil.storageBoolean(ProcessSettingActivity.this,ContantValues.CLEAR_SCREEN_OFF,isChecked);
            }
        });
    }

    private void initShowSystem() {
        cb_show_system_pro = (CheckBox) findViewById(R.id.cb_show_system_pro);

        //回显过程
        boolean isShowSystem = SPUtil.getBoolean(this, ContantValues.SHOW_SYSTEM);
        cb_show_system_pro.setChecked(isShowSystem);

        if(isShowSystem){
            cb_show_system_pro.setText("开启隐藏系统进程");
        }else{
            cb_show_system_pro.setText("关闭隐藏系统进程");
        }
        cb_show_system_pro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_show_system_pro.setText("开启隐藏系统进程");

                }else{
                    cb_show_system_pro.setText("关闭隐藏系统进程");
                }

                SPUtil.storageBoolean(ProcessSettingActivity.this,ContantValues.SHOW_SYSTEM,isChecked);
            }
        });

        setResult(RESULT_OK);
    }


    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ProcessSettingActivity.class);
        return i;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
