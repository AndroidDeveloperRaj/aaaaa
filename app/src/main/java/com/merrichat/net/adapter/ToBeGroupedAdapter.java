package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.SellOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.view.MyListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/2/5.
 * 群订单管理---待成团adapter
 */

public class ToBeGroupedAdapter extends RecyclerView.Adapter<ToBeGroupedAdapter.ViewHolder> {

    private Context context;
    private List<SellOrderModel.DataBean> list;
    private BuyerListAdapter buyerListAdapter;

    public ToBeGroupedAdapter(Context context, List<SellOrderModel.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_be_grouped, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        SellOrderModel.DataBean data = list.get(position);
        Glide.with(context).load(data.getOrderItemList().get(0).getImg()).centerCrop().into(holder.ivSellContentUrl);
        holder.tvSellContentTitle.setText(data.getOrderItemList().get(0).getProductName());
        holder.tvSellContentDiscripe.setText(data.getOrderItemList().get(0).getProductPropertySpecValue());
        holder.tvSellerNickname.setText("卖家昵称:"+data.getShopMemberName());
        if (data.getSerialMember() != null) {
            buyerListAdapter = new BuyerListAdapter(context, data.getSerialMember());
            holder.myListview.setAdapter(buyerListAdapter);
        }


        if (UserModel.getUserModel().getMemberId().equals(data.getShopMemberId())){
            holder.tvContactSeller.setVisibility(View.GONE);
        }else {
            holder.tvContactSeller.setVisibility(View.VISIBLE);
        }

        buyerListAdapter.setOnItemClickListener(new BuyerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(pos,position);
                }
            }
        });


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onItemClickListener != null) {
//                    int pos = holder.getLayoutPosition();
//                    onItemClickListener.onItemClick(pos);
//                }
//            }
//        });


        holder.tvContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.contactSellerOClick(pos);
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 商品图片
         */
        @BindView(R.id.iv_sell_content_url)
        ImageView ivSellContentUrl;

        /**
         * 商品标题
         */
        @BindView(R.id.tv_sell_content_title)
        TextView tvSellContentTitle;

        /**
         * 商品介绍
         */
        @BindView(R.id.tv_sell_content_discripe)
        TextView tvSellContentDiscripe;
        @BindView(R.id.rl_sell_content)
        RelativeLayout rlSellContent;


        @BindView(R.id.my_listview)
        MyListView myListview;

        /**
         * 联系卖家
         */
        @BindView(R.id.tv_contact_seller)
        TextView tvContactSeller;

        /**
         * 卖家昵称
         */
        @BindView(R.id.tv_seller_nickname)
        TextView tvSellerNickname;


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
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int pos,int position);
        void contactSellerOClick(int position);
    }

}
