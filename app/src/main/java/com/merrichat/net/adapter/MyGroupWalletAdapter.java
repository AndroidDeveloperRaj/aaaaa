package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 群钱包列表适配器
 * Created by amssy on 18/1/18.
 */

public class MyGroupWalletAdapter extends RecyclerView.Adapter<MyGroupWalletAdapter.ViewHolder> {
    private Context context;
    private List<GroupListModel.DataBean.ListBean> listBeans;

    public MyGroupWalletAdapter(Context context, List<GroupListModel.DataBean.ListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_wallet, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        //群头像
        holder.simpleHeader.setImageURI(listBeans.get(position).getCommunityImgUrl());
        //群名称
        holder.tvGroupName.setText(listBeans.get(position).getCommunityName());

        holder.relGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupItemClick.onGroupItemClickLister(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 群头像
         */
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleHeader;
        /**
         * 群名称
         */
        @BindView(R.id.tv_group_name)
        TextView tvGroupName;

        @BindView(R.id.rel_group)
        RelativeLayout relGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public OnGroupItemClick onGroupItemClick;

    public void setOnGroupItemClickLister(OnGroupItemClick onGroupItemClick) {
        this.onGroupItemClick = onGroupItemClick;
    }

    public interface OnGroupItemClick {
        void onGroupItemClickLister(int position);
    }
}
