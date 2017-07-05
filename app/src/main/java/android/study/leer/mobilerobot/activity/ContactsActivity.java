package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Leer on 2017/2/20.
 */
public class ContactsActivity extends AppCompatActivity{

    private static final String TAG = "ContactsActivity";
    private RecyclerView contactsRecyclerView;
    private ArrayList<String> contactsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initData();
        intiUI();
    }

    /**
     * 获取联系人列表
     */
    private void initData() {
        contactsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor contactsCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,null,null,null);
                contactsCursor.moveToFirst();
                do {
                    String contactName = contactsCursor
                            .getString(contactsCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String contactNum = contactsCursor.getString(
                            contactsCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactsList.add(contactName+contactNum);
                    Log.i(TAG,contactName);
                }while (contactsCursor.moveToNext());
                contactsCursor.close();
            }
        }).start();
    }

    private void intiUI() {
        contactsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_contacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setAdapter(new MyAdapter(contactsList));
    }

    /**
     * 重写recyclerview的内部类,viewholder
     */
    private class MyViewholder extends RecyclerView.ViewHolder{
        public MyViewholder(View itemView) {
            super(itemView);
        }
        public void bindView(final String s){
            TextView tv = (TextView) itemView;
            tv.setTextColor(Color.BLACK);
            tv.setText(s);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra(ContantValues.EXTRA_NUMBER,s);
                    setResult(RESULT_OK,data);
                    finish();
                }
            });
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewholder>{
        ArrayList<String> dataList = new ArrayList<>();
        public MyAdapter(ArrayList<String> datas) {
            this.dataList = datas;
        }

        @Override
        public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_1,null);
            return new MyViewholder(view);
        }

        @Override
        public void onBindViewHolder(MyViewholder holder, int position) {
            String contactInf = dataList.get(position);
            holder.bindView(contactInf);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context,ContactsActivity.class);
        return i;
    }
}
