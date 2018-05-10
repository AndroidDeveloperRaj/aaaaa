package com.merrichat.net.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.CircleModel;
import com.merrichat.net.model.NearModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.FrescoUtils;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.DrawableCenterTextViewH;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/16.
 * 朋友圈---附近adapter
 */

public class NearAdapter extends RecyclerView.Adapter<NearAdapter.ViewHolder> {
    private final List<Integer> listColor = new ArrayList<>();
    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;


    private Context context;
    private List<NearModel.DataBean.ListBean> listBeans;
    private DianZanOnCheckListener dianZanOnCheckListener;
    private int colorNum = 0;

    public NearAdapter(Context context, List<NearModel.DataBean.ListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_near, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        NearModel.DataBean.ListBean listBean = listBeans.get(position);

        NearModel.DataBean.ListBean.BeautyLogBean logBean = listBean.beautyLog;

        //String title = logBean.title;
        String memberImage = logBean.memberImage;
        String memberName = logBean.memberName;
        int commontCount = logBean.commentCounts;
        int likeCounts = logBean.likeCounts;

        String cover = logBean.cover.replace("\\", "");
        PhotoVideoModel model = JSON.parseObject(cover, PhotoVideoModel.class);
        String coverImage = model.getUrl();
        int width = model.getWidth();
        int height = model.getHeight();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.simpleCover.getLayoutParams();
        params.height = (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30)));
        holder.simpleCover.setLayoutParams(params);

        //默认颜色
        if (colorNum == 7){
            colorNum = 0;
        }else {
            colorNum ++;
        }
        holder.simpleCover.setBackgroundColor(context.getResources().getColor(listColor.get(colorNum)));

        //距离
        if (listBean.distance < 1) {
            int distance = (int) (listBean.distance * 1000);
            holder.tvDistance.setText(distance + " m");
        } else {
            String distance = new DecimalFormat("#.0").format(listBean.distance);
            holder.tvDistance.setText(distance + " km");
        }
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
//        FrescoUtils.setFrescoImageUri(Uri.parse(coverImage), holder.simpleCover, StringUtil.getWidths(context) / 2 - 30, (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30))));
        Uri uri = Uri.parse(coverImage);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                .build();
        holder.simpleCover.setController(draweeController);

        if (logBean.flag == 2){
            holder.imageViewVideo.setVisibility(View.VISIBLE);
        }else if (logBean.flag == 1){
            holder.imageViewVideo.setVisibility(View.GONE);
        }

        holder.simpleHeader.setImageURI(memberImage);
        holder.tvName.setText(memberName);
        //holder.tvTitle.setText(title);
        //holder.tvComment.setText("" + commontCount);
        if (likeCounts > 9999){
            double likeCounts1 = likeCounts;
            holder.tvCollect.setText(StringUtil.rounded(likeCounts1 / 10000, 1) + "W");
        }else {
            holder.tvCollect.setText("" + likeCounts);
        }

        if (!TextUtils.isEmpty(listBean.income)) {
            //赚钱
            if (Double.valueOf(listBean.income) >= 10000) {
                double income = Double.valueOf(listBean.income);
                holder.tvComment.setText(StringUtil.rounded(income / 10000, 1) + "w");
            } else {
                holder.tvComment.setText(listBean.income);
            }
        }

        holder.simpleCover.setOnClickListener(new View.OnClickListener() {
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
        @BindView(R.id.rl_all)
        RelativeLayout rlAll;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.check_collect)
        ImageView checkCollect;
        @BindView(R.id.lin_bottom)
        LinearLayout linBottom;
        @BindView(R.id.tv_collect)
        TextView tvCollect;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_distance)
        DrawableCenterTextViewH tvDistance;
        @BindView(R.id.imageView_video)
        ImageView imageViewVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
