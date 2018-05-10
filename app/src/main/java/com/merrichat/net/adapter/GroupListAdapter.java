package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupListModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2018/1/19.
 */

public class GroupListAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public GroupListAdapter(int viewId, ArrayList<T> groupList) {
        super(viewId, groupList);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {

        helper.setText(R.id.tv_group_name, ((GroupListModel.DataBean.ListBean) item).getCommunityName());
        SimpleDraweeView headerView = helper.getView(R.id.iv_header);
        headerView.setImageURI(((GroupListModel.DataBean.ListBean) item).getCommunityImgUrl());
    }
}
