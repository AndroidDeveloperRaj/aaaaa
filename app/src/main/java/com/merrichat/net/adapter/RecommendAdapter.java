package com.merrichat.net.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.CircleModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.FrescoUtils;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.RoundAngleFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/17.
 * 朋友圈---推荐adapter
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;

    private List<CircleModel.ListBean> listBeans;
    private DianZanOnCheckListener dianZanOnCheckListener;
    private List<Integer> mHeights;
    private List<Integer> listColor = new ArrayList<>();
    private int colorNum = 0;

    public void clearHeight(){
        mHeights.clear();
    }

    public RecommendAdapter(Context context, List<CircleModel.ListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
        mHeights = new ArrayList<>();
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_friends, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        CircleModel.ListBean listBean = listBeans.get(position);
        CircleModel.LogBean logBean = listBean.log;

        String title = logBean.title;
        String memberImage = logBean.memberImage;
        String memberName = logBean.memberName;
        int commontCount = logBean.commentCounts;
        int likeCounts = logBean.likeCounts;

        PhotoVideoModel model = JSON.parseObject(logBean.cover, PhotoVideoModel.class);
        String coverImage = model.getUrl();
        int width = model.getWidth();
        int height = model.getHeight();

        if (mHeights.size() <= position) {
            mHeights.add((int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30))));
        }

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.rlAll.getLayoutParams();
        params.height = (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30)));
        holder.rlAll.setLayoutParams(params);

        //默认颜色
        if (colorNum == 7){
            colorNum = 0;
        }else {
            colorNum ++;
        }
        holder.simpleCover.setBackgroundColor(context.getResources().getColor(listColor.get(colorNum)));

        //是否喜欢
        final boolean isLike = listBean.likes;
        if (isLike) {
            holder.checkCollect.setImageDrawable(context.getResources().getDrawable(R.mipmap.pengyouquan_click_dianzan_2x));
//            Glide.with(context).load(R.mipmap.pengyouquan_click_dianzan_2x).into(holder.checkCollect);
        } else {
            holder.checkCollect.setImageDrawable(context.getResources().getDrawable(R.mipmap.pengyouquan_dianzan_waibu_2x));
//            Glide.with(context).load(R.mipmap.pengyouquan_dianzan_waibu_2x).into(holder.checkCollect);
        }
        holder.linBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dianZanOnCheckListener != null) {
                    dianZanOnCheckListener.dianZanOnCheckListener(isLike, position);
                }
            }
        });

        //holder.simpleCover.setImageURI(coverImage);
        //FrescoUtils.setFrescoImageUri(Uri.parse(coverImage), holder.simpleCover, StringUtil.getWidths(context) / 2 - 30, (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30))));
        Uri uri = Uri.parse(coverImage);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                .build();
        holder.simpleCover.setController(draweeController);

//        if (logBean.flag == 2){
//        Uri uri = Uri.parse("file:///storage/emulated/0/MerriChat/cover/MerriChat_Cover_20180419140659935.gif");
//        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//                .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
//                .build();
//        holder.simpleCover.setController(draweeController);
//        }else if (logBean.flag == 1){
//            FrescoUtils.setFrescoImageUri(Uri.parse(coverImage), holder.simpleCover, StringUtil.getWidths(context) / 2 - 30, (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30))));
//        }
        if (logBean.flag == 2){
            holder.imageViewVideo.setVisibility(View.VISIBLE);
        }else if (logBean.flag == 1){
            holder.imageViewVideo.setVisibility(View.GONE);
        }

        holder.simpleHeader.setImageURI(memberImage);
        holder.tvName.setText(memberName);
        holder.tvTitle.setText(title);
        //holder.tvComment.setText("" + commontCount);
        if (likeCounts > 9999) {
            double likeCounts1 = likeCounts;
            holder.tvCollect.setText(StringUtil.rounded(likeCounts1 / 10000, 1) + "w");
        } else {
            holder.tvCollect.setText("" + likeCounts);
        }

        if (!TextUtils.isEmpty(listBean.getIncome())) {
            //赚钱
            if (Double.valueOf(listBean.getIncome()) >= 10000) {
                double income = Double.valueOf(listBean.getIncome());
                holder.tvComment.setText(StringUtil.rounded(income / 10000, 1) + "w");
            } else {
                holder.tvComment.setText(listBean.getIncome());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(pos);
                }
            }
        });
    }

    public void setDianZanOnCheckListener(DianZanOnCheckListener dianZanOnCheckListener) {
        this.dianZanOnCheckListener = dianZanOnCheckListener;
    }

    @Override
    public int getItemCount() {
        return listBeans.size();
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface DianZanOnCheckListener {
        void dianZanOnCheckListener(boolean isChecked, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simple_cover)
        SimpleDraweeView simpleCover;
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.rl_all)
        RelativeLayout rlAll;
        @BindView(R.id.lin_bottom)
        LinearLayout linBottom;
        @BindView(R.id.check_collect)
        ImageView checkCollect;
        @BindView(R.id.tv_collect)
        TextView tvCollect;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.imageView_video)
        ImageView imageViewVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
