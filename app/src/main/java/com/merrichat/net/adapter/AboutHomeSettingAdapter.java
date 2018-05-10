package com.merrichat.net.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.AboutHomeSettingModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/6.
 */

public class AboutHomeSettingAdapter extends BaseQuickAdapter<AboutHomeSettingModel,BaseViewHolder> {

    public AboutHomeSettingAdapter(int item_abouthomesetting, ArrayList<AboutHomeSettingModel> settingItemList) {
        super(item_abouthomesetting, settingItemList);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AboutHomeSettingModel aboutHomeSettingModel) {
        TextView tvItemName = baseViewHolder.getView(R.id.tv_item_name);
        if (aboutHomeSettingModel.isChecked()) {
            baseViewHolder.setText(R.id.tv_item_name, aboutHomeSettingModel.getSettingItemName()).setVisible(R.id.iv_checked, true);
            tvItemName.setSelected(true);

        } else {
            baseViewHolder.setText(R.id.tv_item_name, aboutHomeSettingModel.getSettingItemName()).setVisible(R.id.iv_checked, false);
            tvItemName.setSelected(false);

        }
    }

}
