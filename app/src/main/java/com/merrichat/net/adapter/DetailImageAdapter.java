package com.merrichat.net.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.utils.FrescoUtils;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * 详情图片列表适配器
 * Created by amssy on 18/1/18.
 */

public class DetailImageAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public DetailImageAdapter(int viewId, ArrayList<T> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        //商品图片
        SimpleDraweeView simpleDraweeView = helper.getView(R.id.simple_image);
        //simpleDraweeView.setImageURI((String) item);
        FrescoUtils.setFrescoImageUri(Uri.parse((String) item), simpleDraweeView, StringUtil.getWidths(mContext) , StringUtil.dip2px(mContext,375));

    }
}
