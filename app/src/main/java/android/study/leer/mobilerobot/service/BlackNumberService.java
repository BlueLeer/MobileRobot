package android.study.leer.mobilerobot.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.study.leer.mobilerobot.db.BlackNumberCrudDB;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;


/**此服务用来监听发来的短信和打来的电话,如果是黑名单中的拦截号码,就会
 * 相应的拦截电话号码或者短信
 * Created by Leer on 2017/3/2.
 */
public class BlackNumberService extends Service{
    private BlackNumberCrudDB mBlackNumberCrudDB;
    private SMSReceiver mSmsReceiver;
    private TelephonyManager mTM;

    private PhoneStateListener mPL = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
//                CALL_STATE_OFFHOOK : 表示摘机状态
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;

//                CALL_STATE_RINGING : 表示来电状态
                case TelephonyManager.CALL_STATE_RINGING:
                    //讲电话号码放入数据库道中当中查询
                    String mode = mBlackNumberCrudDB.getMode(incomingNumber+"");
                    if(mode.equals("2") || mode.equals("3")){
                        //挂断电话
                        endCall(incomingNumber);
                    }
                    break;
            }

        }
    };
    private MyContentObserver mMyContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mBlackNumberCrudDB = BlackNumberCrudDB.getDBCrud(this);
        //要监听发来的短信必须创建一个能够接收次广播的广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mSmsReceiver = new SMSReceiver();
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mSmsReceiver,filter);

        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(mPL,PhoneStateListener.LISTEN_CALL_STATE);


    }



    //接收发来短信的广播
    private class SMSReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0;i<pdus.length;i++){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                //获取发送该短信的人的电话号码
                String phoneNum = smsMessage.getOriginatingAddress();
                String mode = "0";
                //从数据库当中查询此号码的拦截模式(mode)
                if(!TextUtils.isEmpty(phoneNum)){
                    mode = mBlackNumberCrudDB.getMode(phoneNum);
                }
                if(mode.equals("1") || mode.equals("3")){
                    //因为短信的广播是一条有序广播,abortBroadcast()可以阻断有序广播的向下传播
                    //拦截短信(android 4.4版本失效,需要在短信数据库进行删除)
                    abortBroadcast();
                }

            }
        }
    }

    //创建自己的内容观察者
    private class MyContentObserver extends ContentObserver{
        private final String phoneNum;

        public MyContentObserver(Handler handler, String phoneNum) {
            super(handler);
            this.phoneNum = phoneNum;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            getContentResolver().delete(uri,"number = ?",new String[]{phoneNum});
            super.onChange(selfChange, uri);
        }
    }


    private void endCall(String phoneNum) {
        try {
//            1.获取类的字节码文件
            Class<?> clazz = Class.forName("android.os.ServiceManager");
//            2.从字节码文件当中获取方法
            Method method = clazz.getMethod("getService",String.class);
//            3.执行方法获得一个IBinder对象
            IBinder iBinder = (IBinder) method.invoke(null,Context.TELEPHONY_SERVICE);
//            然后将该对象作为ServiceManager.getService(Context.TELEPHONY_SERVICE)的执行结果使用
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();

            //挂断电话以后应该清除通话记录当中的数据,因为挂断电话时,通话记录没有及时的插入到数据库当中,因此执行这个下面的操作数据并没有及时的删除
//            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new String[]{phoneNum});

            mMyContentObserver = new MyContentObserver(new Handler(),phoneNum);

            //注册内容观察者
            getContentResolver().registerContentObserver(
                    Uri.parse("content://call_log/calls"),
                    true,mMyContentObserver);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册短信广播接收
        if(mSmsReceiver != null){
            unregisterReceiver(mSmsReceiver);
        }
        //取消监听来电状态
        if(mTM != null && mPL != null){
            mTM.listen(mPL,PhoneStateListener.LISTEN_NONE);
        }
        //取消内容观察者的观察
        if(mMyContentObserver != null){
            getContentResolver().unregisterContentObserver(mMyContentObserver);
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, BlackNumberService.class);
        return i;
    }
}
