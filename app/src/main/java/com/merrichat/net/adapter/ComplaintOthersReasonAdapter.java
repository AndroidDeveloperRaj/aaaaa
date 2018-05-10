package com.merrichat.net.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.ComplaintOthersReasonModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/12/5.
 */

public class ComplaintOthersReasonAdapter extends BaseQuickAdapter<ComplaintOthersReasonModel.DataBean, BaseViewHolder> {
    public ComplaintOthersReasonAdapter(int viewId, ArrayList<ComplaintOthersReasonModel.DataBean> reasonList) {
        super(viewId, reasonList);
    }

    @Override
    protected void convert(BaseViewHolder helper, ComplaintOthersReasonModel.DataBean item) {
        helper.setText(R.id.tv_reason_text, item.getTypeName());
        TextView tvReasonText = helper.getView(R.id.tv_reason_text);
        if (item.isChecked()) {
            tvReasonText.setSelected(true);
            helper.setGone(R.id.iv_checked, true);
        } else {
            tvReasonText.setSelected(false);
            helper.setGone(R.id.iv_checked, false);
        }
    }
}
