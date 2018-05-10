package com.merrichat.net.adapter;


import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupRedDetialModel;
import com.merrichat.net.view.CircleImageView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by amssy on 2018/2/26.
 */

public class RedBaoAdapter extends BaseQuickAdapter<GroupRedDetialModel.DataBean.RedBean, BaseViewHolder> {
    public RedBaoAdapter(int layoutResId, @Nullable List<GroupRedDetialModel.DataBean.RedBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupRedDetialModel.DataBean.RedBean item) {
        helper.setText(R.id.tv_name,item.getCollectName());
        helper.setText(R.id.tv_time,new SimpleDateFormat("MM-dd HH:mm").format(new Date(Long.parseLong(String.valueOf(item.getCollectTime())))));
        helper.setText(R.id.tv_money,new DecimalFormat("#0.00").format(item.getCollectMoney()) + "讯美币");
        Glide.with(mContext).load(item.getCollectHeadImgUrl()).dontAnimate().placeholder(R.mipmap.ic_preloading).error(R.mipmap.ic_preloading).into((CircleImageView)helper.getView(R.id.iv_header));
    }
}
