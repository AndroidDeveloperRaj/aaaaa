package com.merrichat.net.adapter;

import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.MyContactModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

/**
 * Created by amssy on 17/11/14.
 * 联系人---我的好友
 */

public class MyFriendsAdapter extends BaseQuickAdapter<MyContactModel.DataBean.AttentionGoodFriendsRelationsBean, BaseViewHolder> {
    public MyFriendsAdapter(int layoutResId, @Nullable List<MyContactModel.DataBean.AttentionGoodFriendsRelationsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyContactModel.DataBean.AttentionGoodFriendsRelationsBean item) {
        Glide.with(mContext).load(item.getGoodFriendsUrl()).dontAnimate().into((CircleImageView) helper.getView(R.id.cv_friends_photo));
        helper.setText(R.id.tv_friends_name, item.getGoodFriendsName());
    }
}
