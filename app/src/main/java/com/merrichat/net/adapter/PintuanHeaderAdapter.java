package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.PintuanDetailModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 拼团人头像列表适配器
 * Created by amssy on 18/1/18.
 */

public class PintuanHeaderAdapter extends RecyclerView.Adapter<PintuanHeaderAdapter.ViewHolder> {

    private Context context;
    private List<PintuanDetailModel.DataBean.SerialMemberBean> listUrl;
    private int listSize = 0;

    public PintuanHeaderAdapter(Context context,List<PintuanDetailModel.DataBean.SerialMemberBean> listUrl) {
        this.context = context;
        this.listUrl = listUrl;
    }

    public void setListSize(int listSize){
        this.listSize = listSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pintuan_header, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        if (listUrl.size() > position){
            holder.simpleHeader.setImageURI(listUrl.get(position).getUrl());
            holder.simpleHeader.setVisibility(View.VISIBLE);
            holder.simpleHeaderNone.setVisibility(View.GONE);
        }else {
            holder.simpleHeader.setVisibility(View.GONE);
            holder.simpleHeaderNone.setVisibility(View.VISIBLE);
        }

        holder.simpleHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClick.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSize;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleHeader;
        @BindView(R.id.simple_header_none)
        SimpleDraweeView simpleHeaderNone;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public onPintuanItemClickLinster onItemClick;

    public void setPintuanItemClickLinster(onPintuanItemClickLinster onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface onPintuanItemClickLinster {
        void onItemClick(int position);
    }
}
