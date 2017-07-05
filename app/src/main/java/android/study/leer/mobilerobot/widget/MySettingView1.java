package android.study.leer.mobilerobot.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.study.leer.mobilerobot.R;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Leer on 2017/2/16.
 */

public class MySettingView1 extends RelativeLayout {


    private static final String TAG = "MySettingView1";
    private TextView settingTitle;
    private TextView settingDesp;
    private CheckBox checkBox;
    private String mDesTitle;
    private String mDesOn;
    private String mDesOff;

    public MySettingView1(Context context) {
        this(context,null);
    }

    public MySettingView1(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MySettingView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//将这个view挂在到此view上面
        View.inflate(context, R.layout.item_setting1,this);

        settingTitle = (TextView) findViewById(R.id.title_id);
        settingDesp = (TextView) findViewById(R.id.desp_id);
        checkBox = (CheckBox) findViewById(R.id.ck_box_id);

//        Log.i("xxxxxx","属性个数总数 : "+attrs.getAttributeCount());
//        for(int i = 0;i<attrs.getAttributeCount();i++){
//            Log.i("xxxxxx","attrs name:"+attrs.getAttributeName(i)+"");
//            Log.i("xxxxxx",attrs.getAttributeValue(i)+"");
//            Log.i("xxxxxxxxxxxxx","");
//        }

        getAttrs(attrs);
        init();


    }


    /**从attrs中获取属性的标题和说明信息
     * @param attrs 属性集合
     */
    private void getAttrs(AttributeSet attrs) {
        //获取属性值三种方法
        //1.通过命名空间和属性的name获取
        mDesTitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "destitle");
        mDesOn = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "deson");
        mDesOff = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desoff");
        Log.i(TAG, mDesTitle);

        //2.遍历属性获取,不过此方法获取的属性先要遍历所有的属性,然后找到自己想要的属性
        for (int i = 0;i<attrs.getAttributeCount();i++){
            attrs.getAttributeValue(i);
        }

        //3.通过工具获取,此方法可以返回多种类型的属性值,例如返回图片,音频等等
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MySettingView1);
        for(int i = 0;i<ta.getIndexCount();i++){
            switch (ta.getIndex(i)){
                case R.styleable.MySettingView1_desoff:

                    break;
               //等等

            }
        }

    }

    /**
     * 初始化条目的UI
     */
    public void init(){
        settingTitle.setText(mDesTitle);
        settingDesp.setText(mDesOff);
    }

    /**获取checkbox是否被选中
     * @return CheckBox是否被选中
     */
    public boolean getIsCheck(){
        return checkBox.isChecked();
    }

    /**
     * @param isCheck 修改checkbox的选中状态
     */
    public void setCheck(boolean isCheck){
        checkBox.setChecked(isCheck);
        updateStatus();
    }

    /**
     * 修改状态说明信息
     */
    public void updateStatus(){
        if(getIsCheck()){
            settingDesp.setText(mDesOn);
        }else{
            settingDesp.setText(mDesOff);
        }
    }
}
