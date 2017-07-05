package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.engine.QueryPhoneAddress;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Leer on 2017/2/24.
 */
public class QueryPhoneAddActivity extends AppCompatActivity{

    private EditText mETPhoneNum;
    private Button mBTQuery;
    private TextView mQueryResult;
    private String mAddress = "未知号码";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mQueryResult.setText(mAddress);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_phone_add_layout);
        initUI();
    }

    private void initUI() {
        mETPhoneNum = (EditText) findViewById(R.id.et_query_phone_address);
        mBTQuery = (Button) findViewById(R.id.bt_query_phone_address);
        mQueryResult = (TextView) findViewById(R.id.tv_query_phone_add_result);

        mBTQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = mETPhoneNum.getText().toString();
                if(!TextUtils.isEmpty(phoneNum)){
                    query(phoneNum);
                }else{
                    //输入框抖动起来,开启了一个抖动的动画
                    Animation shake = AnimationUtils.loadAnimation(QueryPhoneAddActivity.this, R.anim.shake);
                    mETPhoneNum.startAnimation(shake);
                    //当手机号输入框的内容是空的,点击"查询"按钮的时候,就让输入框抖动起来
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }

//                Interpolator:就是一个数学函数
            }
        });

        //开启实时查询
        mETPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = mETPhoneNum.getText().toString();
                query(phoneNum);
            }
        });
    }

    private void query(final String phoneNum) {
        //耗时操作,一次开启线程,查询电话号码
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = QueryPhoneAddress.getAddress(phoneNum);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,QueryPhoneAddActivity.class);
        return i;
    }
}
