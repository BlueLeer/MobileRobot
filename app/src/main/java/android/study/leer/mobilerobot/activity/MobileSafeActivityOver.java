package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Leer on 2017/2/18.
 */

public class MobileSafeActivityOver extends AppCompatActivity {

    private ImageView mIV;
    private TextView mTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_safe_over);
        initUI();
    }

    private void initUI() {
        mIV = (ImageView) findViewById(R.id.iv_lock);
        mTV = (TextView) findViewById(R.id.tv_set_again);
        TextView mNum = (TextView) findViewById(R.id.tv_phone_num);

        String phoneNum = SPUtil.getString(getApplicationContext(),ContantValues.BOUND_PHONE_NUM);
        String s = phoneNum.replaceAll("[a-zA-Z]","");
        mNum.setText(s);

        boolean isSetup = SPUtil.getBoolean(getApplicationContext(), ContantValues.ISMOBILESAFESETTED);
        if(isSetup){
            mIV.setImageResource(R.drawable.lock);
        }else{
            mIV.setImageResource(R.drawable.unlock);
        }

        mTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSetupOne();
                finish();
            }
        });
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,MobileSafeActivityOver.class);
        return i;
    }

    private void enterSetupOne(){
        Intent i = MobileSafeActivityOne.newIntent(getApplicationContext());
        startActivity(i);
    }
}
