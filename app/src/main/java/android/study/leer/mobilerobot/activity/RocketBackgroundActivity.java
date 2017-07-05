package android.study.leer.mobilerobot.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by Leer on 2017/3/1.
 */
public class RocketBackgroundActivity extends Activity{
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rocket_background);

        ImageView iv_top = (ImageView) findViewById(R.id.iv_top);
        ImageView iv_bottom = (ImageView)findViewById(R.id.iv_bottom);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);
        iv_top.setAnimation(alphaAnimation);
        iv_bottom.setAnimation(alphaAnimation);

        Message message = new Message();
        mHandler.sendEmptyMessageDelayed(0,1000);
    }
}
