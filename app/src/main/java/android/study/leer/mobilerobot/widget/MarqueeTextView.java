package android.study.leer.mobilerobot.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Leer on 2017/1/6.
 */

public class MarqueeTextView extends TextView {


    /**通过java代码创建空间的构造方法
     * @param context 上下文环境
     */
    public MarqueeTextView(Context context) {
        super(context);
    }

    /**通过布局文件来创建控件,在xml布局文件中创建,系统自动调用
     * @param context 上下文环境
     * @param attrs 属性集
     */
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**xml文件中使用,自动调用
     * @param context 上下文环境
     * @param attrs 属性集合
     * @param defStyleAttr 样式文件
     */
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        super.setFocusableInTouchMode(true);
    }

}
