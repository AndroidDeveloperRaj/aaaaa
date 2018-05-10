package com.merrichat.net.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.utils.TimeTools;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 拼团列表适配器
 * Created by amssy on 18/1/18.
 */

public class PintuanAdapter extends RecyclerView.Adapter<PintuanAdapter.ViewHolder> {

    private Context context;
    //用于退出activity,避免countdown，造成资源浪费。
    private SparseArray<CountDownTimer> countDownMap;
    private List<PintuanModel.DataBean.ListBean> listData;

    public PintuanAdapter(Context context, List<PintuanModel.DataBean.ListBean> listData) {
        this.context = context;
        this.listData = listData;
        countDownMap = new SparseArray<>();
    }


    /**
     * 清空资源
     */
    public void cancelAllTimers() {
        if (countDownMap == null) {
            return;
        }
        for (int i = 0, length = countDownMap.size(); i < length; i++) {
            CountDownTimer cdt = countDownMap.get(countDownMap.keyAt(i));
            if (cdt != null) {
                cdt.cancel();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pintuan, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        holder.simpleDraweeViewPintuan.setImageURI(listData.get(position).getSeriaCreateMemberUrl());
        holder.tvPintuanName.setText(listData.get(position).getSeriaCreateMemberName());
        if (listData.get(position).getDifference() > 0) {
            holder.tvPerson.setText("还差" + listData.get(position).getDifference() + "人");
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvCenter.setVisibility(View.VISIBLE);
        } else {
            holder.tvPerson.setText("拼团人数已满");
            holder.tvTime.setVisibility(View.GONE);
            holder.tvCenter.setVisibility(View.GONE);
        }

        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }

        final long time = 24 * 3600 * 1000 - listData.get(position).getElapsedTime();//倒计时时间

        //时间倒计时
        if (time > 0) {
            holder.countDownTimer = new CountDownTimer(time, 1000) {

                public void onTick(long millisUntilFinished) {
                    String countTimeByLong = TimeTools.getCountTimeByLong(millisUntilFinished);
                    holder.tvTime.setText(countTimeByLong);
                }

                public void onFinish() {
                    holder.tvTime.setText("00:00:00");
                }
            }.start();

            countDownMap.put(holder.tvTime.hashCode(), holder.countDownTimer);
        } else {
            holder.tvTime.setText("00:00:00");
        }

        //去拼团按钮
        holder.tvGotoPintuan.setOnClickListener(new View.OnClickListener() {
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
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CountDownTimer countDownTimer;
        @BindView(R.id.simpleDraweeView_pintuan)
        SimpleDraweeView simpleDraweeViewPintuan;
        @BindView(R.id.tv_goto_pintuan)
        TextView tvGotoPintuan;

        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_pintuan_name)
        TextView tvPintuanName;
        @BindView(R.id.tv_person)
        TextView tvPerson;

        @BindView(R.id.tv_center)
        TextView tvCenter;

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
