package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2018/1/22.
 */

public class DeliveredAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public DeliveredAdapter(int viewId, ArrayList<T> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        PendingDeliveryModel.DataBean.OrderItemListBean orderItemListBean = ((PendingDeliveryModel.DataBean) item).getOrderItemList().get(0);
        String productType = orderItemListBean.getProductType();//0 实物 1 虚拟
        productType = productType == null ? "0" : productType;
        if (productType.equals("0")) {
            helper.setGone(R.id.tv_contact_cancle, true).setGone(R.id.tv_express_fee, true).setText(R.id.tv_express_fee, "(含运费" + mContext.getResources().getString(R.string.money_character) + StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getDeliveryFee()) + ")");
        } else {
            helper.setGone(R.id.tv_contact_cancle, false).setGone(R.id.tv_express_fee, false).setText(R.id.tv_express_fee, "(含运费" + mContext.getResources().getString(R.string.money_character) + StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getDeliveryFee()) + ")");
        }

        helper.setText(R.id.tv_shop_name, orderItemListBean.getProductName())//商品名
                .setText(R.id.tv_shop_sku, orderItemListBean.getProductPropertySpecValue())//商品详情
                .setText(R.id.tv_address_address, ((PendingDeliveryModel.DataBean) item).getAddresseeDetailed())//买家地址
                .setText(R.id.tv_nick, ((PendingDeliveryModel.DataBean) item).getMemberName())//买家昵称
                .setText(R.id.tv_name, ((PendingDeliveryModel.DataBean) item).getAddresseeName())//买家姓名
                .setText(R.id.tv_product_num, "共" + orderItemListBean.getProductNum() + "件商品 合计:")
                .setText(R.id.tv_total_num, StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getTotalAmount()))

                .addOnClickListener(R.id.ll_product_info)//商品
                .addOnClickListener(R.id.rl_address)
                .addOnClickListener(R.id.tv_contact_cancle)//查看物流
                .setText(R.id.tv_contact_cancle, "查看物流")
                .setVisible(R.id.tv_contact_buyer, false)//联系买家
                .addOnClickListener(R.id.tv_contact_seller)
                .setTextColor(R.id.tv_contact_seller, mContext.getResources().getColor(R.color.base_093FFF))
                .setBackgroundRes(R.id.tv_contact_seller, R.drawable.shape_contact_buyer)
                .setText(R.id.tv_contact_seller, "联系买家");//联系买家}

        SimpleDraweeView imageView = helper.getView(R.id.simple_order);
        imageView.setImageURI(orderItemListBean.getImg());//商品图片
    }
}
