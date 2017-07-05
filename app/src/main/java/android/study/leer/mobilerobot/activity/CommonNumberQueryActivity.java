package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.db.CommonNumberCrudDB;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


import java.util.List;

/**
 * Created by Leer on 2017/3/12.
 */
public class CommonNumberQueryActivity extends Activity{

    private ExpandableListView e_lv_common_number;
    private List<CommonNumberCrudDB.Group> mGroupList;
    private MyAdapter mAdapter;

    private Handler mHandelr = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mAdapter == null){
                mAdapter = new MyAdapter();
                e_lv_common_number.setAdapter(mAdapter);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);

        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mGroupList = new CommonNumberCrudDB().getGroup(CommonNumberQueryActivity.this);
                mHandelr.sendEmptyMessage(0);
            }
        }.start();

    }
    private void initUI() {
        e_lv_common_number = (ExpandableListView) findViewById(R.id.e_lv_common_number);
        e_lv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String number = mGroupList.get(groupPosition).children.get(childPosition).number;
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+number));
                startActivity(i);
                return false;
            }
        });
    }

    class MyAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).children.size();
        }

        @Override
        public CommonNumberCrudDB.Group getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        @Override
        public CommonNumberCrudDB.Child getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).children.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = View.inflate(CommonNumberQueryActivity.this,
                    android.R.layout.simple_list_item_1,null);
            TextView tv = (TextView) view;
            tv.setText("      "+mGroupList.get(groupPosition).name);
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(CommonNumberQueryActivity.this,
                    android.R.layout.simple_list_item_1,null);
            TextView tv = (TextView) view;
            tv.setText("商户名称:"+mGroupList.get(groupPosition).children.get(childPosition).name +
                    "\n"+"商户电话:"+mGroupList.get(groupPosition).children.get(childPosition).number
            );
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }



    public static Intent newIntent(Context context){
        Intent i = new Intent(context,CommonNumberQueryActivity.class);
        return i;
    }
}
