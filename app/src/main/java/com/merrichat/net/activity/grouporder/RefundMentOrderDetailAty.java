package com.merrichat.net.activity.grouporder;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.merrichat.net.adapter.ComplaintOthersAdapter;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/22.
 * <p>
 * 销售订单--退款详情
 */

public class RefundMentOrderDetailAty extends MerriActionBarActivity {
    public final static int activityId = MiscUtil.getActivityId();
    private final String key = "sale";//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
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
    @BindView(R.id.tv_shop_sku)
    TextView tvShopSku;
    @BindView(R.id.ll_product_info)
    LinearLayout llProductInfo;
    @BindView(R.id.tv_express_fee)
    TextView tvExpressFee;
    @BindView(R.id.tv_product_num)
    TextView tvProductNum;
    @BindView(R.id.tv_product_fee)
    TextView tvProductFee;
    @BindView(R.id.tv_pay_totall)
    TextView tvPayTotall;
    /**
     * 退款原因
     */
    @BindView(R.id.tv_refund_reason)
    TextView tvRefundReason;
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    @BindView(R.id.tv_order_text)
    TextView tvOrderText;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_copy_order_mem)
    TextView tvCopyOrderMem;
    @BindView(R.id.tv_pay_way)
    TextView tvPayWay;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;
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
    @BindView(R.id.tv_contact_cancle)
    TextView tvContactCancle;
    /**
     * 联系买家
     */
    @BindView(R.id.tv_contact_buyer)
    TextView tvContactBuyer;
    /**
     * 查看物流
     */
    @BindView(R.id.tv_contact_seller)
    TextView tvContactSeller;
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
    /**
     * 退款时间
     */
    @BindView(R.id.tv_refund_time_text)
    TextView tvRefundTimeText;
    /**
     * 退款图片证据
     */
    @BindView(R.id.ll_img_evidence)
    LinearLayout llImgEvidence;
    @BindView(R.id.rl_express_fee)
    RelativeLayout rlExpressFee;
    @BindView(R.id.tv_pay_totall_text)
    TextView tvPayTotallText;
    /**
     * 拒绝原因
     */
    @BindView(R.id.tv_refuse_reason)
    TextView tvRefuseReason;
    /**
     * 仲裁原因
     */
    @BindView(R.id.tv_arbitration_reason)
    TextView tvArbitrationReason;
    /**
     * 仲裁图片证据
     */
    @BindView(R.id.rl_arbitration_recyclerview)
    RecyclerView rlArbitrationRecyclerview;
    @BindView(R.id.ll_arbitration_img_evidence)
    LinearLayout llArbitrationImgEvidence;
    /**
     * 仲裁结果 原因
     */
    @BindView(R.id.tv_arbitration_result_reason)
    TextView tvArbitrationResultReason;
    @BindView(R.id.tv_trade_way_text)
    TextView tvTradeWayText;
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    private String orderId;
    private int orderStatus = 7;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private ComplaintOthersAdapter complaintOthersImgAdapter;
    private ArrayList<String> imgUrlList = new ArrayList<>();
    private SellOrderDetailModel.DataBean dataBeans;
    private CommomDialog dialog;
    private ComplaintOthersAdapter arbitrationImgAdapter;
    private ArrayList<String> arbitrationImgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refundment_orderdetail);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("订单详情");
        initView();
    }

    private void initView() {
        tvContactCancle.setText("拒绝退款");
        tvContactSeller.setText("确认退款");
        orderId = getIntent().getStringExtra("orderId");
        if (RxDataTool.isNullString(orderId) || orderId.equals("0")) {
            RxToast.showToast("未获取到订单信息，请重试！");
            return;
        }
        //退款图片证据
        initRefundRecyclerveiw();
        //仲裁图片证据
        initArbitrationRecyclerveiw();
        //查询销售订单
        queryOrder();


    }

    private void initArbitrationRecyclerveiw() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlArbitrationRecyclerview.setLayoutManager(layoutManager);
        arbitrationImgAdapter = new ComplaintOthersAdapter(R.layout.item_img_80, arbitrationImgList);
        rlArbitrationRecyclerview.setAdapter(arbitrationImgAdapter);
    }

    private void initRefundRecyclerveiw() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        complaintOthersImgAdapter = new ComplaintOthersAdapter(R.layout.item_img_80, imgUrlList);
        rlRecyclerview.setAdapter(complaintOthersImgAdapter);
    }

    @OnClick({R.id.tv_trade_way, R.id.ll_product_info, R.id.tv_copy_order_mem, R.id.tv_copy_express_mem, R.id.tv_contact_cancle, R.id.tv_contact_buyer, R.id.tv_contact_seller})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_trade_way:
                if (dataBeans != null && !dataBeans.getTransactionType().equals("1")) { //1:个人，2:c2c群，3:b2c(微商)群
                    isBannedOrTrade(dataBeans.getShopId(), dataBeans.getShopName(), dataBeans.getGroupUrl());
                }
                break;
            case R.id.ll_product_info:
                break;
            case R.id.tv_copy_order_mem://复制订单号
                RxTool.copyContent(tvOrderNum);
                break;
            case R.id.tv_copy_express_mem://复制运单号
                RxTool.copyContent(tvExpressNum);
                break;
            case R.id.tv_contact_cancle://拒绝退款
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("orderBeanInfo", dataBeans);
                bundle1.putInt("activityId", activityId);
                RxActivityTool.skipActivity(RefundMentOrderDetailAty.this, RefusingRefundmentAty.class, bundle1);
                break;
            case R.id.tv_contact_buyer://联系买家
                Intent intent1 = new Intent(this, SingleChatActivity.class);
                intent1.putExtra("receiverMemberId", dataBeans.getMemberId());
                intent1.putExtra("receiverHeadUrl", dataBeans.getMemberUrl());
                intent1.putExtra("receiverName", dataBeans.getMemberName());
                startActivity(intent1);
                break;
            case R.id.tv_contact_seller://确认退款
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要退款吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                confirmProduct(true, orderId);
                            }
                        }
                    }).setTitle("确认退款");
                    dialog.show();
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
     * 确认/拒绝退款
     *
     * @param flag    true:确认退款,false:取消退款
     * @param orderId 订单号
     */
    private void confirmProduct(final boolean flag, String orderId) {

        OkGo.<String>post(Urls.confirmProduct)//
                .tag(this)//
                .params("flag", flag)
                .params("orderId", orderId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_REFUND = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        RxToast.showToast("已退款成功！");
                                        finish();
                                    } else {
                                        RxToast.showToast("退款失败，请重试！");
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
     * 查询销售订单
     */
    private void queryOrder() {
        QueryOrderJsonModel queryOrderJsonModel = new QueryOrderJsonModel();
        String memberId = UserModel.getUserModel().getMemberId();
        if (RxDataTool.isNullString(memberId)) {
            RxToast.showToast("请登录后查看！");
            return;
        }
        queryOrderJsonModel.setOrderStatus(orderStatus);
        queryOrderJsonModel.setKey(key);
        queryOrderJsonModel.setMemberId(memberId);
        queryOrderJsonModel.setOrderId(orderId);
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
                                    JSONObject dataBean = jsonObjectEx.optJSONObject("data");
                                    if (dataBean != null && dataBean.optBoolean("success")) {
                                        SellOrderDetailModel sellOrderDetailModel = JSON.parseObject(response.body(), SellOrderDetailModel.class);
                                        dataBeans = sellOrderDetailModel.getData();
                                        SellOrderDetailModel.DataBean.OrderItemListBean orderItemListBean = dataBeans.getOrderItemList().get(0);
                                        tvAddressName.setText("收货人：" + dataBeans.getAddresseeName());
                                        tvAddressPhone.setText(dataBeans.getAddresseePhone());
                                        tvAddressAddress.setText("收货地址：" + dataBeans.getAddresseeDetailed());
                                        tvProductPrice.setText(getResources().getString(R.string.money_character) + " " + orderItemListBean.getSalesPrice());

                                        String productType = orderItemListBean.getProductType();// 0 实物 1 虚物
                                        if (!RxDataTool.isNullString(productType)) {
                                            if (!productType.equals("0")) {
                                                rlExpressFee.setVisibility(View.GONE);
                                                tvPayTotallText.setText("实付款");
                                            }
                                        }
                                        String returnReason = dataBeans.getReturnReason();
                                        String reasonText = dataBeans.getReasonText();
                                        String arbitrateCause = dataBeans.getArbitrateCause();//申请仲裁原因
                                        String arbitrateResult = dataBeans.getArbitrateResult();//申请仲裁的结论
                                        String reStatus = dataBeans.getReStatus();//0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
                                        switch (reStatus) {
                                            case "1":
                                                toShowRefundReason(returnReason);
                                                break;
                                            case "2":
                                                toShowRefundReason(returnReason);
                                                toShowRefuseReason(reasonText);
                                                break;
                                            case "4":
                                                toShowRefundReason(returnReason);
                                                toShowRefuseReason(reasonText);
                                                toShowArbitrationReason(arbitrateCause);
                                                break;
                                            case "5":
                                                toShowRefundReason(returnReason);
                                                toShowRefuseReason(reasonText);
                                                toShowArbitrationReason(arbitrateCause);
                                                tvArbitrationResultReason.setVisibility(View.VISIBLE);
                                                tvArbitrationResultReason.setText("拒绝仲裁原因        " + arbitrateResult);
                                                break;
                                            case "6":
                                                toShowRefundReason(returnReason);
                                                toShowRefuseReason(reasonText);
                                                toShowArbitrationReason(arbitrateCause);
                                                tvArbitrationResultReason.setVisibility(View.VISIBLE);
                                                tvArbitrationResultReason.setText("同意仲裁原因        " + arbitrateResult);
                                                break;
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
                                        tvOrderNum.setText(dataBeans.getOrderId());
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
                                        switch (reStatus) {//0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
                                            case "1":
                                                llRefundTimeStart.setVisibility(View.VISIBLE);
                                                llRefundTime.setVisibility(View.GONE);
                                                //发起退款时间
                                                tvRefundTimeStart.setText(RxTimeTool.getDate(dataBeans.getBeginReturnTime(), "yyyy-MM-dd HH:mm:ss"));
                                                break;
                                            case "2":
                                                llRefundTimeStart.setVisibility(View.VISIBLE);
                                                tvContactCancle.setVisibility(View.GONE);
                                                tvContactSeller.setVisibility(View.GONE);
                                                //发起退款时间
                                                tvRefundTimeStart.setText(RxTimeTool.getDate(dataBeans.getBeginReturnTime(), "yyyy-MM-dd HH:mm:ss"));
                                                //拒绝退款时间
                                                tvRefundTimeText.setText("拒绝时间");
                                                tvRefundTime.setText(RxTimeTool.getDate(dataBeans.getRefuseMoneyTime(), "yyyy-MM-dd HH:mm:ss"));
                                                break;
                                            case "3":
                                                llRefundTimeStart.setVisibility(View.VISIBLE);
                                                tvContactCancle.setVisibility(View.GONE);
                                                tvContactSeller.setVisibility(View.GONE);
                                                //发起退款时间
                                                tvRefundTimeStart.setText(RxTimeTool.getDate(dataBeans.getBeginReturnTime(), "yyyy-MM-dd HH:mm:ss"));
                                                //退款时间
                                                tvRefundTime.setText(RxTimeTool.getDate(dataBeans.getReturnMoneyTime(), "yyyy-MM-dd HH:mm:ss"));
                                                break;
                                            case "4":
                                                tvContactCancle.setVisibility(View.GONE);
                                                tvContactSeller.setVisibility(View.GONE);
                                                break;
                                            case "5":
                                                tvContactCancle.setVisibility(View.GONE);
                                                tvContactSeller.setVisibility(View.GONE);
                                                break;
                                            case "6":
                                                tvContactCancle.setVisibility(View.GONE);
                                                tvContactSeller.setVisibility(View.GONE);
                                                break;
                                        }
                                    } else {
                                        RxToast.showToast(dataBean.optString("message"));
                                    }

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }

                    private void toShowRefuseReason(String reasonText) {
                        tvRefuseReason.setVisibility(View.VISIBLE);
                        if (!RxDataTool.isNullString(reasonText)) {
                            tvRefuseReason.setText("拒绝原因        " + reasonText);
                        }
                    }

                    private void toShowRefundReason(String returnReason) {
                        tvRefundReason.setVisibility(View.VISIBLE);
                        llImgEvidence.setVisibility(View.VISIBLE);
                        tvRefundReason.setText("退款原因        " + returnReason);
                        List<String> returnUrls = dataBeans.getReturnUrls();
                        if (returnUrls != null && returnUrls.size() > 0) {
                            imgUrlList.addAll(returnUrls);
                            complaintOthersImgAdapter.notifyDataSetChanged();
                        } else {
                            llImgEvidence.setVisibility(View.GONE);
                        }
                    }

                    private void toShowArbitrationReason(String arbitrateCause) {
                        tvArbitrationReason.setVisibility(View.VISIBLE);
                        llArbitrationImgEvidence.setVisibility(View.VISIBLE);
                        tvArbitrationReason.setText("仲裁原因        " + arbitrateCause);
                        List<String> arbitrateUrls = dataBeans.getArbitrateUrls();//申请仲裁的图片
                        if (arbitrateUrls != null && arbitrateUrls.size() > 0) {
                            llArbitrationImgEvidence.setVisibility(View.VISIBLE);
                            arbitrationImgList.addAll(arbitrateUrls);
                            arbitrationImgAdapter.notifyDataSetChanged();
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
