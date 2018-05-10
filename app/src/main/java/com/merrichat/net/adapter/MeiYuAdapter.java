package com.merrichat.net.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;

import java.util.List;

/**
 * Created by amssy on 17/11/28.
 */

public class MeiYuAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public MeiYuAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Integer item) {
        helper.setImageResource(R.id.iv_tab, item);
    }
}
