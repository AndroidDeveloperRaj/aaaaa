package com.merrichat.net.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.merrichat.net.R;


/**
 * Created by amssy on 17/11/20.
 */

public class PopShare extends PopupWindow implements View.OnClickListener {

    public static View conentView;
    private OnPopuClick onPopuClick;
    private SimpleDraweeView iv_bulr;
    private LinearLayout layout_collect, lay_private_lock, lay_jubao, lay_dele, lin_pengyouquan, lin_wechat, lin_weibo, lin_qq;
    private Context mContext;
    private View view;
    private Button btnCancel;

    public PopShare(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.layout_share, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
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
        lay_private_lock = (LinearLayout) conentView.findViewById(R.id.lay_private_lock);
        lay_jubao = (LinearLayout) conentView.findViewById(R.id.lay_jubao);
        lay_dele = (LinearLayout) conentView.findViewById(R.id.lay_dele);
        layout_collect = (LinearLayout) conentView.findViewById(R.id.layout_collect);
        lin_pengyouquan = (LinearLayout) conentView.findViewById(R.id.lin_pengyouquan);
        lin_wechat = (LinearLayout) conentView.findViewById(R.id.lin_wechat);
        lin_weibo = (LinearLayout) conentView.findViewById(R.id.lin_weibo);
        lin_qq = (LinearLayout) conentView.findViewById(R.id.lin_qq);
        btnCancel = (Button) conentView.findViewById(R.id.btn_cancel);
        view = conentView.findViewById(R.id.view_top_touch);

        layout_collect.setOnClickListener(this);
        lay_private_lock.setOnClickListener(this);
        lay_jubao.setOnClickListener(this);
        lay_dele.setOnClickListener(this);
        lin_pengyouquan.setOnClickListener(this);
        lin_wechat.setOnClickListener(this);
        lin_weibo.setOnClickListener(this);
        lin_qq.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
//        view.setOnClickListener(this);
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

    public void deleteBtn(boolean isVisibility) {
        if (isVisibility) {
            lay_dele.setVisibility(View.VISIBLE);
            lay_private_lock.setVisibility(View.VISIBLE);
            layout_collect.setVisibility(View.GONE);
            lay_jubao.setVisibility(View.GONE);
        } else {
            lay_dele.setVisibility(View.GONE);
            lay_private_lock.setVisibility(View.GONE);
            layout_collect.setVisibility(View.VISIBLE);
            lay_jubao.setVisibility(View.VISIBLE);
        }
    }

}
