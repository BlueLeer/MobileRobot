package android.study.leer.mobilerobot.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.db.BlackNumInfo;
import android.study.leer.mobilerobot.db.BlackNumberCrudDB;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/3/1.
 */
public class BlackNumActivity extends AppCompatActivity{

    private BlackNumberCrudDB mBlackNumberCrudDB;
    private List<BlackNumInfo> mBlackNumberList = new ArrayList<>();
    private ListView lv_phone;
    private Button bt_add;
    private MyAdapter mAdapte;
    //是否正在从数据库中读取数据,默认状态下数据没有正在加载
    private boolean isLoad = false;
    private int mBlackNumberCount;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mAdapte == null){
                mAdapte = new MyAdapter();
                lv_phone.setAdapter(mAdapte);
            }else{
                mAdapte.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_phonenum_activity);

        mBlackNumberCrudDB = BlackNumberCrudDB.getDBCrud(getApplicationContext());

        //初始化界面UI
        initUI();

        //初始化数据
        initData();
    }

    //ListView的优化:
    //1.复用convertView,避免多次加载布局文件
    //2.给convertView设置setTag(),避免多次findViewById()方法的调用
    //3.将ViewHolder设置成静态的,避免多次创建viewHolder对象,使它可以被共享
    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(),R.layout.list_view_item,null);
                viewHolder.mPhoneTV = (TextView) convertView.findViewById(R.id.tv_item_phone);
                viewHolder.mModeTV = (TextView) convertView.findViewById(R.id.tv_item_mode);
                viewHolder.mDeleteTV = (ImageView) convertView.findViewById(R.id.iv_item_delete);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }


            //删除对应的条目
            viewHolder.mDeleteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackNumberCrudDB.delete(mBlackNumberList.get(position).getPhoneNum());
                    mBlackNumberList.remove(position);
                    mAdapte.notifyDataSetChanged();
                }
            });

            viewHolder.mPhoneTV.setText(mBlackNumberList.get(position).getPhoneNum());
            String modeStr = "";
            switch (mBlackNumberList.get(position).getMode()){
                case "1":
                    modeStr = "拦截短信";
                    break;
                case "2":
                    modeStr = "拦截电话";
                    break;
                case "3":
                    modeStr = "拦截所有";
                    break;
            }
            viewHolder.mModeTV.setText(modeStr);

            return convertView;
        }
    }

    //单一实例,所有的类共享一个实例
    private static class ViewHolder{
        TextView mPhoneTV;
        TextView mModeTV;
        ImageView mDeleteTV;
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mBlackNumberCount = mBlackNumberCrudDB.getCount();
                mBlackNumberList = mBlackNumberCrudDB.query(mBlackNumberList.size()+"");
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add = (Button) findViewById(R.id.bt_add);
        lv_phone = (ListView) findViewById(R.id.lv_phone);
        lv_phone.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                //表示飞快的滑动
//                case SCROLL_STATE_FLING:
//                    break;
//                //表示处于空闲状态(也就是ListView没有滑动的时候)
//                case SCROLL_STATE_IDLE:
//                    break;
//                //表示用手指缓慢滑动的时候
//                case SCROLL_STATE_TOUCH_SCROLL:
//                    break;

                //当处于空闲状态,滑动到底的时候,并且此时没有发出加载数据的时候
                if(scrollState == SCROLL_STATE_IDLE &&
                        lv_phone.getLastVisiblePosition() >= mBlackNumberList.size()-1 && !isLoad ){
                    new Thread(){
                        @Override
                        public void run() {
                            //判断当前数据库当中的记录是否已经读取完毕
                            if(mBlackNumberList.size()<mBlackNumberCount){
                                isLoad = true;
                                mBlackNumberList.addAll(mBlackNumberCrudDB.query(mBlackNumberList.size()+""));
                                mHandler.sendEmptyMessage(0);
                                isLoad = false;
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加拦截的电话号码
                //显示添加拦截的dialog
                showAddDialog();

            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(),R.layout.add_black_number_dialog,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();


        final EditText dialog_et_phone_number = (EditText) view.findViewById(R.id.dialog_et_phone_number);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_phone_mode);

        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = dialog_et_phone_number.getText().toString();
                String phoneMode = "拦截短信";
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rb_sms:
                        phoneMode = "1";
                        break;
                    case R.id.rb_phone:
                        phoneMode = "2";
                        break;
                    case R.id.rb_all:
                        phoneMode = "3";
                        break;

                }
                dialog.dismiss();
                BlackNumInfo blackNumInfo = new BlackNumInfo();
                blackNumInfo.setPhoneNum(phoneNum);
                blackNumInfo.setMode(phoneMode);
                mBlackNumberList.add(0,blackNumInfo);
                mBlackNumberCrudDB.insert(phoneNum,phoneMode);
                mAdapte.notifyDataSetChanged();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public static Intent newIntent(Context context){
        Intent i = new Intent(context,BlackNumActivity.class);
        return i;
    }
}
