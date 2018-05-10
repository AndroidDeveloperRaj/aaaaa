package com.merrichat.net.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.DraftModel;
import com.merrichat.net.model.MyMovieModel;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.merrichat.net.activity.my.MyDynamicsAty.COLLECTION_FLAG;
import static com.merrichat.net.activity.my.MyDynamicsAty.DRAFT_FLAG;
import static com.merrichat.net.activity.my.MyDynamicsAty.YINGJI_FLAG;

/**
 * Created by AMSSY1 on 2017/11/8.
 */

public class MyAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {


    private final int flag;
    private final Context context;
    private DianZanOnCheckListener dianZanOnCheckListener;
    private int type;
    private List<Integer> listColor = new ArrayList<>();
    private int colorNum = 0;


    public MyAdapter(Context mContext, int flag, int viewId, ArrayList<T> list) {
        super(viewId, list);
        this.flag = flag;
        this.context = mContext;
        //创建默认颜色
        listColor.add(R.color.FF7474);
        listColor.add(R.color.DE99E6);
        listColor.add(R.color.DC86AC);
        listColor.add(R.color._8D7DEA);
        listColor.add(R.color.E4A091);
        listColor.add(R.color._7C95EA);
        listColor.add(R.color.A362FF);
        listColor.add(R.color.FFAD41);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (flag == YINGJI_FLAG && item instanceof MyMovieModel.DataBean.MovieListBean) {
            final int position = helper.getLayoutPosition();

            RelativeLayout rl_all = helper.getView(R.id.rl_all);
            ViewGroup.LayoutParams params = rl_all.getLayoutParams();
          /*  params.height = (int) (((MyMovieModel.DataBean.MovieListBean) item).getCover().getHeight() /
                    ((float) ((MyMovieModel.DataBean.MovieListBean) item).getCover().getWidth() / (StringUtil.getWidths(context) / 2 - 30)));*/
            params.height = (StringUtil.getWidths(mContext) - 160) / 3;
            rl_all.setLayoutParams(params);
            //默认颜色
            if (colorNum == 7) {
                colorNum = 0;
            } else {
                colorNum++;
            }
            ((SimpleDraweeView) helper.getView(R.id.simple_cover)).setBackgroundColor(context.getResources().getColor(listColor.get(colorNum)));
            if (type == COLLECTION_FLAG) {
                helper.setGone(R.id.ll_header, false);
            } else {
                helper.setGone(R.id.ll_header, false);
            }
            ((SimpleDraweeView) helper.getView(R.id.simple_cover)).setImageURI(((MyMovieModel.DataBean.MovieListBean) item).getCover().getUrl());
            ((SimpleDraweeView) helper.getView(R.id.simple_header)).setImageURI(((MyMovieModel.DataBean.MovieListBean) item).getMemberImage());
            helper.setText(R.id.tv_distance, "距离").setText(R.id.tv_comment, ((MyMovieModel.DataBean.MovieListBean) item).getRMBSign())
                    .setText(R.id.tv_name, ((MyMovieModel.DataBean.MovieListBean) item).getMemberName())
                    .setText(R.id.tv_title, ((MyMovieModel.DataBean.MovieListBean) item).getTitle())
                    .setText(R.id.tv_collect, ((MyMovieModel.DataBean.MovieListBean) item).getLikeCounts() + "");
            ImageView checkCollect = helper.getView(R.id.check_collect);
            final boolean isChecked = ((MyMovieModel.DataBean.MovieListBean) item).getIsLike() == 0 ? false : true;//是否喜欢 0:不喜欢 1:喜欢
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
        } else if (flag == DRAFT_FLAG && item instanceof DraftModel) {
            helper.setText(R.id.tv_draft_date, RxTimeTool.getDate(((DraftModel) item).getDate(), "yyyy.MM.dd HH:mm"))
                    .setText(R.id.tv_continue_edit, ((DraftModel) item).getTitle())
                    .addOnClickListener(R.id.tv_draft_share).addOnClickListener(R.id.tv_continue_edit);
            ImageView view = helper.getView(R.id.iv_draft_img);
            Glide.with(mContext).load(((DraftModel) item).getCover()).into(view);
        }
    }

    public void setDianZanOnCheckListener(DianZanOnCheckListener dianZanOnCheckListener) {
        this.dianZanOnCheckListener = dianZanOnCheckListener;
    }

    public void setType(int type) {
        this.type = type;
    }

    public interface DianZanOnCheckListener {
        void dianZanOnCheckListener(boolean isChecked, int position);
    }


}
