package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.BonusReportModel;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/8.
 */

public class BonusReportAdapter extends BaseQuickAdapter<BonusReportModel.DataBean.BonusReportsBean, BaseViewHolder> {

    public BonusReportAdapter(int item_mymeiyou, ArrayList<BonusReportModel.DataBean.BonusReportsBean> bonusReportList) {
        super(item_mymeiyou, bonusReportList);
    }

    @Override
    protected void convert(BaseViewHolder helper, BonusReportModel.DataBean.BonusReportsBean item) {
        helper.setText(R.id.tv_month, item.getCurrentYear() + "月" + item.getMonth() + "奖励记录")
                .setText(R.id.tv_money_num, StringUtil.getPrice(item.getAmount() + "") + "讯美币");
    }
}
