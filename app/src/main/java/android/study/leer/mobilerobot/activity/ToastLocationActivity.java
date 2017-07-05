package android.study.leer.mobilerobot.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


/**
 * Created by Leer on 2017/2/27.
 */

public class ToastLocationActivity extends Activity {

    private Button bt_top;
    private Button bt_bottom;
    private WindowManager mWM;
    private int mWindowWidth;
    private int mWindowHeight;
    long[] mHits = new long[2];
    private ImageView iv_drag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toast_location_activity_layout);

        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWM.getDefaultDisplay().getWidth();
        mWindowHeight = mWM.getDefaultDisplay().getHeight();
        initUI();
    }

    private void initUI() {
        bt_top = (Button) findViewById(R.id.bt_top);
        bt_bottom = (Button) findViewById(R.id.bt_bottom);
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        int locationX = SPUtil.getInt(this, ContantValues.TOAST_LOCATION_X);
        int locationY = SPUtil.getInt(this, ContantValues.TOAST_LOCATION_Y);

        LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = locationX;
        params.topMargin = locationY;
        iv_drag.setLayoutParams(params);

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理双击事件
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    //满足双击事件后,调用代码
                    int left = mWindowWidth / 2 - iv_drag.getWidth() / 2;
                    int top = mWindowHeight / 2 - iv_drag.getHeight() / 2;
                    int right = mWindowWidth / 2 + iv_drag.getWidth() / 2;
                    int bottom = mWindowHeight / 2 + iv_drag.getHeight() / 2;


                    //控件按以上规则显示
                    iv_drag.layout(left, top, right, bottom);
                    SPUtil.storageInt(ToastLocationActivity.this,ContantValues.TOAST_LOCATION_X, iv_drag.getLeft());
                    SPUtil.storageInt(ToastLocationActivity.this,ContantValues.TOAST_LOCATION_Y, iv_drag.getTop());
                }
            }
        });

        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    //按下的事件
                    case MotionEvent.ACTION_DOWN:
                        //记录下按下的位置
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    //手指移动的事件
                    case MotionEvent.ACTION_MOVE:
                        int disX = (int)(event.getRawX() - startX);
                        int disY = (int) (event.getRawY() - startY);

                        int left = iv_drag.getLeft() + disX;
                        int top = iv_drag.getTop()  + disY;
                        int right = iv_drag.getRight() + disX;
                        int bottom = iv_drag.getBottom() + disY;

                        //容错处理
                        if(right>mWindowWidth || left<0
                                || top<0 || bottom>mWindowHeight-100){
                            return true;
                        }

                        if(top > mWindowHeight/2){
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else{
                            bt_bottom.setVisibility(View.VISIBLE);
                            bt_top.setVisibility(View.INVISIBLE);
                        }

                        iv_drag.layout(left, top, right, bottom);

                        //当手指抬起来的时候,将初始位置更新
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    //手指抬起的事件
                    case MotionEvent.ACTION_UP:
                        int locationX = (int) iv_drag.getLeft();
                        int locationY = (int) iv_drag.getTop();

                        SPUtil.storageInt(ToastLocationActivity.this,ContantValues.TOAST_LOCATION_X,locationX);
                        SPUtil.storageInt(ToastLocationActivity.this,ContantValues.TOAST_LOCATION_Y,locationY);
                        break;
                }
                //返回false表示不相应event,true代表响应对应的事件

                //既要响应点击事件,又要响应手势事件,应该将这里改为false,不然双击事件是不能响应的
                //返回false代表onTouch()当中的代码虽然已经执行完毕了,但是该事件可以继续往下传递,他就会进入到相应的
                //onClick()当中去,
                //若除了注册setOnTouchListener()之外没有注册其他的事件,那么ACTION_MOVE事件就不会被处理,也就是它当中
                //的代码不会被成功执行
                return false;
            }
        });
    }


    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ToastLocationActivity.class);
        return i;
    }
}
