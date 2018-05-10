package com.merrichat.net.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/***
 * 自定义GridView，主要实现GridView被ScrollView包裹时显示不正常的问题
 */
public class MyGridView extends GridView {

    public MyGridView(Context context) {

        super(context);

    }

    public MyGridView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    /**
     * 处理嵌套显示不全的问题
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override

    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            return true;  //禁止GridView滑动
        }

        return super.dispatchTouchEvent(ev);
    }
}
