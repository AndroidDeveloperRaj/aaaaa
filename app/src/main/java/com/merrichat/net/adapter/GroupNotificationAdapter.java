package com.merrichat.net.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupNotificationModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

/**
 * Created by amssy on 18/3/1.
 * 群通知adapter
 */

public class GroupNotificationAdapter extends BaseQuickAdapter<GroupNotificationModel, BaseViewHolder> {


    public GroupNotificationAdapter(int layoutResId, @Nullable List<GroupNotificationModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final GroupNotificationModel item) {
        TextView tvName = helper.getView(R.id.tv_new_friends_name);
        TextView tv_content = helper.getView(R.id.tv_content);
        CircleImageView ivImage = helper.getView(R.id.civ_new_friends);
        TextView tv_invite = helper.getView(R.id.tv_invite);
        Button btnDelete = helper.getView(R.id.btn_delete);
        Button btRefuse = helper.getView(R.id.bt_refuse);

        Glide.with(mContext).load(item.getHeadImgUrl()).error(R.mipmap.ic_preloading).into(ivImage);
        tvName.setText(item.getMemberName());
        if (item.getStatus() == 0) {
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
        } else if (item.getStatus() == 1) {
            tv_invite.setText("已同意");
            tv_invite.setTextColor(Color.parseColor("#888888"));
            tv_invite.setBackground(null);
        } else if (item.getStatus() == 2) {
            tv_invite.setText("已拒绝");
            tv_invite.setTextColor(Color.parseColor("#888888"));
            tv_invite.setBackground(null);
        }
        tv_content.setText(item.getRemark());
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

        btRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onRefuseListener) {
                    onRefuseListener.onRefuse(helper.getAdapterPosition());
                }
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickCallBack != null) {
                    onPhotoOnClick.onPhotoOClick(helper.getAdapterPosition());
                }
            }
        });
    }


    /**
     * 同意
     */
    private OnClickCallBack onClickCallBack;

    public void setOnClickCallBack(OnClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
    }

    public interface OnClickCallBack {
        void onTongYiClick(int pos);
    }


    /**
     * 点击头像
     */
    private OnPhotoClickListener onPhotoOnClick;

    public void setPhotoOnClick(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoOnClick = onPhotoClickListener;
    }

    public interface OnPhotoClickListener {
        void onPhotoOClick(int pos);
    }

    /**
     * 删除
     */
    public interface onSwipeListener {
        void onDel(int pos);
    }

    private onSwipeListener mOnSwipeListener;


    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }


    /**
     * 拒绝
     */
    public interface onRefuseListener {
        void onRefuse(int pos);
    }

    private onRefuseListener onRefuseListener;

    public void setOnRefuseListener(onRefuseListener mOnDelListener) {
        this.onRefuseListener = mOnDelListener;
    }
}
