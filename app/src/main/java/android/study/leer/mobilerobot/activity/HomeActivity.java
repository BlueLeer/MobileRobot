package android.study.leer.mobilerobot.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.MD5util;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;


public class HomeActivity extends AppCompatActivity {

    private GridView gView;
    private View marqueenTextView;
    private String[] itemTitle;
    private int[] images;
    private String mPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("功能列表");

        //初始化界面UI
        initUI();

        //初始化界面的数据
        initData();
//        初始化有米广告
        initAD();


    }

    private void initAD() {
//         设置横竖图模式
        SpotManager.getInstance(this)
        .setImageType(SpotManager.IMAGE_TYPE_VERTICAL);

//          设置展示动画
        SpotManager.getInstance(this)
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        SpotManager.getInstance(this).showSpot(this,
                new SpotListener(){
                    @Override
                    public void onShowSuccess() {
                        Log.i("有米广告xxxxxxxxxxxxxxxxxx","广告展示成功");
                    }

                    @Override
                    public void onShowFailed(int i) {
                        Log.i("有米广告xxxxxxxxxxxxxxxxxx","广告展示失败");

                    }

                    @Override
                    public void onSpotClosed() {
                        Log.i("有米广告xxxxxxxxxxxxxxxxxx","广告关闭");

                    }

                    @Override
                    public void onSpotClicked(boolean b) {
                        Log.i("有米广告xxxxxxxxxxxxxxxxxx","广告被点击");

                    }
                });

        // 获取广告条
        View bannerView = BannerManager.getInstance(this)
                .getBannerView(this, new BannerViewListener(){
                    @Override
                    public void onRequestSuccess() {

                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });

// 获取要嵌入广告条的布局
        LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);

// 将广告条加入到布局中
        bannerLayout.addView(bannerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 如果有需要，可以点击后退关闭插播广告。
        if (SpotManager.getInstance(this).isSpotShowing()) {
            SpotManager.getInstance(this).hideSpot();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(this).onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 插屏广告
        SpotManager.getInstance(this).onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(this).onDestroy();
        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(this).onDestroy();
    }

    private void initData() {
        itemTitle = new String[]{"手机防盗","通讯卫士","软件管理","进程管理",
        "流量统计","手机杀毒","缓存清理","高级工具","设置中心"};

        images = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager,
                R.drawable.home_trojan,R.drawable.home_sysoptimize,
                R.drawable.home_tools,R.drawable.home_settings};

        gView.setAdapter(new MyAdapter());
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //获取存贮在SharePreference当中的手机防盗功能的密码
                        mPsd = SPUtil.getString(getApplicationContext(), ContantValues.MOBILE_SAVE_PSD);
                        showDialog();
                        break;
                    case 1:
                        //跳转到"通信卫士"功能列表
                        Intent i1 = BlackNumActivity.newIntent(HomeActivity.this);
                        startActivity(i1);
                        break;
                    case 2:
                        //跳转到"通信卫士"功能列表
                        Intent i2 = AppManagerActivity.newIntent(HomeActivity.this);
                        startActivity(i2);
                        break;
                    case 3:
                        //跳转到"通信卫士"功能列表
                        Intent i3 = ProcessManagerActivity.newIntent(HomeActivity.this);
                        startActivity(i3);
                        break;
                    case 4:
                        //跳转到"通信卫士"功能列表
                        Intent i4 = TrafficActivity.newIntent(HomeActivity.this);
                        startActivity(i4);
                        break;
                    case 5:
                        //跳转到"病毒查杀"功能列表
                        Intent i5 = ScanVirusActivity.newIntent(HomeActivity.this);
                        startActivity(i5);
                        break;
                    case 6:
                        //跳转到"病毒查杀"功能列表
                        ToastUtil.show(getApplicationContext(),"对不起,该模块正在填坑中...");
                        break;
                    case 7:
                        Intent i7 = AToolsActivity.newIntent(HomeActivity.this);
                        startActivity(i7);
                        break;
                    case 8:
                        Intent i8 = SettingActivity.newIntent(getApplicationContext());
                        startActivity(i8);
                        break;
                    default:
                        break;
                }

            }
        });

    }

    private void showDialog() {
        //设置初始密码
        if(TextUtils.isEmpty(mPsd)){
            settingPSDDialog();
        }else {
            //输入确认密码
            confirmPSDDialog();
        }

    }

    /**
     * 进入“手机防盗”功能模块的设置界面
     */
    private void enterMobileSafe() {
        boolean isMobileSafeSetted = SPUtil.getBoolean(this, ContantValues.ISMOBILESAFESETTED);
        if(!isMobileSafeSetted){
            //如果手机防盗功能未设置完毕,则直接进入手机防盗功能列表界面
            Intent intent = MobileSafeActivityOne.newIntent(this);
            startActivity(intent);
        }else{
            //如果手机防盗功能已经设置完毕,则直接进入手机防盗功能设置完成后的界面
            Intent intent = MobileSafeActivityOver.newIntent(this);
            startActivity(intent);
        }
    }

    private void confirmPSDDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        final AlertDialog confirmDialog = builder1.create();
        View confirmPSDView = View.inflate(this, R.layout.confirm_psd_view,null);
        final EditText ed = (EditText) confirmPSDView.findViewById(R.id.et_confirm_psd);

        //给对话框中的view不加上下左右的4个外边距
        confirmDialog.setView(confirmPSDView,0,0,0,0);
        confirmDialog.show();

        Button confirmBT = (Button)confirmPSDView.findViewById(R.id.bt_confirm);
        Button cancelBT = (Button) confirmPSDView.findViewById(R.id.bt_cancel);

        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmPSD = ed.getText().toString();
                if(MD5util.encoder(confirmPSD).equals(mPsd)){
                    ToastUtil.show(getApplicationContext(),"密码输入正确...");
                    //直接进入到手机防盗功能的设置模块界面
                    enterMobileSafe();
                    confirmDialog.dismiss();
                }else{
                    ToastUtil.show(getApplicationContext(),"密码输入错误...");
                    confirmDialog.dismiss();
                }
            }
        });

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });


    }

    private void settingPSDDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog settingDialog = builder.create();

        //alertdialog的界面的view
        final View setPSDView = View.inflate(this, R.layout.set_psd_view,null);
        final EditText enterET = (EditText) setPSDView.findViewById(R.id.et_enter_psd);
        final EditText confirmET = (EditText) setPSDView.findViewById(R.id.et_comfirm_psd);


        settingDialog.setView(setPSDView);
        settingDialog.show();

        Button confirmBT = (Button)setPSDView.findViewById(R.id.bt_confirm);
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterString = enterET.getText().toString();
                String confirmString = confirmET.getText().toString();
                if(!TextUtils.isEmpty(enterString) && !TextUtils.isEmpty(confirmString)){
                    if(enterString.equals(confirmString)){
                        ToastUtil.show(getApplicationContext(),"密码已设定...");
                    //直接进入到手机防盗功能的设置模块界面
                        enterMobileSafe();
                        settingDialog.dismiss();
                        SPUtil.storageString(getApplicationContext(), ContantValues.MOBILE_SAVE_PSD, MD5util.encoder(enterString));
                    }else {
                        ToastUtil.show(getApplicationContext(),"密码设置出错!");
                        settingDialog.dismiss();
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),"密码设置出错!");
                    settingDialog.dismiss();
                }

            }
        });

        Button btCancel = (Button) setPSDView.findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.dismiss();
            }
        });
    }


    private void initUI() {
        marqueenTextView = findViewById(R.id.marquee_text_view);
        gView = (GridView) findViewById(R.id.item_view);

    }

    public static void newIntent(Context context){
        Intent intent = new Intent(context,HomeActivity.class);
        context.startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return itemTitle.length;
        }

        @Override
        public Object getItem(int position) {
            return itemTitle[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),R.layout.item_view,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.background_id);
            TextView textView = (TextView) view.findViewById(R.id.title_id);
            imageView.setImageResource(images[position]);
            textView.setText(itemTitle[position]);
            return view;
        }
    }

}
