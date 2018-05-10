package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.ExpressNotificationModel;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.view.CircleImageView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/3/20.
 * 快递通知adapter
 */

public class ExpressNotificationAdapter extends RecyclerView.Adapter<ExpressNotificationAdapter.ViewHolder> {


    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    private Context context;
    List<ExpressNotificationModel> list;

    public ExpressNotificationAdapter(Context context, List<ExpressNotificationModel> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_notification, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        ExpressNotificationModel model = list.get(position);

        holder.tvNoticeTime.setText(DateTimeUtil.formatN(new Date(Long.parseLong(model.getCreateTime()))));
        holder.tvExpressCompany.setText("快递公司:"+model.getNetName());
        holder.tvTimeArrival.setText("到达时间:"+DateTimeUtil.formatForMouthDay(new Date(Long.parseLong(model.getCreateTime()))));
        holder.tvPickupNumber.setText("取件编号:"+model.getNumber());
        holder.tvPickupLocation.setText("取件地点:"+model.getPickupAddr());

        holder.tvCourierName.setText(model.getName());
        holder.tvTelPhone.setText(model.getMemberPhone());
        holder.tvExpressContent.setText(model.getSendContent());
        Glide.with(context).load(model.getImageUrl()).error(R.mipmap.ic_preloading).into(holder.civPhoto);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongOnclick(holder.itemView, pos);
                }
                return true;
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.OnTouchListenerOnclick(pos, motionEvent);
                }
                return false;
            }
        });


        holder.llCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCallPhoneListener != null) {
                    int pos = holder.getLayoutPosition();
                    onCallPhoneListener.onCallPhone(pos);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 时间
         */
        @BindView(R.id.tv_notice_time)
        TextView tvNoticeTime;

        /**
         * 头像
         */
        @BindView(R.id.civ_photo)
        CircleImageView civPhoto;

        /**
         * 快递员名字
         */
        @BindView(R.id.tv_courier_name)
        TextView tvCourierName;

        /**
         * 快递员电话
         */
        @BindView(R.id.tv_tel_phone)
        TextView tvTelPhone;


        /**
         * 拨打电话
         */
        @BindView(R.id.ll_call_phone)
        LinearLayout llCallPhone;


        /**
         * 快递公司名字
         */
        @BindView(R.id.tv_express_company)
        TextView tvExpressCompany;

        /**
         * 到达时间
         */
        @BindView(R.id.tv_time_arrival)
        TextView tvTimeArrival;

        /**
         * 取件编号
         */
        @BindView(R.id.tv_pickup_number)
        TextView tvPickupNumber;

        /**
         * 取件地点
         */
        @BindView(R.id.tv_pickup_location)
        TextView tvPickupLocation;

        /**
         * 短信通知内容
         */
        @BindView(R.id.tv_express_content)
        TextView tvExpressContent;

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
    }


    private OnCallPhoneListener onCallPhoneListener;

    public void setOnCallPhoneListener(OnCallPhoneListener callPhonelistener) {
        this.onCallPhoneListener = callPhonelistener;
    }


    public interface OnCallPhoneListener {
        void onCallPhone(int position);
    }
}
