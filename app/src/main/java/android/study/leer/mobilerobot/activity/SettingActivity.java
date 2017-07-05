package android.study.leer.mobilerobot.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.service.AppLockService;
import android.study.leer.mobilerobot.service.BlackNumberService;
import android.study.leer.mobilerobot.service.MyRocketService;
import android.study.leer.mobilerobot.service.PhoneAddressService;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ServiceUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.study.leer.mobilerobot.widget.MySettingView1;
import android.study.leer.mobilerobot.widget.MySettingView2;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Leer on 2017/2/16.
 */

public class SettingActivity extends AppCompatActivity {

    private MySettingView1 myUpdateView;
    private MySettingView1 myPhoneOfPlaceiew;
    private MySettingView2 mToastStyleView;
    private MySettingView2 mToastLocationView;
    private String[] mStyles;
    private AlertDialog mSelectDialog;
    private MySettingView1 mRocketView;
    private MySettingView1 mBlackNumberView;
    private MySettingView1 app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //初始化"设置中心"-"开启自动更新"第一个条目的UI
        initUpdate(ContantValues.UPPDARE_CB_ISCHECKED);

        //初始化"设置中心"-"显示号码归属地查询"第二个条目的UI
        //电话号码归属地查询是否已经开启,应该符合实际,也就是看后台是否已经开起了这个服务,
        //因此,不能直接从SP当中获取以前的开启状态
        initPhoneOfPlace();

        //始化"设置中心",第三个设置条目"归属地显示样式"
        intPhoneAddressStyle();

        //初始化"设置中心",第四个设置条目"归属地提示框位置"
        initToastLocationView();

        //初始化"开启桌面小火箭人"选项
        initRocketMan();

        //初始化黑名单拦截选项
        initBlackNumber();

        //开启程序锁
        initAppLock();

    }

    private void initAppLock() {
        app_lock = (MySettingView1) findViewById(R.id.app_lock);
        clickUpdate(null,app_lock);
    }

    private void initBlackNumber() {
        mBlackNumberView = (MySettingView1) findViewById(R.id.black_number_setting_view);
        clickUpdate(null,mBlackNumberView);
    }

    private void initRocketMan() {
        mRocketView = (MySettingView1) findViewById(R.id.open_rocket);
        clickUpdate(ContantValues.ROCKET_MAN_ISSTART,mRocketView);
    }

    private void initToastLocationView() {
        mToastLocationView = (MySettingView2) findViewById(R.id.phone_address_toast_location);
        mToastLocationView.init("归属地提示框位置","设置归属地提示框位置");
        mToastLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击时跳转到另外一个activity
                Intent i = ToastLocationActivity.newIntent(SettingActivity.this);
                startActivity(i);
            }
        });
    }

    /**
     * 初始化"设置中心",第三个设置条目"归属地显示样式"
     */
    private void intPhoneAddressStyle() {
        mToastStyleView = (MySettingView2) findViewById(R.id.phone_address_toast_style_select);
        mStyles = new String[]{"透明","橙色","蓝色","灰色","绿色"};

        int loc = SPUtil.getInt(this,ContantValues.STYLE_INDEX);
        mToastStyleView.init("设置来电归属地显示风格",mStyles[loc]);

        mToastStyleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SettingActivity.this,"请选择显示主题");
                showSelectStylesDialog();
            }
        });
    }

    /**
     * 显示选择归属地显示样式的dialog
     */
    private void showSelectStylesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择归属地显示样式");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(mStyles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.storageInt(SettingActivity.this,ContantValues.STYLE_INDEX,which);
                //第三个设置条目的回显,回显已经选择的主题样式
                mToastStyleView.init("设置来电归属地显示风格",mStyles[which]);
            }
        });
        mSelectDialog = builder.create();
        mSelectDialog.show();
    }

    /** 初始化"号码归属地显示设置"是否开启
     *
     */
    private void initPhoneOfPlace() {
        myPhoneOfPlaceiew = (MySettingView1) findViewById(R.id.phone_of_place_id);
        //获取当前服务状态是否已经开启

        clickUpdate(null,myPhoneOfPlaceiew);
    }

    /** 初始化"开启软件自动检查更新"是否开启
     * @param key 存储在sp当中自动检查更新是否开启的key
     */
    private void initUpdate(String key) {
        myUpdateView = (MySettingView1) findViewById(R.id.update_id);
        clickUpdate(key,myUpdateView);
    }

    /** 负责点击状态的监听
     * @param key 存贮在sp当中的各项状态的开启 key
     * @param settingView "设置中心"中每一个条目的 ItemView
     */
    private void clickUpdate(final String key, final MySettingView1 settingView){
        //从sharepreference中读取CheckBox的选取状态
        boolean cb_isChecked = SPUtil.getBoolean(getApplicationContext(),key);

        //归属地查询是否开启应该和后台负责归属地查询的服务绑定,
        //如果点击的view是myPhoneOfPlaceiew的话,就获取它绑定服务的开启的状态
        if(settingView == myPhoneOfPlaceiew){
            cb_isChecked = ServiceUtil.getServiceState(SettingActivity.this,"android.study.leer.mobilerobot.service.PhoneAddressService");
        }

        if(settingView == mBlackNumberView){
            cb_isChecked = ServiceUtil.getServiceState(SettingActivity.this,"android.study.leer.mobilerobot.service.BlackNumberService");
        }

        //判断当前火箭人是否已经开启,应该根据开启的实际情况来做判断
        if(settingView == mRocketView){
            cb_isChecked = ServiceUtil.getServiceState(SettingActivity.this,
                    "android.study.leer.mobilerobot.service.MyRocketService");
        }


        if(settingView == app_lock){
            cb_isChecked = ServiceUtil.getServiceState(SettingActivity.this,
                    "android.study.leer.mobilerobot.service.AppLockService");
        }

        //初始化条目的选中状态
        if(cb_isChecked){
            settingView.setCheck(cb_isChecked);
        }

        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = settingView.getIsCheck();
                settingView.setCheck(!isCheck);

                //下面这段代码是归属地查询的开启与关闭的逻辑
                Intent i = PhoneAddressService.newIntent(SettingActivity.this);
                if(!isCheck && settingView == myPhoneOfPlaceiew){
                    //开启"显示归属地查询",开启一个服务
                    startService(i);
                }else if(isCheck && settingView == myPhoneOfPlaceiew){
                    stopService(i);
                }

                //是否开启加速小火箭的服务
                Intent intent = MyRocketService.newIntent(SettingActivity.this);
                if(!isCheck && settingView == mRocketView){
                    //开启一个服务
                    startService(intent);
                }else{
                    stopService(intent);
                }

                //开启和关闭拦截黑名单中电话或者短信的服务
                Intent intent1 = BlackNumberService.newIntent(SettingActivity.this);
                if(!isCheck && settingView == mBlackNumberView){
                    startService(intent1);
                }else{
                    stopService(intent1);
                }

                //开启程序锁的逻辑
                Intent intent2 = AppLockService.newIntent(SettingActivity.this);
                if(!isCheck && settingView == app_lock){
                    startService(intent2);
                }else{
                    stopService(intent2);
                }

                SPUtil.storageBoolean(SettingActivity.this,key,!isCheck);
            }
        });
    }

    public static Intent newIntent(Context context){
        return new Intent(context,SettingActivity.class);
    }
}
