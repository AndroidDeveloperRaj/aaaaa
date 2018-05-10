package com.merrichat.net.adapter;

import android.graphics.Color;
import android.view.View;
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

public class PhotoFilmMoveAdapter extends BaseQuickAdapter<PhotoFilmPicModel, BaseViewHolder> {
    private int itemType;

    public PhotoFilmMoveAdapter(int itemType, int viewId, ArrayList<PhotoFilmPicModel> cutList) {
        super(viewId, cutList);
        this.itemType = itemType;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoFilmPicModel item) {

            if (itemType == PhotoFilmPicModel.move_itemType) {//动作
                if (helper.getLayoutPosition() == 0) {//第一条
                    helper.getView(R.id.iv_photofilm_cut_icon).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_photofilm_cut_name, item.getCutName());
                } else {
                    helper.getView(R.id.iv_photofilm_cut_icon).setVisibility(View.GONE);
                    helper.setText(R.id.tv_photofilm_cut_name, item.getCutName());
                }
                if (item.isChecked()) {//设置 动作条目的背景
                    helper.getView(R.id.ll_photofilmlvjing_cut).setSelected(true);
                    if (itemType == PhotoFilmPicModel.move_itemType) {
                        helper.getView(R.id.ll_photofilmlvjing_cut).setBackground(mContext.getResources().getDrawable(R.drawable.photofilm_item_square_selected));
                    }

                } else {
                    helper.getView(R.id.ll_photofilmlvjing_cut).setSelected(false);
                    if (itemType == PhotoFilmPicModel.move_itemType) {
                        helper.getView(R.id.ll_photofilmlvjing_cut).setBackgroundColor(Color.parseColor("#F0F0F0"));
                    }

                }
            } else  {
                helper.setGone(R.id.tv_photofilm_mobanlvjing_name, false);
                if (itemType == PhotoFilmPicModel.tiezhi_itemType) {
                    Glide.with(mContext).load(item.getDrawId()).into((ImageView) helper.getView(R.id.iv_photofilm_pic));

                } else {

                    Glide.with(mContext).load(item.getImgUrl()).into((ImageView) helper.getView(R.id.iv_photofilm_pic));
                }
            }
    }
}
