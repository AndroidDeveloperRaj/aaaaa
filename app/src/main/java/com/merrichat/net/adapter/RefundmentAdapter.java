package com.merrichat.net.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2018/1/22.
 */

public class RefundmentAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public RefundmentAdapter(int viewId, ArrayList<T> list) {
        super(viewId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        PendingDeliveryModel.DataBean.OrderItemListBean orderItemListBean = ((PendingDeliveryModel.DataBean) item).getOrderItemList().get(0);
        String reStatus = ((PendingDeliveryModel.DataBean) item).getReStatus();
        String reStatusText = ((PendingDeliveryModel.DataBean) item).getReStatusText();
        //0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
        if (reStatus.equals("1")) {
            helper.setGone(R.id.tv_contact_cancle, true).setGone(R.id.tv_contact_seller, true)
                    .setGone(R.id.tv_refund_status, false);
        } else {
            helper.setGone(R.id.tv_contact_cancle, false).setGone(R.id.tv_contact_seller, false)
                    .setGone(R.id.tv_refund_status, true).setText(R.id.tv_refund_status, reStatusText);
        }
        String productType = orderItemListBean.getProductType();//0 实物 1 虚拟
        productType = productType == null ? "0" : productType;
        if (productType.equals("0")) {
            helper.setGone(R.id.tv_express_fee, true).setText(R.id.tv_express_fee, "(含运费" + mContext.getResources().getString(R.string.money_character) + StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getDeliveryFee()) + ")");
        } else {
            helper.setGone(R.id.tv_express_fee, false).setText(R.id.tv_express_fee, "(含运费" + mContext.getResources().getString(R.string.money_character) + StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getDeliveryFee()) + ")");
        }
        helper.setText(R.id.tv_shop_name, orderItemListBean.getProductName())//商品名
                .setText(R.id.tv_shop_sku, orderItemListBean.getProductPropertySpecValue())//商品详情
                .setText(R.id.tv_address_address, ((PendingDeliveryModel.DataBean) item).getAddresseeDetailed())//买家地址
                .setText(R.id.tv_nick, ((PendingDeliveryModel.DataBean) item).getMemberName())//买家昵称
                .setText(R.id.tv_name, ((PendingDeliveryModel.DataBean) item).getAddresseeName())//买家姓名
                .setText(R.id.tv_product_num, "共" + orderItemListBean.getProductNum() + "件商品 合计:")
                .setText(R.id.tv_total_num, StringUtil.getPrice(((PendingDeliveryModel.DataBean) item).getTotalAmount()))

                .addOnClickListener(R.id.ll_product_info)
                .addOnClickListener(R.id.rl_address)
                .addOnClickListener(R.id.tv_contact_cancle)
                .addOnClickListener(R.id.tv_contact_buyer)
                .addOnClickListener(R.id.tv_contact_seller)
                .setText(R.id.tv_contact_cancle, "拒绝退款")
                .setText(R.id.tv_contact_seller, "确认退款");//确认退款

        SimpleDraweeView imageView = helper.getView(R.id.simple_order);
        imageView.setImageURI(orderItemListBean.getImg());//商品图片
    }
}
