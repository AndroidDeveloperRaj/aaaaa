package com.merrichat.net.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.SelectorLabelModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/20.
 */

public class SelectorLabelAdapter extends BaseQuickAdapter<SelectorLabelModel.DataBean, BaseViewHolder> {
    public SelectorLabelAdapter(int viewId, ArrayList<SelectorLabelModel.DataBean> list) {
        super(viewId, list);

    }

    @Override
    protected void convert(BaseViewHolder helper, SelectorLabelModel.DataBean item) {
        helper.setText(R.id.tv_label_name, item.getLable());
        if (item.isChecked()) {
//            helper.setVisible(R.id.iv_checked, true);
            helper.getView(R.id.tv_label_name).setBackgroundResource(R.drawable.shape_button_tab_true);
            ((TextView)helper.getView(R.id.tv_label_name)).setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
//            helper.setVisible(R.id.iv_checked, false);
            helper.getView(R.id.tv_label_name).setBackgroundResource(R.drawable.shape_button_tab_false);
            ((TextView)helper.getView(R.id.tv_label_name)).setTextColor(mContext.getResources().getColor(R.color.black_new_two));
        }
    }
}
