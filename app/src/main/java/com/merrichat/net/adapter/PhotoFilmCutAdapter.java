package com.merrichat.net.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoFilmPicModel;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/13.
 */

public class PhotoFilmCutAdapter extends BaseQuickAdapter<PhotoFilmPicModel, BaseViewHolder> {
    private final ArrayList<PhotoFilmPicModel> cutList;

    public PhotoFilmCutAdapter(int viewId, ArrayList<PhotoFilmPicModel> cutList) {
        super(viewId, cutList);
        this.cutList =cutList;

    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoFilmPicModel item) {
        helper.setBackgroundRes(R.id.iv_photofilm_cut_icon,item.getCutIconId());
        helper.setText(R.id.tv_photofilm_cut_name,item.getCutName());
        if (helper.getLayoutPosition() == cutList.size() - 1) {
            helper.setBackgroundColor(R.id.ll_photofilmlvjing_cut, Color.parseColor("#B35B5B"));
        }

    }
}
