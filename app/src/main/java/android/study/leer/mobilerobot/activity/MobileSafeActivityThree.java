package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Leer on 2017/2/18.
 */

public class MobileSafeActivityThree extends BaseMobileSafeActivity {

    private static final int REQUEST_CONTACTS = 1;
    private Button nextBtn;
    private Button preBtn;
    private EditText etPhoneNum;
    private Button btPhoneNum;

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,MobileSafeActivityThree.class);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_safe_three);

        initUI();
    }

    private void initUI() {
        nextBtn = (Button) findViewById(R.id.btn_next);
        preBtn = (Button) findViewById(R.id.btn_previous);
        etPhoneNum = (EditText) findViewById(R.id.et_select_phone_number);
        btPhoneNum = (Button) findViewById(R.id.bt_select_phone_number);

        //从sp中获取已经存贮的联系人，并且更新EditText
        String phoneOfSp = SPUtil.getString(getApplicationContext(),ContantValues.BOUND_PHONE_NUM);
        String s = phoneOfSp.replaceAll("[a-zA-Z]","");
        etPhoneNum.setText(s);

        //点击“选择联系人按钮时，就进入联系人选择界面”
        btPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ContactsActivity.newIntent(getApplicationContext());
                startActivityForResult(i,REQUEST_CONTACTS);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bindPhoneNum = etPhoneNum.getText().toString();
                if(!TextUtils.isEmpty(bindPhoneNum)){
                    SPUtil.storageString(getApplicationContext(), ContantValues.BOUND_PHONE_NUM,bindPhoneNum);
                    nextStep();
                }else{
                    ToastUtil.show(getApplicationContext(),"请输入电话号码");
                }
                //进入到设置的第四个页面
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回退到上一个界面
                preStep();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data != null) {
                String numAndName = data.getStringExtra(ContantValues.EXTRA_NUMBER);
                numAndName = numAndName.replace("-", "");
                numAndName = numAndName.replace(" ", "");
                SPUtil.storageString(getApplicationContext(), ContantValues.BOUND_PHONE_NUM, numAndName);
                String s = numAndName.replaceAll("[a-zA-Z]","");
                etPhoneNum.setText(s);
            }
        }
    }

    public void nextStep(){
        Intent i = MobileSafeActivityFour.newIntent(getApplicationContext());
        startActivity(i);
        finish();

        overridePendingTransition(R.anim.anim_next_in,R.anim.anim_next_out);
    }
    public void preStep(){
        //因为EditText中的文字可能在编辑后再会退到上一页,因此,
        //此时也要将EditText中的文字进行读取和保存到SP当中去
        String bindPhoneNum = etPhoneNum.getText().toString();
        SPUtil.storageString(getApplicationContext(), ContantValues.BOUND_PHONE_NUM,bindPhoneNum);
        Intent i = MobileSafeActivityTwo.newIntent(getApplicationContext());
        startActivity(i);
        finish();

        overridePendingTransition(R.anim.anim_pre_in,R.anim.anim_pre_out);
    }
}
