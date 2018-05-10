package com.merrichat.net.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisFunsAndAttentionsAty;
import com.merrichat.net.activity.my.AttentionsActivity;
import com.merrichat.net.activity.my.FansActivity;
import com.merrichat.net.model.MyFansModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/12/15.
 */

public class FansAndAttentionAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    private int activityId;

    public FansAndAttentionAdapter(int viewId, ArrayList<T> list, int activityId) {
        super(viewId, list);
        this.activityId = activityId;

    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (item instanceof MyFansModel.DataBean) {
            helper.setText(R.id.tv_friends_name, ((MyFansModel.DataBean) item).getToMemberName())
                    .setText(R.id.tv_jianjie, ((MyFansModel.DataBean) item).getIntroduction());
            Glide.with(mContext).load(((MyFansModel.DataBean) item).getHeadUrl()).into((ImageView) helper.getView(R.id.cv_fans_photo));
            if (activityId == FansActivity.activityId || activityId == HisFunsAndAttentionsAty.activityId) {
                if (((MyFansModel.DataBean) item).getAttention() == 0) {
                    helper.setGone(R.id.tv_add_attention, true).setGone(R.id.tv_cancle_attention, false);
                } else {
                    helper.setGone(R.id.tv_add_attention, false).setGone(R.id.tv_cancle_attention, true);
                }
            } else if (activityId == AttentionsActivity.activityId) {
                helper.setGone(R.id.tv_add_attention, false).setGone(R.id.tv_cancle_attention, true);
            }
        }

        helper.addOnClickListener(R.id.tv_add_attention).addOnClickListener(R.id.tv_cancle_attention);
    }
}
