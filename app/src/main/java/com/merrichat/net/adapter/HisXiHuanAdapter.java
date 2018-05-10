package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.HisHomeModel;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/16.
 */

public class HisXiHuanAdapter extends BaseQuickAdapter<HisHomeModel.DataBean.FavorListBean, BaseViewHolder> {
    public HisXiHuanAdapter(int viewId, List<HisHomeModel.DataBean.FavorListBean> xiHuanList) {
        super(viewId, xiHuanList);
    }

    @Override
    protected void convert(BaseViewHolder helper, HisHomeModel.DataBean.FavorListBean item) {
        SimpleDraweeView view = helper.getView(R.id.cv_header);
        view.setImageURI(item.getImgUrl());
    }
}
