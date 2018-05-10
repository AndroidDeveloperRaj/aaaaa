package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.AddGroupModel;
import com.merrichat.net.utils.MerriUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 添加群列表适配器
 * Created by amssy on 18/1/18.
 */

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupAdapter.ViewHolder> {
    private Context context;
    private List<AddGroupModel.DataBean.ListBean> listBeans;
    public String mKeyWord;

    public AddGroupAdapter(Context context, List<AddGroupModel.DataBean.ListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
    }

    public void setKeyWord(String mKeyWord) {
        this.mKeyWord = mKeyWord;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_group, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        holder.simpleHeader.setImageURI(listBeans.get(position).getCommunityImgUrl());
        //搜索的群 关键字加粗处理
        if (!TextUtils.isEmpty(mKeyWord)) {
            if (listBeans.get(position).getCommunityName() != null && listBeans.get(position).getCommunityName().contains(mKeyWord)) {
                int index = listBeans.get(position).getCommunityName().indexOf(mKeyWord);
                int len = mKeyWord.length();
                Spanned temp = Html.fromHtml(listBeans.get(position).getCommunityName().substring(0, index)
                        + "<b>"
                        + listBeans.get(position).getCommunityName().substring(index, index + len)
                        + "</b>"
                        + listBeans.get(position).getCommunityName().substring(index + len, listBeans.get(position).getCommunityName().length()));

                holder.tvGroupName.setText(temp);
            } else {
                holder.tvGroupName.setText(listBeans.get(position).getCommunityName());
            }
        } else {
            holder.tvGroupName.setText(listBeans.get(position).getCommunityName());
        }
        String type = listBeans.get(position).getType();//群类型 0：交流群，1：微商群（BTC） 2：集市群(CTC)
        switch (type) {
            case "0":
                holder.btnGroupType.setText("交流群");
                holder.btnGroupType.setBackgroundColor(Color.rgb(131,204,240));
                break;
            case "1":
                holder.btnGroupType.setText("电商群");
                holder.btnGroupType.setBackgroundColor(Color.rgb(240, 41, 131));
                break;
            case "2":
                holder.btnGroupType.setText("集市群");
                holder.btnGroupType.setBackgroundColor(Color.rgb(243, 137, 34));
                break;
        }
        String notice = listBeans.get(position).getCommunityNotice();
        if (TextUtils.isEmpty(notice)){
            notice = "暂无群介绍";
        }
        holder.tvGroupContent.setText("群介绍:" + notice);
        holder.tvAddress.setText(listBeans.get(position).getAddress() + "  " + listBeans.get(position).getDistance());
        int isJoin = listBeans.get(position).getIsJoin();//是否加入过0:没有，1：申请过该群，还没通过，2：已经是该群成员
        if (isJoin == 2) {
            holder.btnAddGroup.setText("已加入");
            holder.btnAddGroup.setTextColor(Color.rgb(136,136,136));
            holder.btnAddGroup.setBackgroundResource(R.drawable.shape_recharge_no);
        } else if (isJoin == 0) {
            holder.btnAddGroup.setText("加入");
            holder.btnAddGroup.setTextColor(Color.rgb(255,61,111));
            holder.btnAddGroup.setBackgroundResource(R.drawable.shape_recharge_yes);
        } else {
            holder.btnAddGroup.setText("等待同意");
            holder.btnAddGroup.setTextColor(Color.rgb(136,136,136));
            holder.btnAddGroup.setBackgroundResource(R.drawable.shape_recharge_no);

        }

        holder.btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MerriUtils.isFastDoubleClick2()){
                    return;
                }
                if (onItemClick != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClick.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_add_group)
        TextView btnAddGroup;
        @BindView(R.id.simple_header)
        SimpleDraweeView simpleHeader;
        @BindView(R.id.tv_group_name)
        TextView tvGroupName;
        @BindView(R.id.btn_group_type)
        Button btnGroupType;
        @BindView(R.id.tv_group_content)
        TextView tvGroupContent;
        @BindView(R.id.tv_address)
        TextView tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public onAddGroupItemClickLinster onItemClick;

    public void setAddGroupItemClickLinster(onAddGroupItemClickLinster onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface onAddGroupItemClickLinster {
        void onItemClick(int position);
    }
}
