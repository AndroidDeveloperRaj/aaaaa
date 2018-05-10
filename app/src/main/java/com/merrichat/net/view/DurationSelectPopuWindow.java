package com.merrichat.net.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextClock;
import android.widget.TextView;

import com.merrichat.net.R;

/**
 * Created by amssy on 17/11/14.
 */

public class DurationSelectPopuWindow extends PopupWindow {

    private View conentView;
    private View clickView;
    private OnPopuClick onPopuClick;
    private TriangleView mTriangleView;
    private TextView tv1, tv2, tv3;
    private int popupWidth;
    private int popupHeight;
    public static int[] duration = {15, 60, 300};
    private static TextView[] textViews;
    private Context mContext;

    public DurationSelectPopuWindow(final Activity context, int second, View clickView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.layout_popu_duration, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popwindow_anim_camera_setting);
        //获取自身的长宽高
        conentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = conentView.getMeasuredHeight();
        popupWidth = conentView.getMeasuredWidth();


        this.clickView = clickView;
        this.mContext = context;
        mTriangleView = (TriangleView) conentView.findViewById(R.id.triangle_view);
        tv1 = (TextView) conentView.findViewById(R.id.tv_1);
        tv2 = (TextView) conentView.findViewById(R.id.tv_2);
        tv3 = (TextView) conentView.findViewById(R.id.tv_3);
        mTriangleView.setColor(context.getResources().getColor(R.color.c_222629_70));
        mTriangleView.setGravity(Gravity.BOTTOM);
        textViews = new TextView[]{tv1, tv2, tv3};

        tv1.setText(getCodeSecond(duration[0]));
        tv2.setText(getCodeSecond(duration[1]));
        tv3.setText(getCodeSecond(duration[2]));

        for (int i = 0; i < duration.length; i++) {
            if (second == duration[i]) {
                textViews[i].setTextColor(context.getResources().getColor(R.color.normal_red));
            } else {
                textViews[i].setTextColor(context.getResources().getColor(R.color.white));
            }
        }

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeTextColor(0);
                if (onPopuClick != null) {
                    onPopuClick.onClick(duration[0]);
                }
                dismiss();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeTextColor(1);
                if (onPopuClick != null) {
                    onPopuClick.onClick(duration[1]);
                }
                dismiss();
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeTextColor(2);
                if (onPopuClick != null) {
                    onPopuClick.onClick(duration[2]);
                }
                dismiss();
            }
        });
    }


    private void changeTextColor(int index) {
        for (int i = 0; i < duration.length; i++) {
            if (index == i) {
                textViews[i].setTextColor(mContext.getResources().getColor(R.color.normal_red));
            } else {
                textViews[i].setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }

    public void setOnPopuClick(OnPopuClick click) {
        this.onPopuClick = click;
    }

    public interface OnPopuClick {
        void onClick(int second);
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     *
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (clickView != null) {
            clickView.setEnabled(true);
        }
    }

    public static String getCodeSecond(int second) {
        String str = "";
        if (second < 60) {
            str = second + "秒";
        } else {
            str = second / 60 + "分钟";
        }
        return str;
    }
}
