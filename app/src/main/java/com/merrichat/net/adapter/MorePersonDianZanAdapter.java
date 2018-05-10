package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.CircleDetailModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2018/1/12.
 */

public class MorePersonDianZanAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public MorePersonDianZanAdapter(int viewId, ArrayList memberList) {
        super(viewId, memberList);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        helper.setText(R.id.tv_nike_name, ((CircleDetailModel.DataBean.MemberIdBean) item).getNickName());
        SimpleDraweeView headerView = helper.getView(R.id.clv_header);
        headerView.setImageURI(((CircleDetailModel.DataBean.MemberIdBean) item).getPersonUrl());
    }
}
