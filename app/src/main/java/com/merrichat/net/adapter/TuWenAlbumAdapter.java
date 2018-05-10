package com.merrichat.net.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.StringUtil;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/12.
 */

public class TuWenAlbumAdapter extends BaseQuickAdapter<PhotoVideoModel, BaseViewHolder> {
    public TuWenAlbumAdapter(int layoutResId, @Nullable List<PhotoVideoModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoVideoModel item) {
        String itemText = item.getText();
        String itemUrl = item.getUrl();
        if (!RxDataTool.isNullString(itemText)) {
            helper.setText(R.id.tv_content_text, itemText).setGone(R.id.tv_content_text, true);
        } else {
            helper.setGone(R.id.tv_content_text, false);
        }
        if (!RxDataTool.isNullString(itemUrl)) {
            ((SimpleDraweeView)helper.getView(R.id.iv_content_img)).setImageURI(itemUrl);
            //设置图片宽高
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) helper.getView(R.id.iv_content_img).getLayoutParams();
            params.height = (int) (item.getHeight() / ((float) item.getWidth() / (StringUtil.getWidths(mContext) - 20)));
            helper.getView(R.id.iv_content_img).setLayoutParams(params);

            helper.setGone(R.id.iv_content_img, true);

            helper.addOnClickListener(R.id.iv_content_img);
        } else {
            helper.setGone(R.id.iv_content_img, false);
        }
    }
}
