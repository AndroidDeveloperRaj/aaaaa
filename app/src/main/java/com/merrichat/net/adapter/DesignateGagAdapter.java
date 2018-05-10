package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupMembersModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/31.
 * 指定禁言adapter
 */

public class DesignateGagAdapter extends RecyclerView.Adapter<DesignateGagAdapter.ViewHolder> {


    private final List<GroupMembersModel> groupMembersList;
    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;

    private int sourceFlag;


    public DesignateGagAdapter(Context context, List<GroupMembersModel> groupMembersList,int sourceFlag) {
        this.context = context;
        this.groupMembersList = groupMembersList;
        this.sourceFlag = sourceFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_designate_gag, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GroupMembersModel listBean = groupMembersList.get(position);
        holder.tvMemberName.setText(listBean.getMemberName());

        //指定禁言专用
        if (sourceFlag==1){
            if (listBean.isChecked()) {
                holder.ivGossip.setImageResource(R.mipmap.accept_2x_true);
            } else {
                holder.ivGossip.setImageResource(R.mipmap.accept_2x_none);
            }
        }else if (sourceFlag==2){
            //设置群管理专用
            if (listBean.getIsMater()==1){
                holder.ivGossip.setImageResource(R.mipmap.accept_2x_true);
            }else {
                holder.ivGossip.setImageResource(R.mipmap.accept_2x_none);
            }
        }

        Glide.with(context).load(listBean.getHeadImgUrl()).into(holder.civMemberPhoto);
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
        return groupMembersList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.civ_member_photo)
        CircleImageView civMemberPhoto;

        @BindView(R.id.tv_member_name)
        TextView tvMemberName;

        @BindView(R.id.iv_gossip)
        ImageView ivGossip;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
