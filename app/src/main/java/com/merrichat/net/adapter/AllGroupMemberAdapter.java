package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupMemberModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/20.
 * 群主页---群成员展示（只展示两行）
 */

public class AllGroupMemberAdapter extends RecyclerView.Adapter<AllGroupMemberAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;
    private List<GroupMemberModel> list;

    private int NumFlag;


    /**
     * 身份flag
     * 默认为false
     * 如果是群主and管理员  则为  true
     * 如果为群成员 则为false
     */
    private boolean IdentityFlag;

    public AllGroupMemberAdapter(Context context, List<GroupMemberModel> list, int NumFlag, boolean IdentityFlag) {
        this.context = context;
        this.list = list;
        this.NumFlag = NumFlag;
        this.IdentityFlag = IdentityFlag;
    }

    public void setNotifyDataSetChanged(Context context, List<GroupMemberModel> list, int NumFlag, boolean IdentityFlag) {
        this.context = context;
        this.list = list;
        this.NumFlag = NumFlag;
        this.IdentityFlag = IdentityFlag;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_group_member, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GroupMemberModel model = list.get(position);
        holder.tvMemberName.setText(model.getMemberName());
        if (IdentityFlag) {
            if (list.size()>0){
                if (position == NumFlag) {
                    holder.civPhotoMember.setImageResource(R.mipmap.tianjiarenyuan);
                } else if (position == NumFlag + 1) {
                    holder.civPhotoMember.setImageResource(R.mipmap.shanchurenyuan);
                } else {
                    Glide.with(context).load(model.getHeadImgUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).error(R.mipmap.ic_preloading).into(holder.civPhotoMember);
                }
            }
        } else {
            Glide.with(context).load(model.getHeadImgUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).error(R.mipmap.ic_preloading).into(holder.civPhotoMember);
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


    @Override
    public int getItemCount() {
        return list.size();
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

        @BindView(R.id.civ_photo_member)
        CircleImageView civPhotoMember;


        @BindView(R.id.tv_member_name)
        TextView tvMemberName;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
