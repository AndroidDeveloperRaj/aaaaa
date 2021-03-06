package com.merrichat.net.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.adapter.EffectAndFilterSelectAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.utils.DensityUtils;

/**
 * Created by lirui on 2017/1/20.
 */

public class EffectAndFilterItemView extends LinearLayout {
    private CircleImageView mItemIcon;
    private TextView mItemText;

    private int mItemType;//effect or filter

    public EffectAndFilterItemView(Context context, int itemType) {
        super(context);
        this.mItemType = itemType;
        init(context);
    }

    private void init(Context context) {
//        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        setLayoutParams(params);
        View viewRoot = LayoutInflater.from(context).inflate(R.layout.effect_and_filter_item_view, this, true);
        mItemIcon = (CircleImageView) viewRoot.findViewById(R.id.item_icon);
        mItemText = (TextView) viewRoot.findViewById(R.id.item_text);
        if (mItemType == EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_FILTER) {
            mItemText.setVisibility(VISIBLE);
        }
    }

    public void setUnselectedBackground() {
        if (mItemType == EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT) {
            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_circle_unselected));
        } else {
//            mItemIcon.setBackgroundColor(Color.parseColor("#00000000"));
            mItemIcon.setBorderWidth(0);
            mItemIcon.setBorderColor(MerriApp.mContext.getResources().getColor(R.color.transparent));
        }
    }

    public void setSelectedBackground() {
        if (mItemType == EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT) {
            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_circle_selected));
        } else {
            mItemIcon.setBorderWidth(DensityUtils.dp2px(MerriApp.mContext, 2));
            mItemIcon.setBorderColor(MerriApp.mContext.getResources().getColor(R.color.normal_red));
//            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_circle_selected));
//            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_square_selected));
        }
    }

    public void setItemIcon(int resourceId) {
        mItemIcon.setImageDrawable(getResources().getDrawable(resourceId));
    }

    public void setItemText(String text) {
        mItemText.setText(text);
    }
}
