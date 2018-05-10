package com.merrichat.net.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.BonusRecordModel;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/8.
 */

public class BonusRecordAdapter extends BaseQuickAdapter<BonusRecordModel.DataBean.BonusRecordsBean, BaseViewHolder> {
    public BonusRecordAdapter(int layout, ArrayList<BonusRecordModel.DataBean.BonusRecordsBean> bonusRecordList) {
        super(layout, bonusRecordList);
    }

    @Override
    protected void convert(BaseViewHolder helper, BonusRecordModel.DataBean.BonusRecordsBean item) {
        helper.setText(R.id.tv_item_date, RxTimeTool.getDate(item.getCreateTime() + "", "MM月dd日 HH:mm"))
                .setText(R.id.tv_item_allnum, "+" + StringUtil.getPrice(item.getAmount()) + "讯美币")
                .setText(R.id.tv_item_from, item.getSourceStr())
                .setText(R.id.tv_item_meiyou_level, item.getGradeStr());
        if (item.getGrade() == 1 || item.getGrade() == 2) {//好友等级 0 好友送的，1直接奖励，2间接奖励,3平台
            helper.setGone(R.id.cv_header, false).setText(R.id.tv_item_name, "***").setGone(R.id.ll_item_meiyou_level, true);
        } else {
            helper.setGone(R.id.cv_header, true).setText(R.id.tv_item_name, item.getGoodFriendsName()).setGone(R.id.ll_item_meiyou_level, false);
            Glide.with(mContext).load(item.getGoodFriendsUrl()).into((ImageView) helper.getView(R.id.cv_header));
        }
    }
}
