package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/10.
 */

public class MinZuAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MinZuAdapter(int viewId, List<String> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_minzu_text, item);
    }
}
