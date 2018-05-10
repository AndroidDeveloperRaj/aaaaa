package com.merrichat.net.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.model.Progress;
import com.merrichat.net.R;


/**
 * Created by amssy on 17/11/20.
 */

public class PopVideo extends PopupWindow implements View.OnClickListener {

    public static View conentView;
    private final ImageView iv_close;
    private OnPopuClick onPopuClick;
    private Context mContext;
    private final ProgressBar progress;
    private final TextView textView;

    public PopVideo(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.layout_pop_video, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
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
        this.mContext = context;

        progress = (ProgressBar) conentView.findViewById(R.id.progress_video);
        iv_close = (ImageView) conentView.findViewById(R.id.iv_close);
        textView = (TextView) conentView.findViewById(R.id.textView_video);

    }

    public void setOnPopuClick(OnPopuClick click) {
        this.onPopuClick = click;
    }

    @Override
    public void onClick(View v) {
        onPopuClick.onClick(v);
    }

    public interface OnPopuClick {
        void onClick(View view);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
