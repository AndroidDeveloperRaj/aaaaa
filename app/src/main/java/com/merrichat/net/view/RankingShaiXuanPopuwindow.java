package com.merrichat.net.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.merrichat.net.R;

/**
 * Created by amssy on 17/9/27.
 * 榜单--筛选弹框
 */

public class RankingShaiXuanPopuwindow extends PopupWindow implements View.OnClickListener {

    private View mMenuView;
    private PopShare.OnPopuClick onPopuClick;

    private LinearLayout tv_male;
    private LinearLayout tv_female;
    private LinearLayout tv_all;
    private Context context;

    private int bangDanType;

    public RankingShaiXuanPopuwindow(Context cnt, int bangDanType) {
        super(cnt);
        this.context = cnt;
        this.bangDanType = bangDanType;
        LayoutInflater inflater = (LayoutInflater) cnt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popupwindow_ranking_shai_xuan, null);
        this.setContentView(mMenuView);

        initView();
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明0xb0000000
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        mMenuView.findViewById(R.id.view_top_touch).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }

    private void initView() {
        tv_male = (LinearLayout) mMenuView.findViewById(R.id.lay_sex_all);
        tv_female = (LinearLayout) mMenuView.findViewById(R.id.lay_sex_male);
        tv_all = (LinearLayout) mMenuView.findViewById(R.id.lay_sex_female);
        mMenuView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_all.setOnClickListener(this);

    }

    public void setOnPopuClick(PopShare.OnPopuClick click) {
        this.onPopuClick = click;
    }

    @Override
    public void onClick(View v) {
        onPopuClick.onClick(v);
    }

    public interface OnPopuClick {
        void onClick(View view);
    }

}
