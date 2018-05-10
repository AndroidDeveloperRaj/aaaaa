package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.ChaKanWuLiuModel;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/23.
 */

public class ChaKanWuLiuAdapter extends BaseQuickAdapter<ChaKanWuLiuModel, BaseViewHolder> {
    public ChaKanWuLiuAdapter(int viewId, List<ChaKanWuLiuModel> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChaKanWuLiuModel item) {
        if (helper.getPosition() == 0) {
            helper.setVisible(R.id.tv_top, false);
            helper.setImageResource(R.id.iv_tag, R.drawable.chakanwuliu_one_2x);
            helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color.blue_new_thress));
            helper.setTextColor(R.id.tv_time, mContext.getResources().getColor(R.color.blue_new_thress));
        } else {
            helper.setVisible(R.id.tv_top, true);
            helper.setImageResource(R.id.iv_tag, R.drawable.chakanwuliu_two_2x);
            helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color.base_888888));
            helper.setTextColor(R.id.tv_time, mContext.getResources().getColor(R.color.base_888888));

        }

        helper.setText(R.id.tv_content, item.getLog_info().trim()).setText(R.id.tv_time, item.getLog_time());

    }
}
