package android.study.leer.mobilerobot.service;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.activity.ToastLocationActivity;
import android.study.leer.mobilerobot.engine.QueryPhoneAddress;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.study.leer.mobilerobot.utils.ToastUtil;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Created by Leer on 2017/2/25.
 */
public class PhoneAddressService extends Service{

    private View mToastVeiw;
    private TextView mTextView;
    private WindowManager mWM;
    private int mWindowWidth;
    private int mWindowHeight;


    private TelephonyManager mTM;
    private static final String TAG = "PhoneAddressService";
    private PhoneStateListener mPL = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
//                CALL_STATE_IDLE :表示空闲状态
                case TelephonyManager.CALL_STATE_IDLE:
                    ToastUtil.show(PhoneAddressService.this,"电话已经挂断");
                    Log.d(TAG,"电话已经挂断............");
                    if(mWM != null && mToastVeiw != null){
                        //保证电话空闲状态的时候,不显示Toast(也就是来电归属地的显示的Toast)
                        mWM.removeView(mToastVeiw);

                    }
                    break;
//                CALL_STATE_OFFHOOK : 表示摘机状态
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
//                CALL_STATE_RINGING : 表示来电状态
                case TelephonyManager.CALL_STATE_RINGING:
                    ToastUtil.show(PhoneAddressService.this,"来电话啦");
                    Log.d(TAG,"来电话啦............");
                    showToast();
                    queryAddress(incomingNumber);
                    break;
            }
        }
    };
    private String mAddress;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mTextView.setText(mAddress);
        }
    };
    /**
     * 归属地主题列表
     */
    private int[] mStyles;
    private OutCallingReceiver mOutCallReceiver;

    private String queryAddress(final String phoneNum) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = QueryPhoneAddress.getAddress(phoneNum);
                mHandler.sendEmptyMessage(0);
            }
        }).start();

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWM.getDefaultDisplay().getWidth();
        mWindowHeight = mWM.getDefaultDisplay().getHeight();
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(mPL,PhoneStateListener.LISTEN_CALL_STATE);

//        "透明","橙色","蓝色","灰色","绿色"
        mStyles = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green };

        //去电的监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        mOutCallReceiver = new OutCallingReceiver();
        registerReceiver(mOutCallReceiver,filter);


    }

    //创建一个广播接收器的内部类,用来监听去电事件
    class OutCallingReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String phoneNum = getResultData();
            showToast();
            queryAddress(phoneNum);
        }
    }

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    //在源码当中取得的设置参数,因为Toast的显示是不依赖活动存在的,也就是说
    //当activity消亡的时候Toast依然可以打印出来
    //因此在Toast的参数当中肯定是有相关的设置的
    private void showToast() {
        final WindowManager.LayoutParams params = mParams;
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
        int locationX = SPUtil.getInt(PhoneAddressService.this,ContantValues.TOAST_LOCATION_X);
        int locationY = SPUtil.getInt(PhoneAddressService.this,ContantValues.TOAST_LOCATION_Y);

        //吐司展示的位置
        params.x = locationX;
        params.y = locationY;

        mToastVeiw = View.inflate(this, R.layout.toast_view,null);
        mTextView = (TextView) mToastVeiw.findViewById(R.id.tv_toast);
        //从sp当中获取应当显示的主题
        int styleIndex = SPUtil.getInt(this, ContantValues.STYLE_INDEX);
        mTextView.setBackgroundResource(mStyles[styleIndex]);


        mTextView.setOnTouchListener(new View.OnTouchListener() {
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
                        if(params.x > mWindowWidth-mTextView.getWidth()){
                            params.x = mWindowWidth-mTextView.getWidth();
                        }
                        if(params.y > mWindowHeight-mTextView.getHeight() - 22){
                            params.y = mWindowHeight-mTextView.getHeight() - 22;
                        }

                        //告知WindowManager更新布局显示
                        mWM.updateViewLayout(mToastVeiw,params);
                        //当手指抬起来的时候,将初始位置更新
                        startX = event.getRawX();
                        startY = event.getRawY();
                    //手指抬起的事件
                    case MotionEvent.ACTION_UP:
                        //因为mToastView是放置在窗体上的,因此它的getLeft()的值始终是0
                        int locationX = params.x;
                        int locationY = params.y;

                        SPUtil.storageInt(PhoneAddressService.this,ContantValues.TOAST_LOCATION_X,locationX);
                        SPUtil.storageInt(PhoneAddressService.this,ContantValues.TOAST_LOCATION_Y,locationY);
                        break;
                }
                //返回false表示不相应event,true代表响应对应的事件
                return true;
            }
        });

        mTextView.setText("来电话啦....");

        mWM.addView(mToastVeiw,params);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTM != null && mPL != null){
            mTM.listen(mPL,PhoneStateListener.LISTEN_NONE);
        }
        //注销广播接收器
        if(mOutCallReceiver != null){
            unregisterReceiver(mOutCallReceiver);
        }
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,PhoneAddressService.class);
        return i;
    }
}
