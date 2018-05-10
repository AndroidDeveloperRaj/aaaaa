package com.merrichat.net.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoFilmPicModel;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/10.
 */

public class PhotoFilmPicsAdapter extends BaseQuickAdapter<PhotoFilmPicModel, BaseViewHolder> {


    private final int itemType;

    public PhotoFilmPicsAdapter(int itemType, int view, List<PhotoFilmPicModel> picsList) {
        super(view, picsList);
        this.itemType = itemType;

    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoFilmPicModel item) {
        helper.addOnClickListener(R.id.iv_photofilm_pic);
        if (itemType == PhotoFilmPicModel.lvJing_itemType) {
            ImageView imageView = helper.getView(R.id.iv_photofilm_pic);
            imageView.setImageDrawable(mContext.getResources().getDrawable(item.getCutIconId()));
            helper.setText(R.id.tv_photofilm_mobanlvjing_name, item.getCutName());
        }
        if (itemType == PhotoFilmPicModel.music_itemType) {
//            ImageView imageView = helper.getView(R.id.iv_photofilm_pic);
//            imageView.setImageDrawable(mContext.getResources().getDrawable(item.getCutIconId()));
//            helper.setText(R.id.tv_photofilm_mobanlvjing_name, item.getCutName());
        }
        if (itemType == PhotoFilmPicModel.moBan_itemType || itemType == PhotoFilmPicModel.jianCai_itemType) {
            Glide.with(mContext).load(item.getImgUrl()).into((ImageView) helper.getView(R.id.iv_photofilm_pic));
        }
        if (itemType == PhotoFilmPicModel.jianCai_itemType) {
            helper.setVisible(R.id.tv_photofilm_mobanlvjing_name, false);
        }
        if (item.isChecked()) {
            helper.getView(R.id.tv_photofilm_mobanlvjing_name).setSelected(true);
            helper.getView(R.id.rl_photofilm_pic).setBackground(mContext.getResources().getDrawable(R.drawable.photofilm_item_square_selected));
        } else {
            helper.getView(R.id.tv_photofilm_mobanlvjing_name).setSelected(false);
            helper.getView(R.id.rl_photofilm_pic).setBackgroundColor(Color.parseColor("#00000000"));

        }

    }


}
