package com.merrichat.net.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;

import java.io.File;
import java.util.List;

/**
 * Created by amssy on 17/11/13.
 */

public class PhotoAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {


    public PhotoAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Glide.with(mContext).load(new File(item)).thumbnail(0.5f).into((ImageView) helper.getView(R.id.iv_photo));
    }
}
