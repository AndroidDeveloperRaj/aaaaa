package com.merrichat.net.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.model.AboutLogModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.FrescoUtils;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.StringUtil;

import java.util.List;

/**
 * 相关日志适配器
 * Created by AMSSY1 on 2017/12/12.
 */
public class AboutLogAdapter extends BaseQuickAdapter<AboutLogModel.DataBean.BeautyLogListBean, BaseViewHolder> {
    public AboutLogAdapter(int layoutResId, @Nullable List<AboutLogModel.DataBean.BeautyLogListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AboutLogModel.DataBean.BeautyLogListBean item) {
        helper.getView(R.id.check_collect).setBackground(mContext.getResources().getDrawable(R.mipmap.pengyouquan_dianzan_waibu_2x));

        if (TextUtils.isEmpty(item.beautylog.content)) {
            return;
        }
        SimpleDraweeView simpleCover = helper.getView(R.id.simple_cover);
        PhotoVideoModel photoVideoModel = JSON.parseObject(item.beautylog.cover, PhotoVideoModel.class);

        //simpleCover.setImageURI(photoVideoModel.url);
        FrescoUtils.setFrescoImageUri(Uri.parse(photoVideoModel.url), simpleCover, StringUtil.getWidths(mContext) / 2 - 30, (int) (photoVideoModel.height / ((float) photoVideoModel.width / (StringUtil.getWidths(mContext) / 2 - 30))));

        SimpleDraweeView simpleHeader = helper.getView(R.id.simple_header);
        simpleHeader.setImageURI(item.beautylog.memberImage);

        helper.setText(R.id.tv_name, item.beautylog.memberName);
        helper.setText(R.id.tv_title, item.beautylog.title);
        helper.setText(R.id.tv_comment, "" + item.beautylog.commentCounts);

        if (item.beautylog.likeCounts > 9999){
            double likeCounts1 = item.beautylog.likeCounts;
            helper.setText(R.id.tv_collect, StringUtil.rounded(likeCounts1 / 1000, 1) + "W");
        }else {
            helper.setText(R.id.tv_collect, "" + item.beautylog.likeCounts);
        }
        if (!TextUtils.isEmpty(item.income)) {
            //赚钱
            if (Double.valueOf(item.income) >= 10000) {
                double income = Double.valueOf(item.income);
                helper.setText(R.id.tv_comment, StringUtil.rounded(income / 10000, 1) + "w");
            } else {
                helper.setText(R.id.tv_comment, "" + item.income);
            }
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleCover.getLayoutParams();
        params.height = (int) (photoVideoModel.height / ((float) photoVideoModel.width / (StringUtil.getWidths(mContext) / 2 - 30)));
        simpleCover.setLayoutParams(params);
    }
}
