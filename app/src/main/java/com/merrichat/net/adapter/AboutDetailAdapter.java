package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.AttentionModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.utils.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/17.
 * 朋友圈---详情相关帖子adapter
 */

public class AboutDetailAdapter extends RecyclerView.Adapter<AboutDetailAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;

    private List<AttentionModel.DataBean.ListBean> listBeans;

    public AboutDetailAdapter(Context context, List<AttentionModel.DataBean.ListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
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
        AttentionModel.DataBean.ListBean listBean = listBeans.get(position);
        //是否喜欢
        boolean isLike = listBean.isLikes();

        AttentionModel.DataBean.ListBean.BeautyLogBean logBean = listBean.getBeautyLog();

        String title = logBean.getTitle();
        String memberImage = logBean.getMemberImage();
        String memberName = logBean.getMemberName();
        int commontCount = logBean.getCommentCounts();
        int collectCount = logBean.getCollectCounts();

        String cover = logBean.getCover().replace("\\", "");
        PhotoVideoModel model = JSON.parseObject(cover, PhotoVideoModel.class);
        String coverImage = model.getUrl();
        int width = model.getWidth();
        int height = model.getHeight();

        holder.simpleCover.setImageURI(coverImage);
        holder.simpleHeader.setImageURI(memberImage);
        holder.tvName.setText(memberName);
        holder.tvTitle.setText(title);
        holder.tvComment.setText("" + commontCount);
        holder.tvCollect.setText("" + collectCount);
        holder.checkCollect.setChecked(isLike);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.simpleCover.getLayoutParams();
        params.height = (int) (height / ((float) width / (StringUtil.getWidths(context) / 2 - 30)));
        holder.simpleCover.setLayoutParams(params);

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


    @Override
    public int getItemCount() {
        return listBeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simple_cover)
        SimpleDraweeView simpleCover;
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.check_collect)
        CheckBox checkCollect;
        @BindView(R.id.tv_collect)
        TextView tvCollect;
        @BindView(R.id.tv_title)
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
