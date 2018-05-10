package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.SellOrderModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/2/5.
 * 群订单管理---待发货
 */

public class ToBeDeliveredAdapter extends RecyclerView.Adapter<ToBeDeliveredAdapter.ViewHolder> {

    private Context context;
    private List<SellOrderModel.DataBean> list;
    private int source;
    private OnItemClickListener onItemClickListener;

    public ToBeDeliveredAdapter(Context context, List<SellOrderModel.DataBean> list, int source) {
        this.context = context;
        this.list = list;
        this.source = source;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_be_delivered, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        SellOrderModel.DataBean data = list.get(position);
        holder.tvBuyerAddress.setText("买家地址：" + data.getAddresseeDetailed());
        holder.tvBuyerNickname.setText("买家昵称：" + data.getMemberName());
        holder.tvBuyerName.setText("买家姓名:" + data.getAddresseeName());

        Glide.with(context).load(data.getOrderItemList().get(0).getImg()).centerCrop().into(holder.ivSellContentUrl);

        holder.tvSellContentTitle.setText(data.getOrderItemList().get(0).getProductName());
        holder.tvSellContentDiscripe.setText(data.getOrderItemList().get(0).getProductPropertySpecValue());
        holder.tvOrderPay.setText(Html.fromHtml(" 共<font color='#FF3D6F'>" + data.getOrderItemList().get(0).getProductNum() + "</font>件商品合计<font color='#FF3D6F'>" + data.getTotalAmount() + "</font>(含运费" + data.getDeliveryFee() + ")"));
        if (source == 2) {
            holder.tvCheckTransMap.setVisibility(View.GONE);
        } else {
            holder.tvCheckTransMap.setVisibility(View.VISIBLE);
            if (source == 5) {
                if ("4".equals(data.getReStatus())) {         // 0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
                    //交易类型 1:个人，2:c2c群，3:b2c(微商)群  只有c2c才能由群主/管理员仲裁
                    if ("2".equals(data.getTransactionType())) {
                        //true:表示超过了24小时,false:表示在24小时内
                        if (!data.isArbitrate_due()) {
                            holder.tvCheckTransMap.setVisibility(View.VISIBLE);
                            holder.tvCheckTransMap.setText("仲裁退款");
                        } else {
                            holder.tvCheckTransMap.setVisibility(View.GONE);
                        }
                    }
                } else {
                    holder.tvCheckTransMap.setVisibility(View.GONE);
                }
            } else {
                holder.tvCheckTransMap.setText("查看物流");
            }
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

        //联系卖家
        holder.tvContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.contactSellerOClick(pos);
                }
            }
        });

        //联系买家
        holder.tvCallSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.CallSellerOClick(pos);
                }
            }
        });

        //查看物流
        holder.tvCheckTransMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.CheckTransMapOClick(pos);
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

        void contactSellerOClick(int position);

        void CallSellerOClick(int position);

        void CheckTransMapOClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 买家地址
         */
        @BindView(R.id.tv_buyer_address)
        TextView tvBuyerAddress;

        @BindView(R.id.rl_buyer_address)
        RelativeLayout rlBuyerAddress;

        /**
         * 买家昵称
         */
        @BindView(R.id.tv_buyer_nickname)
        TextView tvBuyerNickname;

        /**
         * 买家姓名
         */
        @BindView(R.id.tv_buyer_name)
        TextView tvBuyerName;
        @BindView(R.id.ll_buyer_information)
        LinearLayout llBuyerInformation;

        /**
         * 商品图片
         */
        @BindView(R.id.iv_sell_content_url)
        ImageView ivSellContentUrl;

        /**
         * 商品title
         */
        @BindView(R.id.tv_sell_content_title)
        TextView tvSellContentTitle;

        /**
         * 商品详情
         */
        @BindView(R.id.tv_sell_content_discripe)
        TextView tvSellContentDiscripe;

        /**
         * 商品数量and价格、运费
         */
        @BindView(R.id.tv_order_pay)
        TextView tvOrderPay;

        /**
         * 联系买家
         */
        @BindView(R.id.tv_call_seller)
        TextView tvCallSeller;

        /**
         * 联系卖家
         */
        @BindView(R.id.tv_contact_seller)
        TextView tvContactSeller;

        /**
         * 查看物流/仲裁退款
         */
        @BindView(R.id.tv_check_trans_map)
        TextView tvCheckTransMap;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
