package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.AppInfo;
import android.study.leer.mobilerobot.engine.AppInfoProvider;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Leer on 2017/3/8.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener{

    private ListView lv_app;
    private List<AppInfo> mAppInfoList;

    private List<AppInfo> mCustomerAppList = new ArrayList<>();
    private List<AppInfo> mSystemAppList = new ArrayList<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mAppInfoList != null && lv_app != null){
                lv_app.setAdapter(new MyAdapter());
            }
        }
    };
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;


    private class MyAdapter extends BaseAdapter{

        @Override
        public int getViewTypeCount() {
            //告知一共有几种类型的item预加载的view,这样,在contentView缓存的时候就能够区分开来
            return super.getViewTypeCount() + 1;
        }

        //view显示的类型有两种
        @Override
        public int getItemViewType(int position) {
            if(position == 0 || position == mCustomerAppList.size()+1){
                return 0;
            }else{
                return 1;
            }
        }

        //条目的总数为加上额外的两个标题显示
        @Override
        public int getCount() {
            return mCustomerAppList.size() + mSystemAppList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if(position == 0 || position == mCustomerAppList.size()+1){
                return null;
            }else{
                if(position < mCustomerAppList.size()+1){
                    return mCustomerAppList.get(position-1);
                }else{
                    return mSystemAppList.get(position-2 - mCustomerAppList.size());
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(getItemViewType(position) == 0){
                TitleHolder holder;
                if(convertView == null){
                    holder = new TitleHolder();
                    convertView = View.inflate(getApplicationContext(),R.layout.lv_app_info_title,null);
                    holder.tv_app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(holder);
                }else{
                    holder = (TitleHolder) convertView.getTag();
                }

                if(position != 0){
                    holder.tv_app_title.setText("系统应用");
                }else{
                    holder.tv_app_title.setText("用户应用");
                }

                return convertView;
            }else{
                ViewHolder viewHolder;
                if(convertView == null){
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(AppManagerActivity.this,R.layout.lv_app_info,null);

                    viewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                    viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tv_app_path = (TextView) convertView.findViewById(R.id.tv_app_path);

                    convertView.setTag(viewHolder);
                }else{
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                AppInfo a = getItem(position);
                Drawable d = a.getAppIcon();
                ImageView im = viewHolder.iv_app_icon;
                viewHolder.iv_app_icon.setImageDrawable(getItem(position).getAppIcon());
                viewHolder.tv_app_name.setText(getItem(position).getAppName());

                if(getItem(position).isSystem()){
                    viewHolder.tv_app_path.setText("系统应用");
                }else{
                    viewHolder.tv_app_path.setText("用户应用");

                }
            }

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_path;
    }

    static class TitleHolder{
        TextView tv_app_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initMemoryView();
        initAppList();
    }

    private void initAppList() {
        lv_app = (ListView) findViewById(R.id.lv_app);
        final TextView tv_app_desp_title = (TextView) findViewById(R.id.tv_app_desp_title);
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mCustomerAppList != null && mSystemAppList != null){
                    //滚动到了系统的条目
                    if(firstVisibleItem>=mCustomerAppList.size()+1){
                        tv_app_desp_title.setText("系统应用");
                    }else{
                        tv_app_desp_title.setText("用户应用");
                    }
                }
            }
        });

        initListViewData();

        //注册每一个条目的点击事件
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击了系统应用和用户应用的title的时候什么都不做
                if(position == 0 || position == mAppInfoList.size()+1){
                    return;
                }
                //点击了非title的时候就弹出popupwindow
                else{
                    showPopupWindow(view);
//                    if(position<mCustomerAppList.size()+1){
//                        mAppInfo = mCustomerAppList.get(position-1);
//                    }else{
//                        mAppInfo = mSystemAppList.get(position-2 - mCustomerAppList.size());
//                    }

                    mAppInfo = (AppInfo) lv_app.getAdapter().getItem(position);
                }

            }
        });
    }

    private void showPopupWindow(View view) {
        View v = View.inflate(getApplicationContext(),R.layout.popupwindow_layout,null);
        TextView tv_start = (TextView) v.findViewById(R.id.tv_start);
        TextView tv_uninstall = (TextView) v.findViewById(R.id.tv_uninstall);
        TextView tv_share = (TextView) v.findViewById(R.id.tv_share);

        tv_share.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_uninstall.setOnClickListener(this);

        //将v挂在popupWindow上面
        mPopupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //在挂载的view的下方展示(挂载的父view,水平方向上的偏转,垂直方向上的偏转)
        mPopupWindow.showAsDropDown(view,50,-view.getHeight());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start:
                //启动指定的包名的应用
                PackageManager pm = getPackageManager();
                Intent i1 = pm.getLaunchIntentForPackage(mAppInfo.getAppPackage());
                startActivity(i1);
                break;
            case R.id.tv_uninstall:
                //卸载指定包名的应用
                Intent i2 = new Intent("android.intent.action.DELETE");
                i2.addCategory("android.intent.category.DEFAULT");
                //这里传入的是完整的包的路径名,因此前面要加上package:
                i2.setData(Uri.parse("package:"+mAppInfo.getAppPackage()));
                startActivity(i2);
                break;
            case R.id.tv_share:
                //分享应用
                Intent i3 = new Intent(Intent.ACTION_SEND);
                i3.setType("text/plain");
                i3.putExtra(Intent.EXTRA_TEXT,"快来使用APP:"+mAppInfo.getAppName());
                i3.putExtra(Intent.EXTRA_SUBJECT,"发现了一个好应用,快来使用吧");
                //创建选择器
                Intent i = Intent.createChooser(i3,"请选择要分享至那个应用:");
                startActivity(i);
                break;
        }

        if(mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }

    private void initListViewData() {
        new Thread(){
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(AppManagerActivity.this);
                for (AppInfo appInfo:mAppInfoList){
                    if(appInfo.isSystem()){
                        mSystemAppList.add(appInfo);
                    }else{
                        mCustomerAppList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initMemoryView() {
        TextView tv_system_memory = (TextView) findViewById(R.id.tv_system_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
        //获取系统内存的路径
        String systemMemPath = Environment.getDataDirectory().getAbsolutePath();
//
        //获取sd卡内存的路径
        String sdMemPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String systemSpace = getAvailSpace(systemMemPath);
        String sdSpace = getAvailSpace(sdMemPath);
        tv_system_memory.setText("系统可用内存:"+systemSpace);
        tv_sd_memory.setText("SD可用内存:"+sdSpace);
    }

    /**
     * @param path 想要查看内存的路径
     * @return Formatter类格式化了的可用磁盘的大小
     */
    private String getAvailSpace(String path){
        //获取可用磁盘大小类
        StatFs statFs = new StatFs(path);
        //获取可用区块个数
        long blocks = statFs.getAvailableBlocks();
        //获取每个可用区块的大小
        long size = statFs.getBlockSize();

        String space = Formatter.formatFileSize(this,blocks*size);
        return space;

    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, AppManagerActivity.class);
        return i;
    }
}
