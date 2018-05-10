package com.merrichat.net.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/12/5.
 */

public class ComplaintOthersAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public ComplaintOthersAdapter(int viewId, ArrayList<String> imgUrlList) {
        super(viewId, imgUrlList);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.iv_img));
    }
}
