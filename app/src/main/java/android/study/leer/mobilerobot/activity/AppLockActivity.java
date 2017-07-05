package android.study.leer.mobilerobot.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.db.AppLockCrudDB;
import android.study.leer.mobilerobot.engine.AppInfoProvider;
import android.study.leer.mobilerobot.utils.AppInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/3/13.
 */
public class AppLockActivity extends Activity {
    private TextView tv_lock;
    private TextView tv_unlock;
    private TextView tv_unlock_count;
    private TextView tv_lock_count;
    private ListView lv_unlock;
    private ListView lv_lock;
    private LinearLayout ll_unlock;
    private LinearLayout ll_lock;
    private AppLockCrudDB mAppLockCrudDB;
    private List<AppInfo> mLockAppList;
    private List<AppInfo> mUnLockAppList;

    public MyAdapter mLockAdapter;
    private MyAdapter mUnlockAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mUnlockAdapter = new MyAdapter(false);
            mLockAdapter = new MyAdapter(true);

            lv_lock.setAdapter(mLockAdapter);
            lv_unlock.setAdapter(mUnlockAdapter);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();

        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mAppLockCrudDB = AppLockCrudDB.getAppLockCrudDB(AppLockActivity.this);
                //已加锁应用列表
                mLockAppList = new ArrayList<>();
                //未加锁应用列表
                mUnLockAppList = new ArrayList<>();

                //获取数据库当中所有已经加锁的应用的列表
                List<String> lockAppList = mAppLockCrudDB.queryAll();
                //获取所有的已经安装的程序列表
                List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //将数据库当中存储的已加锁的应用和已安装所有的应用程序进行比对,如果存在就放入已加锁的程序列表中
                for (AppInfo appInfo : appInfoList) {
                    if (lockAppList.contains(appInfo.getAppPackage())) {
                        mLockAppList.add(appInfo);
                    } else {
                        mUnLockAppList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    private class MyAdapter extends BaseAdapter {
        boolean mIsLock;

        public MyAdapter(boolean isLock) {
            mIsLock = isLock;
        }

        @Override
        public int getCount() {
            if (mIsLock) {
                return mLockAppList.size();
            } else {
                return mUnLockAppList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (mIsLock) {
                return mLockAppList.get(position);
            } else {
                return mUnLockAppList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.app_lock_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_app_lock_icon = (ImageView)
                        convertView.findViewById(R.id.iv_app_lock_icon);
                viewHolder.iv_app_lock_lock = (ImageView)
                        convertView.findViewById(R.id.iv_app_lock_lock);
                viewHolder.tv_app_lock_name = (TextView)
                        convertView.findViewById(R.id.tv_app_lock_name);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final View view = convertView;
            AppInfo appInfo = getItem(position);
            viewHolder.tv_app_lock_name.setText(appInfo.getAppName());
            viewHolder.iv_app_lock_icon.setImageDrawable(appInfo.getAppIcon());

            //注册点击事件,添加加锁应用和移除加锁应用
            viewHolder.iv_app_lock_lock.setOnClickListener(new View.OnClickListener() {
                AppInfo appInfo = getItem(position);
                @Override
                public void onClick(View v) {

                    //定义一个动画
                    //自己的相对位置,x起始位置
                    //自己的相对位置,x终止位置
                    //自己的相对位置,y的起始位置
                    //自己的相对位置,y的终止位置
                    TranslateAnimation animation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,0,
                            Animation.RELATIVE_TO_SELF,1,
                            Animation.RELATIVE_TO_SELF,0,
                            Animation.RELATIVE_TO_SELF,0);
                    //设置动画的时间
                    animation.setDuration(300);
                    //给contentView开启一个动画
                    view.startAnimation(animation);
                    //设置动画播放完毕以后执行的任务
                    //动画的执行过程是一个阻塞过程,应该在动画执行完毕之后,再从数据列表中移除相关的数据
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mIsLock){
                                //如果点击的是已加锁的应用,就将当前条目对应的条目移除,并且添加到未加锁的应用中

                                mLockAppList.remove(appInfo);
                                mUnLockAppList.add(0,appInfo);
                                //将当前新添加的应用从数据库当中删除
                                mAppLockCrudDB.delete(appInfo.getAppPackage());

                                mLockAdapter.notifyDataSetChanged();
                                mUnlockAdapter.notifyDataSetChanged();

                            }else{
                                mUnLockAppList.remove(appInfo);
                                mLockAppList.add(0,appInfo);
                                //将当前新添加的应用放入数据库当中
                                mAppLockCrudDB.insert(appInfo.getAppPackage());
                                mUnlockAdapter.notifyDataSetChanged();
                                mLockAdapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });
            if(mIsLock){
                viewHolder.iv_app_lock_lock.setImageResource(R.drawable.lock);
            }else{
                viewHolder.iv_app_lock_lock.setImageResource(R.drawable.unlock);
            }
            return convertView;
        }
    }


    static class ViewHolder {
        ImageView iv_app_lock_icon;
        TextView tv_app_lock_name;
        ImageView iv_app_lock_lock;
    }

    private void initUI() {
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);

        tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
        tv_lock_count = (TextView) findViewById(R.id.tv_lock_count);

        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        tv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);

                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
            }
        });
        tv_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_unlock.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_left_default);

                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, AppLockActivity.class);
        return i;
    }
}
