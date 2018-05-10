package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.SellOrderModel;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/23.
 */

class PendingRegimentItemAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public PendingRegimentItemAdapter(int viewId, List<T> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        String memberName = ((SellOrderModel.DataBean.SerialMemberBean) item).getMemberName();
        helper.setText(R.id.tv_buy_name, "买家昵称：" + memberName);

    }
}
