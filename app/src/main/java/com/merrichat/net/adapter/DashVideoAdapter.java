package com.merrichat.net.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.DashModel;
import com.merrichat.net.utils.TimeUtil;

import java.util.List;

/**
 * 视频打赏列表
 * Created by amssy on 18/4/12.
 */

public class DashVideoAdapter extends BaseQuickAdapter<DashModel.DataBean,BaseViewHolder> {

    public DashVideoAdapter(int layoutResId, @Nullable List<DashModel.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DashModel.DataBean item) {
        //头像
        ((SimpleDraweeView)helper.getView(R.id.cv_photo)).setImageURI(item.getImgUrl());
        //名称
        ((TextView)helper.getView(R.id.tv_name)).setText(item.getName());
        //打赏金额
//        ((TextView)helper.getView(R.id.tv_contant)).setText("打赏了"+item.getAmount()+"讯美币");

        SpannableString spannableString1 = new SpannableString("打赏了"+item.getAmount()+"讯美币");
        SpannableString spannableString = new SpannableString("打赏了"+item.getAmount()+"讯美币" + "   " + (TextUtils.isEmpty(item.getCreateTime()) ? "" : TimeUtil.getStrTime1(String.valueOf(item.getCreateTime()))));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
        spannableString.setSpan(colorSpan, spannableString1.length() + 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ((TextView)helper.getView(R.id.tv_contant)).setText(spannableString);
    }

}
