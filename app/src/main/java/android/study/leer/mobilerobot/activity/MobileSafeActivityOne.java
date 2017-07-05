package android.study.leer.mobilerobot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Leer on 2017/2/18.
 */

public class MobileSafeActivityOne extends BaseMobileSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_safe_one);

        initUI();
    }


    private void initUI() {
        Button nextBtn = (Button) findViewById(R.id.btn_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

    }

    protected void nextStep() {
        Intent intent = MobileSafeActivityTwo.newIntent(getApplicationContext());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_next_in,R.anim.anim_next_out);
    }

    @Override
    protected void preStep() {
        //因为不用返回上一页,因此提供空实现
    }

    public static Intent newIntent(Context context) {
        return new Intent(context,MobileSafeActivityOne.class);
    }
}
