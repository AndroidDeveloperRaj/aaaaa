package com.merrichat.net.adapter;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.CircleDetailModel;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/13.
 */

public class TuWenXiHuanPerAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public TuWenXiHuanPerAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (item instanceof CircleDetailModel.DataBean.MemberIdBean) {
            SimpleDraweeView headerView = helper.getView(R.id.cv_header);
            headerView.setImageURI(((CircleDetailModel.DataBean.MemberIdBean) item).getPersonUrl());
        }

    }
}
