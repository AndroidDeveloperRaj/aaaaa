package com.merrichat.net.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoFilmPicModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/13.
 */

public class PhotoFilmMorePicsAdapter extends BaseQuickAdapter<PhotoFilmPicModel, BaseViewHolder> {

    private final int itemType;

    public PhotoFilmMorePicsAdapter(int itemType, int LayoutId, ArrayList<PhotoFilmPicModel> picsList) {
        super(LayoutId, picsList);
        this.itemType = itemType;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoFilmPicModel item) {
        Glide.with(mContext).load(item.getImgUrl()).into((ImageView) helper.getView(R.id.iv_photofilm_pic));
        helper.addOnClickListener(R.id.iv_photofilm_pic);
        helper.setText(R.id.tv_photofilm_mobanlvjing_name, item.getMoBanLvJingTypeName());
        helper.setVisible(R.id.tv_photofilm_mobanlvjing_name, false);
        if (item.isChecked()) {
            helper.getView(R.id.rl_photofilm_pic).setBackground(mContext.getResources().getDrawable(R.drawable.photofilm_item_square_selected));
        } else {
            helper.getView(R.id.rl_photofilm_pic).setBackgroundColor(Color.parseColor("#00000000"));

        }

    }
}
