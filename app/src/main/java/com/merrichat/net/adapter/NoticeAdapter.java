package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.model.NoticeModel;
import com.merrichat.net.utils.DateTimeUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/8/15.
 * 通知adapter
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    private Context context;
    private List<NoticeModel> list;

    public NoticeAdapter(Context context, List<NoticeModel> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        final NoticeModel noticeModel = list.get(position);

        holder.tvNoticeTime.setText(DateTimeUtil.NoticeTime(noticeModel.getPushTime(), System.currentTimeMillis()));

        holder.tvNoticeContent.setText(noticeModel.getContent());

        if (!TextUtils.isEmpty(noticeModel.getAccountStatus())) {
            String temp = noticeModel.getAccountStatus();
            if ("1".equals(temp)) {//1 未绑定  2 未认证
                holder.lay_bottom.setVisibility(View.VISIBLE);
                holder.tv_bottom_flag.setText("立即绑定");
            } else if ("2".equals(temp)) {
                holder.lay_bottom.setVisibility(View.VISIBLE);
                holder.tv_bottom_flag.setText("立即认证");
            } else {
                holder.lay_bottom.setVisibility(View.GONE);
            }
        } else {
            holder.lay_bottom.setVisibility(View.GONE);
        }

        holder.tvNoticeContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongOnclick(holder.itemView, pos);
                }
                return true;
            }
        });

        holder.tvNoticeContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.OnTouchListenerOnclick(pos, motionEvent);
                }
                return false;
            }
        });

        holder.lay_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.OnBindClick(noticeModel);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_notice_content)
        TextView tvNoticeContent;

        @BindView(R.id.tv_notice_time)
        TextView tvNoticeTime;
        @BindView(R.id.lay_bottom)
        LinearLayout lay_bottom;
        @BindView(R.id.tv_bottom_flag)
        TextView tv_bottom_flag;

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
        void onItemLongOnclick(View view, int position);

        void OnTouchListenerOnclick(int position, MotionEvent motionEvent);

        void OnBindClick(NoticeModel noticeModel);
    }
}