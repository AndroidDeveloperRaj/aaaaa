package com.merrichat.net.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/16.
 */

public class HisYingJiAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    private DianZanOnCheckListener dianZanOnCheckListener;

    public HisYingJiAdapter(int viewId, ArrayList<T> list) {
        super(viewId, list);

    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (item instanceof HisHomeModel.DataBean.MovieListBean) {
            final int position = helper.getLayoutPosition();
            RelativeLayout rl_all = helper.getView(R.id.rl_all);
            ViewGroup.LayoutParams params = rl_all.getLayoutParams();
//            params.height = (int) (((HisHomeModel.DataBean.MovieListBean) item).getCover().getHeight() /
//                    ((float) ((HisHomeModel.DataBean.MovieListBean) item).getCover().getWidth() / (StringUtil.getWidths(mContext) / 2 - 30)));
            params.height = (StringUtil.getWidths(mContext) - 160) / 3;
            rl_all.setLayoutParams(params);

            ((SimpleDraweeView) helper.getView(R.id.simple_cover)).setImageURI(((HisHomeModel.DataBean.MovieListBean) item).getCover().getUrl());
            ((SimpleDraweeView) helper.getView(R.id.simple_header)).setImageURI(((HisHomeModel.DataBean.MovieListBean) item).getMemberImage());
            helper.setText(R.id.tv_distance, "距离").setText(R.id.tv_name, ((HisHomeModel.DataBean.MovieListBean) item).getMemberName()).setText(R.id.tv_comment, ((HisHomeModel.DataBean.MovieListBean) item).getRMBSign())
                    .setText(R.id.tv_title, ((HisHomeModel.DataBean.MovieListBean) item).getTitle())
                    .setText(R.id.tv_collect, ((HisHomeModel.DataBean.MovieListBean) item).getLikeCounts() + "");
            LinearLayout checkCollect = helper.getView(R.id.ll_check_collect);
            final boolean isChecked = ((HisHomeModel.DataBean.MovieListBean) item).getIsLike() == 0 ? false : true;//是否喜欢 0:不喜欢 1:喜欢
            if (isChecked) {
                helper.setBackgroundRes(R.id.check_collect, R.mipmap.pengyouquan_click_dianzan_2x);
            } else {
                helper.setBackgroundRes(R.id.check_collect, R.mipmap.pengyouquan_dianzan_waibu_2x);

            }
            checkCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dianZanOnCheckListener != null) {
                        dianZanOnCheckListener.dianZanOnCheckListener(isChecked, position);
                    }
                }
            });
        }

    }

    public void setDianZanOnCheckListener(DianZanOnCheckListener dianZanOnCheckListener) {
        this.dianZanOnCheckListener = dianZanOnCheckListener;
    }

    public interface DianZanOnCheckListener {
        void dianZanOnCheckListener(boolean isChecked, int position);
    }


}
