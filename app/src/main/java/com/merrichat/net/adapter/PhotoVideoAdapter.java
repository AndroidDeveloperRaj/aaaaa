package com.merrichat.net.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.RxTools.RxDataTool;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/20.
 */

public class PhotoVideoAdapter extends BaseItemDraggableAdapter<PhotoVideoModel, BaseViewHolder> {
    public PhotoVideoAdapter(int viewId, ArrayList<PhotoVideoModel> list) {
        super(viewId, list);

    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoVideoModel item) {
        if (!RxDataTool.isNullString(item.getUrl())) {
            helper.setGone(R.id.iv_photofilm_pic, true).setGone(R.id.tv_weijiazai_pic, false);

        } else {
            helper.setGone(R.id.iv_photofilm_pic, false).setGone(R.id.tv_weijiazai_pic, true);
        }
        Glide.with(mContext).load(item.getUrl()).into((ImageView) helper.getView(R.id.iv_photofilm_pic));

        helper.setText(R.id.tv_textcontent, item.getText())
                .addOnClickListener(R.id.rl_img)
                .addOnClickListener(R.id.iv_delete_item)
                .addOnClickListener(R.id.iv_check_shefengmian)
                .addOnClickListener(R.id.ll_add_content)
                .addOnClickListener(R.id.iv_add_photovideo)
                .addOnClickListener(R.id.tv_textcontent)
                .addOnClickListener(R.id.iv_toup)
                .addOnClickListener(R.id.iv_todown);
        if (item.getFlag() == 1) {
            helper.setBackgroundRes(R.id.iv_check_shefengmian, R.mipmap.xuanzhong_2x);
        } else {
//            Glide.with(mContext).load(R.mipmap.shaixuan_weixuanzhong2x).into((ImageView) helper.getView(R.id.iv_check_shefengmian));
            helper.setBackgroundRes(R.id.iv_check_shefengmian, R.mipmap.shaixuan_weixuanzhong2x);

        }
    }
}
