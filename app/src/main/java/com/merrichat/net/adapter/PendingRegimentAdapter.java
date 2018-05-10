package com.merrichat.net.adapter;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.activity.grouporder.SellOrderDetailAty;
import com.merrichat.net.model.SellOrderModel;
import com.merrichat.net.view.NoScrollRecyclerView;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/19.
 * <p>
 * 销售订单--待成团adapter
 */

public class PendingRegimentAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public PendingRegimentAdapter(int viewId, List<T> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        SellOrderModel.DataBean.OrderItemListBean orderItemListBean = ((SellOrderModel.DataBean) item).getOrderItemList().get(0);
        helper.setText(R.id.tv_shop_name, orderItemListBean.getProductName())//商品名
                .setText(R.id.tv_shop_sku, orderItemListBean.getProductPropertySpecValue())
                .setBackgroundRes(R.id.tv_contact_buyer, R.drawable.shape_contact_cancle)//取消成团背景
                .setTextColor(R.id.tv_contact_buyer, mContext.getResources().getColor(R.color.base_888888))
                .setText(R.id.tv_contact_buyer, "取消成团").setText(R.id.tv_contact_seller, "同意成团")
                .setGone(R.id.tv_contact_cancle, false)
                .addOnClickListener(R.id.tv_contact_buyer)//取消成团
                .addOnClickListener(R.id.tv_contact_seller);//同意成团
        SimpleDraweeView imageView = helper.getView(R.id.simple_order);
        imageView.setImageURI(orderItemListBean.getImg());//商品图片

        final List<SellOrderModel.DataBean.SerialMemberBean> memberBeanList = ((SellOrderModel.DataBean) item).getSerialMember();
        NoScrollRecyclerView rlRecyclerviewItem = helper.getView(R.id.rl_recyclerview_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rlRecyclerviewItem.setLayoutManager(layoutManager);
        PendingRegimentItemAdapter pendingRegimentItemAdapter = new PendingRegimentItemAdapter(R.layout.item_buyer_list, memberBeanList);
        pendingRegimentItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, SellOrderDetailAty.class);
                intent.putExtra("serialNumber", memberBeanList.get(position).getOrderId());
                intent.putExtra("fragmentId", "待成团");
                intent.putExtra("hisMemberId", memberBeanList.get(position).getMemberId());
                mContext.startActivity(intent);
            }
        });
        rlRecyclerviewItem.setAdapter(pendingRegimentItemAdapter);

    }
}
