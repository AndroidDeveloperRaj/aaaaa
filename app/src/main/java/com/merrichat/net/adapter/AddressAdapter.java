package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.AddressModel;

import java.util.ArrayList;

/**
 * 地址列表适配器
 * Created by amssy on 18/1/18.
 */

public class AddressAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {


    private final String key;

    public AddressAdapter(String key, int viewId, ArrayList<T> list) {
        super(viewId, list);
        this.key = key;
    }


    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (helper.getPosition() == 0) {
            helper.setGone(R.id.view_top, false);
        } else {
            helper.setGone(R.id.view_top, true);
        }
        helper.setText(R.id.tv_name, ((AddressModel) item).getName()).addOnClickListener(R.id.tv_editor).addOnClickListener(R.id.tv_delete);

        if (key.equals("rec")) {//实物地址
            helper.setText(R.id.tv_phone, ((AddressModel) item).getMobile())
                    .setText(R.id.tv_address, ((AddressModel) item).getRecAddress() + ((AddressModel) item).getDetAddress());
        } else {//虚拟地址
            helper.setText(R.id.tv_phone, ((AddressModel) item).getType())
                    .setText(R.id.tv_address, ((AddressModel) item).getAccountAddr());
        }

    }

}
