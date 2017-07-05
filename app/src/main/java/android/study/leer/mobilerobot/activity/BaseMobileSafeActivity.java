package android.study.leer.mobilerobot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 * Created by Leer on 2017/2/20.
 */

public abstract class BaseMobileSafeActivity extends AppCompatActivity {

    public GestureDetector mGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getX() - e2.getX() > 200){
                    nextStep();
                }
                if(e1.getX() - e2.getX() < -200){
                    preStep();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    /**
     * @param event 监听事件
     * @return  返回tounch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    protected abstract void nextStep();
    protected abstract void preStep();
}

