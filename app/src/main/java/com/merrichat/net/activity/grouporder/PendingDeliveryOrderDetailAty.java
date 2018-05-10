package com.merrichat.net.activity.grouporder;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.utils.RxTools.RxActivityTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/22.
 * <p>
 * 销售订单--待发货详情
 */

public class PendingDeliveryOrderDetailAty extends MerriActionBarActivity {
    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    @BindView(R.id.tv_address_phone)
    TextView tvAddressPhone;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_trade_way)
    TextView tvTradeWay;
    @BindView(R.id.simple_order)
    SimpleDraweeView simpleOrder;
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    /**
     * 商品信息
     */
    @BindView(R.id.tv_shop_sku)
    TextView tvShopSku;
    @BindView(R.id.tv_express_fee)
    TextView tvExpressFee;
    @BindView(R.id.tv_product_num)
    TextView tvProductNum;
    @BindView(R.id.tv_product_fee)
    TextView tvProductFee;
    @BindView(R.id.tv_pay_totall)
    TextView tvPayTotall;
    @BindView(R.id.tv_order_text)
    TextView tvOrderText;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_pay_way)
    TextView tvPayWay;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;
    /**
     * 取消订单
     */
    @BindView(R.id.tv_contact_cancle)
    TextView tvContactCancle;
    /**
     * 联系买家
     */
    @BindView(R.id.tv_contact_buyer)
    TextView tvContactBuyer;
    /**
     * 确认发货
     */
    @BindView(R.id.tv_contact_seller)
    TextView tvContactSeller;
    @BindView(R.id.ll_product_info)
    LinearLayout llProductInfo;
    @BindView(R.id.tv_copy_order_mem)
    TextView tvCopyOrderMem;
    @BindView(R.id.tv_deliver_time)
    TextView tvDeliverTime;
    @BindView(R.id.ll_deliver_time)
    LinearLayout llDeliverTime;
    @BindView(R.id.tv_sign_time)
    TextView tvSignTime;
    @BindView(R.id.ll_sign_time)
    LinearLayout llSignTime;
    @BindView(R.id.tv_refund_time)
    TextView tvRefundTime;
    @BindView(R.id.ll_refund_time)
    LinearLayout llRefundTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellorder_detail);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("订单详情");
        initView();
    }

    private void initView() {

        llDeliverTime.setVisibility(View.GONE);
        llSignTime.setVisibility(View.GONE);
        llRefundTime.setVisibility(View.GONE);
        tvContactSeller.setText("确认发货");
    }

    @OnClick({R.id.tv_trade_way, R.id.tv_contact_cancle, R.id.tv_contact_buyer, R.id.tv_contact_seller})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_trade_way:
                break;
            case R.id.tv_contact_cancle:
                break;
            case R.id.tv_contact_buyer:
                break;
            case R.id.tv_contact_seller:
                RxActivityTool.skipActivity(this, ConfirmDeliveryGoodAty.class);
                break;
        }
    }
}
