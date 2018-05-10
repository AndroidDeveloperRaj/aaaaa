package com.merrichat.net.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.ShareToMakeMoneyModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/7.
 */

public class ShareToMakeMoneyAdapter extends BaseQuickAdapter<ShareToMakeMoneyModel.DataBeanX.DataBean, BaseViewHolder> {
    public ShareToMakeMoneyAdapter(int item_mymeiyou, ArrayList<ShareToMakeMoneyModel.DataBeanX.DataBean> makeMoneyList) {
        super(item_mymeiyou, makeMoneyList);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareToMakeMoneyModel.DataBeanX.DataBean item) {

        SimpleDraweeView headerView = helper.getView(R.id.cv_header);
        headerView.setImageURI(item.getImgUrl());
        helper.setText(R.id.tv_name, item.getNickName())
                .addOnClickListener(R.id.cv_header)
                .setText(R.id.tv_inviter_name, "邀请人：" + item.getFromMemberName());

        if (item.getLevel() == 1) {
            helper.setText(R.id.tv_invit_way, "一代");
            TextView view = helper.getView(R.id.tv_invit_way);
            view.setBackground(mContext.getResources().getDrawable(R.drawable.shape_meiyou_jianjie_text));
        } else {
            helper.setText(R.id.tv_invit_way, "二代");
            TextView view = helper.getView(R.id.tv_invit_way);
            view.setBackground(mContext.getResources().getDrawable(R.drawable.shape_tomakemoney_erdu_text));
        }
       /* if (item.getStatus() == 0) {
            helper.setGone(R.id.sb_add_friend, true).addOnClickListener(R.id.sb_add_friend);
            helper.setGone(R.id.tv_status, false);
        } else {
            helper.setGone(R.id.sb_add_friend, false);
            helper.setGone(R.id.tv_status, true);
            if (item.getStatus() == 1) {
                helper.setText(R.id.tv_status, "等待同意");
            } else {
                helper.setText(R.id.tv_status, "已添加");

            }
        }
*/
    }
}
