package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupMarketModel;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我要买列表适配器
 * Created by amssy on 18/1/18.
 */

public class BuyMarketAdapter extends RecyclerView.Adapter<BuyMarketAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GroupMarketModel.DataBean> listMarket;

    public BuyMarketAdapter(Context context, ArrayList<GroupMarketModel.DataBean> listMarket) {
        this.context = context;
        this.listMarket = listMarket;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_market, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        if (position == 0) {
            holder.viewPaddingTop.setVisibility(View.VISIBLE);
        } else {
            holder.viewPaddingTop.setVisibility(View.GONE);
        }

        holder.tvMarketPrice.setText(StringUtil.getPrice(String.valueOf(listMarket.get(position).getProductPrice())));
        holder.tvMarketPriceTuan.setText(StringUtil.getPrice(String.valueOf(listMarket.get(position).getGroupPrice())));
        holder.simpleDraweeView.setImageURI(listMarket.get(position).getImg());
        holder.tvShopName.setText(listMarket.get(position).getProductName());
        holder.tvMarketName.setText(listMarket.get(position).getMemberNick());
        //holder.tvNum.setText(listMarket.get(position).get);//成交数量隐藏（黄总说的）
        //认证
        if (listMarket.get(position).isIsIdentity()) {
            holder.linAttestation.setVisibility(View.VISIBLE);
        } else {
            holder.linAttestation.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClick.onBuyMarketItemClickLinster(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMarket.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_padding_top)
        View viewPaddingTop;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.tv_market_price_tuan)
        TextView tvMarketPriceTuan;
        @BindView(R.id.simpleDraweeView)
        SimpleDraweeView simpleDraweeView;
        @BindView(R.id.tv_shop_name)
        TextView tvShopName;
        @BindView(R.id.btn_attestation)
        Button btnAttestation;
        @BindView(R.id.tv_market_name)
        TextView tvMarketName;
        @BindView(R.id.tv_num)
        TextView tvNum;

        @BindView(R.id.lin_attestation)
        LinearLayout linAttestation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public onItemClick onItemClick;

    public void setOnBuyMarketItemClickLinster(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface onItemClick {
        void onBuyMarketItemClickLinster(int position);
    }
}
