package android.study.leer.mobilerobot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.activity.RocketBackgroundActivity;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


/**
 * Created by Leer on 2017/3/1.
 */
public class MyRocketService extends Service{
    private WindowManager mWM;
    private int mWindowWidth;
    private int mWindowHeight;
    private View mRocketView;
    private WindowManager.LayoutParams params;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int i = (int) msg.obj;
            params.y = i;
            mWM.updateViewLayout(mRocketView,params);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWM.getDefaultDisplay().getWidth();
        mWindowHeight = mWM.getDefaultDisplay().getHeight();
        Log.i("屏幕高度:",mWindowHeight+"");
        Log.i("屏幕宽度:",mWindowWidth+"");
        showRocket();

    }


    private void showRocket() {
        params = mParams;
        //定义toast显示界面的宽高
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;

        //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT+Gravity.TOP;

        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mRocketView = View.inflate(this, R.layout.rocket_view,null);
        final ImageView iv_rocket = (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        Log.i("xxrocket的高度:",iv_rocket.getHeight()+"");
        iv_rocket.setOnTouchListener(new View.OnTouchListener() {
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

                        //params.x代表控件左上角的x的坐标
                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        //容错处理
                        if(params.x < 0){
                            params.x = 0;
                        }
                        if(params.y < 0){
                            params.y = 0;
                        }
                        if(params.x > mWindowWidth-iv_rocket.getWidth()){
                            params.x = mWindowWidth-iv_rocket.getWidth();
                        }
                        if(params.y > mWindowHeight-iv_rocket.getHeight() - 22){
                            params.y = mWindowHeight-iv_rocket.getHeight() - 22;
                        }

                        //告知WindowManager更新布局显示
                        mWM.updateViewLayout(mRocketView, params);
                        //当手指抬起来的时候,将初始位置更新
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                        //手指抬起的事件
                    case MotionEvent.ACTION_UP:
                        //因为mToastView是放置在窗体上的,因此它的getLeft()的值始终是0
                        int locationX = params.x;
                        int locationY = params.y;

                        //移动的过程当中,如果移动到某个特定区域,松开手指,火箭就会发射
                        if(params.x>100 && params.x<400 && params.y >800){
                            launchRocket();
                            //同时开启一个Activity来显示火箭发射的背景图片
                            Intent i = new Intent(MyRocketService.this,RocketBackgroundActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                        break;
                }
                //返回false表示不相应event,true代表响应对应的事件
                return true;
            }
        });
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();
        mWM.addView(mRocketView, params);
    }

    private void launchRocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i<15;i++){
                    Message message = new Message();
                    message.obj = 1000-i*100;
                    mHandler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWM != null && mRocketView != null){
            mWM.removeView(mRocketView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,MyRocketService.class);
        return i;
    }
}
