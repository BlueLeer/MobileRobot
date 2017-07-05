package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.engine.ProcessInfo;
import android.study.leer.mobilerobot.engine.ProcessInfoProvider;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Leer on 2017/3/10.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener{

    private ListView lv_process_info;
    private List<ProcessInfo> mProcessInfoList;
    private MyAdapter mMyAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //给LIstView设置数据适配器
            if(lv_process_info != null){
                mMyAdapter = new MyAdapter();
                lv_process_info.setAdapter(mMyAdapter);
            }
        }
    };
    private List<ProcessInfo> mCustomerApps;
    private List<ProcessInfo> mSystemApps;
    private TextView tv_process_count;
    private TextView tv_memory_info;
    private int mProcessCount;
    private String mSpaceMem;
    private String mTotalMem;

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getItemViewType(int position) {
            if(position == 0 || position == mCustomerApps.size()+1){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            if(SPUtil.getBoolean(ProcessManagerActivity.this, ContantValues.SHOW_SYSTEM)){
                return mCustomerApps.size()+1;
            }else{
                return mCustomerApps.size() + mSystemApps.size() +2 ;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if(position == 0 || position == mCustomerApps.size()+1){
                return null;
            }else{
                if(position < mCustomerApps.size() + 1){
                    return mCustomerApps.get(position - 1);
                }else{
                    return mSystemApps.get(position - mCustomerApps.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TitleHolder titleHolder;
            if(getItemViewType(position) == 0) {
                if (convertView == null) {
                    titleHolder = new TitleHolder();
                    convertView = View.inflate(ProcessManagerActivity.this, R.layout.lv_title_layout, null);
                    titleHolder.tv_title = (TextView) convertView.findViewById(R.id.app_progress_desp);
                    convertView.setTag(titleHolder);
                } else {
                    titleHolder = (TitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    titleHolder.tv_title.setText("用户进程:" + mCustomerApps.size());
                } else {
                    titleHolder.tv_title.setText("系统进程:" + mSystemApps.size());
                }
            }else {
                ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(ProcessManagerActivity.this, R.layout.list_view_process_manager_item, null);
                    viewHolder.iv_process_app_icon = (ImageView) convertView.findViewById(R.id.iv_process_app_icon);
                    viewHolder.iv_process_app_name = (TextView) convertView.findViewById(R.id.iv_process_app_name);
                    viewHolder.iv_process_app_memory = (TextView) convertView.findViewById(R.id.iv_process_app_memory);
                    viewHolder.cb_check = (CheckBox) convertView.findViewById(R.id.cb_check);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                Drawable appIcon = getItem(position).getAppIcon();
                String appName = getItem(position).getAppName();
                String appMemory = getItem(position).getAppMemory();
                boolean isCheck = getItem(position).isCheck();

                viewHolder.iv_process_app_icon.setImageDrawable(appIcon);
                viewHolder.iv_process_app_name.setText(appName);
                viewHolder.iv_process_app_memory.setText(appMemory);
                if(getItem(position).getAppPackage().equals("android.study.leer.mobilerobot")){
                    viewHolder.cb_check.setVisibility(View.GONE);
                }
                viewHolder.cb_check.setChecked(isCheck);
            }

            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_process_app_icon;
        TextView iv_process_app_name;
        TextView iv_process_app_memory;
        CheckBox cb_check;
    }

    static class TitleHolder{
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMyAdapter != null){
            mMyAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getAppInfos(ProcessManagerActivity.this);
                mCustomerApps = new ArrayList<>();
                mSystemApps = new ArrayList<>();

                for(ProcessInfo processInfo:mProcessInfoList){
                    if(processInfo.isSystem()){
                        mSystemApps.add(processInfo);
                    }else{
                        mCustomerApps.add(processInfo);
                    }
                }

                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

        initTitle(tv_process_count, tv_memory_info);

        lv_process_info = (ListView) findViewById(R.id.lv_process_info);
        lv_process_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断当前选择的对象不是本应用
                ProcessInfo processInfo = (ProcessInfo) lv_process_info.getAdapter().getItem(position);
                if(!processInfo.getAppPackage().equals(getPackageName())){
                    processInfo.setCheck(!processInfo.isCheck());
                    CheckBox cb_check = (CheckBox) view.findViewById(R.id.cb_check);
                    cb_check.setChecked(processInfo.isCheck());
                }
            }
        });

        Button bt_select_all = (Button) findViewById(R.id.bt_select_all);
        Button bt_select_reserve = (Button) findViewById(R.id.bt_select_reserve);
        Button bt_clear = (Button) findViewById(R.id.bt_clear);
        Button bt_set = (Button) findViewById(R.id.bt_set);

        bt_select_all.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_set.setOnClickListener(this);
        bt_select_reserve.setOnClickListener(this);

    }

    private void initTitle(TextView tv_process_count, TextView tv_memory_info) {
        mSpaceMem = Formatter.formatFileSize(this, ProcessInfoProvider.getSpaceMemory(this));
        mTotalMem = Formatter.formatFileSize(this, ProcessInfoProvider.getTotalMemory(this));

        mProcessCount = ProcessInfoProvider.processCount(this);
        tv_process_count.setText("进程总数:"+ mProcessCount);
        tv_memory_info.setText("剩余/总共内存:"+ mSpaceMem +"/"
        + mTotalMem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_select_all:
                checkAll();
                break;
            case R.id.bt_select_reserve:
                reserveCheck();
                break;
            case R.id.bt_clear:
                clearProcess();
                break;
            case R.id.bt_set:
                setting();
                break;
        }
    }

    private void setting() {
        Intent i = ProcessSettingActivity.newIntent(this);
        startActivityForResult(i,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //刷新数据
        if(mMyAdapter != null){
            mMyAdapter.notifyDataSetChanged();
        }

        //获取
    }

    private void clearProcess() {
        //注意,在遍历集合的时候将集合中的元素去除的话,是各有漏洞的操作,详见"为知笔记"中的笔记

        List<ProcessInfo> killProcessList = new ArrayList<>();
        for(ProcessInfo processInfo:mProcessInfoList){
            if(processInfo.getAppPackage().equals(getPackageName())){
                //跳出本次循环
                continue;
            }
            if(processInfo.isCheck()){
                killProcessList.add(processInfo);
            }
        }

        //遍历killProcessList集合中的元素,然后从mCustomerList和mSystemList集合中都移除

        for (ProcessInfo processInfo:killProcessList){
            if(mSystemApps.contains(processInfo)){
                mSystemApps.remove(processInfo);
            }
            if(mCustomerApps.contains(processInfo)){
                mCustomerApps.remove(processInfo);
            }

            mProcessCount--;

            ProcessInfoProvider.killProcess(this,processInfo);
        }
        tv_process_count.setText("进程总数:"+ mProcessCount);
        tv_memory_info.setText("剩余/总共内存:"+ mSpaceMem +"/"
                +mTotalMem);

        mMyAdapter.notifyDataSetChanged();
    }

    private void reserveCheck() {
        for(ProcessInfo processInfo:mProcessInfoList){
            if (!processInfo.getAppPackage().equals(getPackageName())){
                processInfo.setCheck(!processInfo.isCheck());
            }
        }
        mMyAdapter.notifyDataSetChanged();
    }

    private void checkAll() {
        for(ProcessInfo processInfo:mProcessInfoList){
            if (!processInfo.getAppPackage().equals(getPackageName())){
                processInfo.setCheck(true);
            }
        }
        mMyAdapter.notifyDataSetChanged();
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ProcessManagerActivity.class);
        return i;
    }

    public class ProcessClearReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}

