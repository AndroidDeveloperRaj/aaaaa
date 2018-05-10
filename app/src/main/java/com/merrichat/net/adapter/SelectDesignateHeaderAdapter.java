package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupMembersModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/2/1.
 */

public class SelectDesignateHeaderAdapter extends RecyclerView.Adapter<GroupHeaderAdapter.ViewHolder> {

    private Context context;
    private List<GroupMembersModel> list;
    public SelectDesignateHeaderAdapter(Context context , List<GroupMembersModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public GroupHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_header, parent, false);
        // 实例化viewholder
        GroupHeaderAdapter.ViewHolder viewHolder = new GroupHeaderAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupHeaderAdapter.ViewHolder holder, final int position) {
        // 绑定数据
        holder.simpleImage.setImageURI(list.get(position).getHeadImgUrl());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

