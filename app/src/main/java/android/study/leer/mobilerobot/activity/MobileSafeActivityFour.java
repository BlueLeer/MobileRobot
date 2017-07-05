package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Leer on 2017/2/18.
 */
public class MobileSafeActivityFour extends BaseMobileSafeActivity{

    private CheckBox checkBox;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_safe_four);

        initUI();
    }

    private void initUI() {
        final Button nextStep = (Button) findViewById(R.id.btn_next);
        Button preStep = (Button) findViewById(R.id.btn_previous);
        checkBox = (CheckBox) findViewById(R.id.ck_box_id);
        textView = (TextView) findViewById(R.id.text_view_id);

        boolean isSecurityOn = SPUtil.getBoolean(getApplicationContext(), ContantValues.ISMOBILESAFESETTED);
        checkBox.setChecked(isSecurityOn);
        if(isSecurityOn){
            textView.setText("安全保护已开启");
        }else{
            textView.setText("安全保护已关闭");
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
                SPUtil.storageBoolean(getApplicationContext(),ContantValues.ISMOBILESAFESETTED,isChecked);
            }
        });


        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });
        preStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preStep();
            }
        });
    }

    protected void preStep() {
        //设置完成,跳转到上一个界面
        Intent i = MobileSafeActivityThree.newIntent(getApplicationContext());
        startActivity(i);
        finish();

        overridePendingTransition(R.anim.anim_pre_in,R.anim.anim_pre_out);
    }

    protected void nextStep(){
        boolean isCheck = SPUtil.getBoolean(getApplicationContext(),ContantValues.ISMOBILESAFESETTED);
        if(isCheck){
            Intent i = MobileSafeActivityOver.newIntent(getApplicationContext());
            startActivity(i);
        }else{
            ToastUtil.show(getApplicationContext(),"请开启手机安全防护总开关");
        }
        finish();

        overridePendingTransition(R.anim.anim_next_in,R.anim.anim_next_out);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context,MobileSafeActivityFour.class);
        return intent;
    }
}
