package com.merrichat.net.activity.grouporder;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.SellOrderDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.RxTools.RxTool;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/20.
 * <p>
 * 待成团-待发货--已发货--已结束（不包括退款）订单详情
 */

public class SellOrderDetailAty extends MerriActionBarActivity {
    public static final int activityId = MiscUtil.getActivityId();
    private final String key = "sale";//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
    private final int orderFlag = 1;//0:买家 1:表示是卖家
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
     * 商品规格
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
     * 取消成团
     */
    @BindView(R.id.tv_contact_cancle)
    TextView tvContactCancle;
    /**
     * 联系买家
     */
    @BindView(R.id.tv_contact_buyer)
    TextView tvContactBuyer;
    /**
     * 同意成团
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
    @BindView(R.id.tv_address_address)
    TextView tvAddressAddress;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.ll_express_name)
    LinearLayout llExpressName;
    @BindView(R.id.tv_express_text)
    TextView tvExpressText;
    @BindView(R.id.tv_express_num)
    TextView tvExpressNum;
    @BindView(R.id.tv_copy_express_mem)
    TextView tvCopyExpressMem;
    @BindView(R.id.rl_express_num)
    RelativeLayout rlExpressNum;
    @BindView(R.id.tv_refund_time_start)
    TextView tvRefundTimeStart;
    @BindView(R.id.ll_refund_time_start)
    LinearLayout llRefundTimeStart;
    @BindView(R.id.rl_express_fee)
    RelativeLayout rlExpressFee;
    @BindView(R.id.tv_pay_totall_text)
    TextView tvPayTotallText;
    @BindView(R.id.tv_trade_way_text)
    TextView tvTradeWayText;
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    private int orderStatus = 0;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private String serialNumber;//    订单号
    private CommomDialog dialog;
    private String fragmentId;
    private SellOrderDetailModel.DataBean dataBeans;
    private String productType;// 0 实物 1 虚物
    private String hisMemberId;
    private String serialNumberPendingRegiment;//如果查的是拼团就传serialNumber参数,如果不是就传orderId参数

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

        serialNumber = getIntent().getStringExtra("serialNumber");
        fragmentId = getIntent().getStringExtra("fragmentId");
        hisMemberId = getIntent().getStringExtra("hisMemberId");

        if (RxDataTool.isNullString(serialNumber) || serialNumber.equals("0")) {
            RxToast.showToast("未获取订单信息，请重试!");
            return;
        }
        switch (fragmentId) {
            case "待成团":
                llDeliverTime.setVisibility(View.GONE);
                llSignTime.setVisibility(View.GONE);
                llRefundTime.setVisibility(View.GONE);
                tvContactCancle.setVisibility(View.VISIBLE);
                tvContactCancle.setText("取消成团");
                tvContactSeller.setText("同意成团");
                orderStatus = 0;
                break;
            case "待发货":
                llDeliverTime.setVisibility(View.GONE);
                llSignTime.setVisibility(View.GONE);
                llRefundTime.setVisibility(View.GONE);
                tvContactSeller.setText("确认发货");
                orderStatus = 2;
                break;
            case "已发货":
                llSignTime.setVisibility(View.GONE);
                llRefundTime.setVisibility(View.GONE);
                tvContactSeller.setText("查看物流");
                tvContactCancle.setVisibility(View.GONE);
                orderStatus = 3;

                break;
            case "已结束":
                llRefundTime.setVisibility(View.GONE);
                tvContactCancle.setVisibility(View.GONE);
                tvContactSeller.setText("查看物流");
                orderStatus = 4;
                break;
        }
        //查询销售订单
        queryOrder();

    }

    /**
     * 查询销售订单
     */
    private void queryOrder() {
        QueryOrderJsonModel queryOrderJsonModel = new QueryOrderJsonModel();
        if (RxDataTool.isNullString(hisMemberId)) {
            RxToast.showToast("获取详情失败，请重试！");
            return;
        }
        queryOrderJsonModel.setOrderStatus(orderStatus);
        queryOrderJsonModel.setKey(key);
        queryOrderJsonModel.setMemberId(hisMemberId);
        queryOrderJsonModel.setOrderId(serialNumber);
        final String jsonStr = JSON.toJSONString(queryOrderJsonModel);
        OkGo.<String>post(Urls.queryOrder)//
                .tag(this)//
                .params("jsObject", jsonStr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (!data.optBoolean("success")) {
                                        RxToast.showToast(data.optString("message"));
                                        return;
                                    }
                                    SellOrderDetailModel sellOrderDetailModel = JSON.parseObject(response.body(), SellOrderDetailModel.class);
                                    dataBeans = sellOrderDetailModel.getData();
                                    serialNumberPendingRegiment = dataBeans.getSerialNumber();//
                                    String orderId = dataBeans.getOrderId();
                                    SellOrderDetailModel.DataBean.OrderItemListBean orderItemListBean = dataBeans.getOrderItemList().get(0);
                                    tvProductPrice.setText(getResources().getString(R.string.money_character) +" "+ orderItemListBean.getSalesPrice());
                                    tvAddressName.setText("收货人：" + dataBeans.getAddresseeName());
                                    tvAddressPhone.setText(dataBeans.getAddresseePhone());
                                    tvAddressAddress.setText("收货地址：" + dataBeans.getAddresseeDetailed());
                                    productType = orderItemListBean.getProductType();// 0 实物 1 虚物
                                    if (!RxDataTool.isNullString(productType)) {
                                        if (productType.equals("1")) { //虚拟物品时的设置
                                            rlExpressFee.setVisibility(View.GONE);
                                            tvPayTotallText.setText("实付款");
                                            if (!RxDataTool.isNullString(fragmentId)) {
                                                if (fragmentId.equals("已发货") || fragmentId.equals("已结束")) {
                                                    tvContactSeller.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    }
                                    tvNickName.setText(dataBeans.getMemberName());
                                    switch (dataBeans.getTransactionType()) {//交易类型          1:个人，2:c2c群，3:b2c(微商)群
                                        case "1":
                                            tvTradeWay.setText("私信交易");
                                            tvTradeWay.setCompoundDrawables(null, null, null, null);
                                            tvTradeWayText.setText("交易方式");
                                            break;
                                        case "2":
                                            tvTradeWay.setText(dataBeans.getShopName());
                                            tvTradeWayText.setText("交易群");
                                            break;
                                        case "3":
                                            tvTradeWay.setText(dataBeans.getShopName());
                                            tvTradeWayText.setText("交易群");
                                            break;
                                        default:
                                            break;
                                    }
                                    simpleOrder.setImageURI(orderItemListBean.getImg());
                                    tvShopName.setText(orderItemListBean.getProductName());
                                    tvShopSku.setText(orderItemListBean.getProductPropertySpecValue());
                                    tvExpressFee.setText(getResources().getString(R.string.money_character) + " " + StringUtil.getPrice(dataBeans.getDeliveryFee()));
                                    tvProductNum.setText(orderItemListBean.getProductNum());
                                    tvProductFee.setText(StringUtil.getPrice(dataBeans.getTotalProductAmount()));
                                    tvPayTotall.setText(StringUtil.getPrice(dataBeans.getTotalAmount()));

                                    tvOrderNum.setText(orderId);
                                    switch (dataBeans.getPaymentType()) {//支付方式     1支付宝, 2微信支付 ,3余额支付
                                        case "1":
                                            tvPayWay.setText("支付宝");
                                            break;
                                        case "2":
                                            tvPayWay.setText("微信支付");

                                            break;
                                        case "3":
                                            tvPayWay.setText("余额支付");
                                            break;
                                    }
                                    tvPayTime.setText(RxTimeTool.getDate(dataBeans.getPaymentTime(), "yyyy-MM-dd HH:mm:ss"));
                                    tvDeliverTime.setText(RxTimeTool.getDate(dataBeans.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                                    tvSignTime.setText(RxTimeTool.getDate(dataBeans.getConfigReceiptTime(), "yyyy-MM-dd HH:mm:ss"));
                                    tvRefundTime.setText(RxTimeTool.getDate(dataBeans.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }

    @OnClick({R.id.tv_trade_way, R.id.tv_copy_order_mem, R.id.tv_contact_cancle, R.id.tv_contact_buyer, R.id.tv_contact_seller})
    public void onViewClicked(View view) {
        if (dialog == null) {
            dialog = new CommomDialog(cnt, R.style.dialog, "", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        dialog.dismiss();
                        switch (fragmentId) {
                            case "待成团":// 0取消，1同意
                                agreeRegiment(serialNumberPendingRegiment, 0);
                                break;
                            case "待发货"://取消订单
                                deleteOrder();
                                break;
                        }

                    }
                }
            });
        }
        switch (view.getId()) {
            case R.id.tv_trade_way:
                if (dataBeans != null && !dataBeans.getTransactionType().equals("1")) { //1:个人，2:c2c群，3:b2c(微商)群
                    isBannedOrTrade(dataBeans.getShopId(), dataBeans.getShopName(), dataBeans.getGroupUrl());
                }
                break;
            case R.id.tv_copy_order_mem://复制订单号
                RxTool.copyContent(tvOrderNum);
                break;
            case R.id.tv_contact_cancle:
                switch (fragmentId) {
                    case "待成团"://取消成团
                        dialog.setContent("您确定要取消成团吗？");
                        dialog.setTitle("取消成团");
                        dialog.show();
                        break;
                    case "待发货"://取消订单
                        dialog.setContent("您确定要取消订单吗？");
                        dialog.setTitle("取消订单");
                        dialog.show();
                        break;

                }

                break;
            case R.id.tv_contact_buyer://联系买家
                Intent intent1 = new Intent(this, SingleChatActivity.class);
                intent1.putExtra("receiverMemberId", dataBeans.getMemberId());
                intent1.putExtra("receiverHeadUrl", dataBeans.getMemberUrl());
                intent1.putExtra("receiverName", dataBeans.getMemberName());
                startActivity(intent1);
                break;
            case R.id.tv_contact_seller:
                switch (fragmentId) {
                    case "待成团"://同意成团
                        agreeRegiment(serialNumberPendingRegiment, 1);
                        break;
                    case "待发货"://确认发货
                        Bundle bundle = new Bundle();
                        bundle.putInt("activityId", activityId);
                        bundle.putSerializable("orderBeanInfo", dataBeans);
                        RxActivityTool.skipActivity(SellOrderDetailAty.this, ConfirmDeliveryGoodAty.class, bundle);
                        break;
                    case "已发货"://查看物流
                        switch (dataBeans.getSendType()) {
                            case "1":
                                Bundle bundle2 = new Bundle();
                                bundle2.putString("orderId", serialNumber);
                                RxActivityTool.skipActivity(SellOrderDetailAty.this, ChaKanWuLiuActivity.class, bundle2);
                                break;
                            case "2":
                                RxToast.showToast("此订单自取！");
                                break;
                            case "3":
                                RxToast.showToast("此订单送货上门！");
                                break;
                            default:
                                break;
                        }

                        break;
                    case "已结束"://查看物流
                        switch (dataBeans.getSendType()) {
                            case "1":
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("orderId", serialNumber);
                                RxActivityTool.skipActivity(SellOrderDetailAty.this, ChaKanWuLiuActivity.class, bundle1);
                                break;
                            case "2":
                                RxToast.showToast("此订单自取！");
                                break;
                            case "3":
                                RxToast.showToast("此订单送货上门！");
                                break;
                            default:
                                break;
                        }

                        break;
                }

                break;
        }
    }

    /**
     * 查询是不是群成员
     */
    private void isBannedOrTrade(final String groupId, final String groupName, final String groupOfImgUrl) {
        OkGo.<String>post(Urls.isBannedOrTrade)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("success")) {
                                    Intent intent = new Intent(cnt, GroupChattingAty.class);
                                    intent.putExtra("groupId", groupId);
                                    intent.putExtra("group", groupName);
                                    intent.putExtra("groupLogoUrl", groupOfImgUrl);
                                    startActivity(intent);
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 申请退款/退货/申请仲裁
     * 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     */
    private void deleteOrder() {
        OkGo.<String>post(Urls.deleteOrder)
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("orderId", serialNumber)
                .params("flag", orderFlag)
                .params("orderStatus", orderStatus)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (jsonObjectEx.optBoolean("success") && data != null) {
                                    if (data.optBoolean("success")) {
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_PENDING_DELIVERY = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        RxToast.showToast(data.optString("message"));
                                        finish();
                                    } else {
                                        RxToast.showToast(data.optString("message"));
                                    }

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);

                    }
                });

    }

    /**
     * 取消成团 同意成团
     *
     * @param serialNumber 拼团号
     * @param type         0取消，1同意
     */
    private void agreeRegiment(String serialNumber, final int type) {

        OkGo.<String>post(Urls.agreeRegiment)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("serialNumber", serialNumber)
                .params("type", type)//0取消，1同意
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.REFRESH_PENDING_REGIMENT = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                    if (type == 0) {
                                        RxToast.showToast("已取消成团！");

                                    } else if (type == 1) {
                                        RxToast.showToast("已成团！");
                                    }
                                    finish();

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);

                    }
                });

    }
}
