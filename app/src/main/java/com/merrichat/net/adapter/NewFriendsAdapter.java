package com.merrichat.net.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.NewFriendsModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

/**
 * Created by amssy on 17/11/14.
 * 联系人--新的朋友
 */

public class NewFriendsAdapter extends BaseQuickAdapter<NewFriendsModel.DataBean.InvitationRecordsBean, BaseViewHolder> {
    private OnClickCallBack onClickCallBack;

    public NewFriendsAdapter(int layoutResId, @Nullable List<NewFriendsModel.DataBean.InvitationRecordsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final NewFriendsModel.DataBean.InvitationRecordsBean item) {
        TextView tvName = helper.getView(R.id.tv_new_friends_name);
        TextView tv_content = helper.getView(R.id.tv_content);
        CircleImageView iv = helper.getView(R.id.civ_new_friends);
        TextView tv_invite = helper.getView(R.id.tv_invite);
        Button btnDelete = helper.getView(R.id.btnDelete);
        LinearLayout lay = helper.getView(R.id.lay);

        if (!TextUtils.isEmpty(item.getInviteMemberUrl())) {
            Glide.with(mContext).load(item.getInviteMemberUrl()).into(iv);
        }
        tvName.setText(item.getInviteMemberName());
        if (item.getIsValid() == 0) {
            tv_invite.setText("  同意  ");
            tv_invite.setTextColor(Color.parseColor("#FFFFFF"));
            tv_invite.setBackgroundResource(R.drawable.shape_button_video_music);
            tv_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickCallBack != null) {
                        onClickCallBack.onTongYiClick(helper.getAdapterPosition());
                    }
                }
            });
        } else if (item.getIsValid() == 1) {
            tv_invite.setText("已添加");
            tv_invite.setTextColor(Color.parseColor("#888888"));
            tv_invite.setBackground(null);
        }
        tv_content.setText("请求添加好友");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onDel(helper.getAdapterPosition());
                }
            }
        });

        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemListener!=null){
                    onItemListener.onItemListener(helper.getAdapterPosition());
                }
            }
        });
    }


    public OnClickCallBack getOnClickCallBack() {
        return onClickCallBack;
    }

    public void setOnClickCallBack(OnClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
    }

    public interface OnClickCallBack {
        void onTongYiClick(int pos);
    }

    public interface onSwipeListener {
        void onDel(int pos);
    }

    public interface OnItemListener {
        void onItemListener(int pos);
    }

    private onSwipeListener mOnSwipeListener;
    private OnItemListener onItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public onSwipeListener getOnDelListener() {
        return mOnSwipeListener;
    }

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
}
