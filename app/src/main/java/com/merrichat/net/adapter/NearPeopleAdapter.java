package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/15.
 * 美遇---附近的人adapter
 */

public class NearPeopleAdapter extends RecyclerView.Adapter<NearPeopleAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private List<Integer> mHeights;

    private Context context;

    public NearPeopleAdapter(Context context) {
        this.context = context;
        mHeights = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_near_people, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据


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
        return 100;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 封面图
         */
        @BindView(R.id.iv_near_image)
        ImageView ivNearImage;

        /**
         * 标题
         */
        @BindView(R.id.tv_title)
        TextView tvTitle;

        /**
         * 内容
         */
        @BindView(R.id.tv_content)
        TextView tvContent;

        /**
         * 头像
         */
        @BindView(R.id.cv_friends_photo)
        CircleImageView cvFriendsPhoto;

        /**
         * 名字/昵称
         */
        @BindView(R.id.tv_near_name)
        TextView tvNearName;

        /**
         * 距离
         */
        @BindView(R.id.tv_ju_li)
        TextView tvJuLi;


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
