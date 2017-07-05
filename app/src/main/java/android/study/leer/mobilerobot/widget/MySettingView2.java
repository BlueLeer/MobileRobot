package android.study.leer.mobilerobot.widget;

import android.content.Context;
import android.media.Image;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;
import android.study.leer.mobilerobot.utils.SPUtil;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Leer on 2017/2/25.
 */

public class MySettingView2 extends RelativeLayout {
    private static final String TAG = "MySettingView2";
    private TextView settingTitle;
    private TextView settingDesp;
    public ImageView mItemSelect;

    public MySettingView2(Context context) {
        this(context, null);
    }

    public MySettingView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySettingView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.item_setting2, this);

        settingTitle = (TextView) findViewById(R.id.title_id);
        settingDesp = (TextView) findViewById(R.id.desp_id);
        mItemSelect = (ImageView)findViewById(R.id.iv_select_id);
    }

    /**
     * 初始化条目的UI
     *
     * @param desTitle   描述条目的题目
     * @param desContent 描述条目具体的设置内容
     */
    public void init(String desTitle, String desContent) {
        settingTitle.setText(desTitle);
        settingDesp.setText(desContent);
    }

}