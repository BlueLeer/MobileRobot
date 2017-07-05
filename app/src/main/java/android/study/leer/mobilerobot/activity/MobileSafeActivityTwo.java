package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.SimUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.study.leer.mobilerobot.widget.MySettingView1;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Leer on 2017/2/18.
 */

public class MobileSafeActivityTwo extends BaseMobileSafeActivity {


    private Button nextBtn;
    private Button preBtn;
    private MySettingView1 mySettingView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_safe_two);

        initUI();
    }

    private void initUI() {
        nextBtn = (Button) findViewById(R.id.btn_next);
        preBtn = (Button) findViewById(R.id.btn_previous);
        mySettingView1 = (MySettingView1) findViewById(R.id.update_id);

        //从SP中获取checkbox的选中状态,如果选中了,就初始化界面的时候选中这个Checkbox
        final boolean isCheck = SPUtil.getBoolean(getApplicationContext(), ContantValues.IS_BIND_SIM);
        mySettingView1.setCheck(isCheck);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = SPUtil.getBoolean(getApplicationContext(), ContantValues.IS_BIND_SIM);
                if(isCheck) {
                    //进入到设置的第三个页面
                    nextStep();
                }else{
                    ToastUtil.show(getApplicationContext(),"请绑定SIM卡,再跳转到下一步");
                }
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回退到上一个界面
                preStep();
            }
        });


        mySettingView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCBCheck = mySettingView1.getIsCheck();
                mySettingView1.setCheck(!isCBCheck);
                if(!isCBCheck){
                    SPUtil.storageString(getApplicationContext(),ContantValues.SIM_ID,
                            SimUtil.getSIMId(getApplicationContext()));
                }
                SPUtil.storageBoolean(getApplicationContext(),ContantValues.IS_BIND_SIM,!isCBCheck);
            }
        });
    }



    public static Intent newIntent(Context context){
        Intent i = new Intent(context,MobileSafeActivityTwo.class);
        return i;
    }

    public void nextStep(){
        Intent i = MobileSafeActivityThree.newIntent(getApplicationContext());
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.anim_next_in,R.anim.anim_next_out);
    }
    @Override
    public void preStep(){
        Intent i = MobileSafeActivityOne.newIntent(getApplicationContext());
        startActivity(i);
        finish();

        overridePendingTransition(R.anim.anim_pre_in,R.anim.anim_pre_out);
    }

}
