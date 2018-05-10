package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.grouporder.ChaKanWuLiuActivity;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.ComplaintOthersAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.SellOrderDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.RxTools.RxTool;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/23.
 * 群订单管理--订单详情
 */

public class GroupOrderDetailActivity extends MerriActionBarActivity {
    /**
     * title提示1
     */
    @BindView(R.id.tv_group_buy_info)
    TextView tvGroupBuyInfo;

    /**
     * title提示2
     */
    @BindView(R.id.tv_group_buy_state)
    TextView tvGroupBuyState;

    /**
     * 收货人
     */
    @BindView(R.id.receiving_name)
    TextView receivingName;

    /**
     * 收货人电话
     */
    @BindView(R.id.receiving_phone_nu)
    TextView receivingPhoneNu;

    /**
     * 收货人地址
     */
    @BindView(R.id.receiving_address)
    TextView receivingAddress;

    /**
     * 退款原因
     */
    @BindView(R.id.rl_refund_reson)
    RelativeLayout rlRefundReson;

    @BindView(R.id.tv_refund_reson)
    TextView tvRefundReson;


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
     * 运费
     */

    @BindView(R.id.rl_freight)
    RelativeLayout rlFreight;

    @BindView(R.id.receiving_price)
    TextView receivingPrice;

    /**
     * 商品数量
     */
    @BindView(R.id.goods_count)
    TextView goodsCount;

    /**
     * 商品总价
     */
    @BindView(R.id.goods_all_price)
    TextView goodsAllPrice;

    /**
     * 实付款（含运费）
     */
    @BindView(R.id.real_pay_price)
    TextView realPayPrice;

    @BindView(R.id.goods_real_cost)
    TextView goodsRealCost;


    /**
     * 卖家昵称
     */
    @BindView(R.id.seller_name)
    TextView sellerName;

    /**
     * 买家昵称
     */
    @BindView(R.id.tv_buyer_nickname)
    TextView tvBuyerNickname;

    /**
     * 订单编号
     */
    @BindView(R.id.seller_order_name)
    TextView sellerOrderName;

    /**
     * 复制按钮
     */
    @BindView(R.id.copy_order)
    TextView copyOrder;

    /**
     * 支付方式
     */
    @BindView(R.id.pay_model)
    TextView payModel;

    /**
     * 付款时间
     */
    @BindView(R.id.pay_time)
    TextView payTime;

    /**
     * 发货时间
     */
    @BindView(R.id.ship_time)
    TextView shipTime;

    /**
     * 签收时间
     */
    @BindView(R.id.sign_time)
    TextView signTime;

    /**
     * 发起退款
     */
    @BindView(R.id.tv_initiate_refund)
    TextView tvInitiateRefund;

    /**
     * 发起仲裁
     */
    @BindView(R.id.tv_initiate_arbitration)
    TextView tvInitiateArbitration;

    /**
     * 底部按钮1
     * 左下角
     */
    @BindView(R.id.tv_bottom_detail_1)
    TextView tvBottomDetail1;

    /**
     * 底部按钮2
     * 右下1
     */
    @BindView(R.id.tv_bottom_detail_2)
    TextView tvBottomDetail2;


    /**
     * 底部按钮3
     * 右下2
     * 联系卖家
     */
    @BindView(R.id.tv_bottom_detail_3)
    TextView tvBottomDetail3;


    /**
     * 仲裁图片证据
     */
    @BindView(R.id.rl_arbitration_recyclerview)
    RecyclerView rlArbitrationRecyclerview;
    @BindView(R.id.ll_arbitration_img_evidence)
    LinearLayout llArbitrationImgEvidence;

    /**
     * 订单id
     */
    private String orderId;


    /**
     * 来源
     * 1待成团
     * 2 待发货
     * 3 已发货
     * 4 已结束
     * 5 仲裁退款
     */
    private int source;


    /**
     * 订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
     */
    private int orderStatus;

    /**
     * {sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
     */
    private final String key = "sale";

    /**
     * orderId参数
     */
    private String serialNumber = "";

    private SellOrderDetailModel.DataBean dataBeans;


    private String groupId = "";


    private ComplaintOthersAdapter arbitrationImgAdapter;
    private ArrayList<String> arbitrationImgList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_order_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setLeftBack();
        setTitle("订单详情");
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.FINISH_GROUP_ORDER_DETAILS) {//关闭页面
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_GROUP_ORDER_LIST = true;
            EventBus.getDefault().post(eventBusModel);
            finish();
        }
    }


    private void initView() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        source = intent.getIntExtra("source", 0);
        serialNumber = getIntent().getStringExtra("serialNumber");
        initArbitrationRecyclerveiw();
        initClick();
        queryOrder();
    }


    private void initArbitrationRecyclerveiw() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlArbitrationRecyclerview.setLayoutManager(layoutManager);
        arbitrationImgAdapter = new ComplaintOthersAdapter(R.layout.item_img_80, arbitrationImgList);
        rlArbitrationRecyclerview.setAdapter(arbitrationImgAdapter);
    }


    private void initClick() {
        switch (source) {
            case 1:

                tvGroupBuyInfo.setText("拼团中，还差人拼团成功");
                tvGroupBuyState.setText("还剩自动取消成团");

                tvBottomDetail2.setVisibility(View.GONE);
                orderStatus = 0;
                break;
            case 2:
                tvGroupBuyInfo.setText("已付款");
                tvGroupBuyState.setText("等待卖家发货");

                tvBottomDetail1.setVisibility(View.VISIBLE);
                tvBottomDetail1.setBackgroundResource(R.drawable.shape_contact_buyer);
                tvBottomDetail1.setTextColor(getResources().getColorStateList(R.color.base_093FFF));
                tvBottomDetail1.setText("联系买家");
                tvBottomDetail2.setVisibility(View.GONE);
                orderStatus = 2;
                break;
            case 3:
                tvGroupBuyInfo.setText("卖家已发货");
                orderStatus = 3;
                shipTime.setVisibility(View.VISIBLE);
                break;
            case 4:
                tvGroupBuyInfo.setText("买家已收货");
                tvGroupBuyState.setText("订单完成");
                orderStatus = 4;
                shipTime.setVisibility(View.VISIBLE);
                signTime.setVisibility(View.VISIBLE);
                break;
            case 5:
                tvGroupBuyInfo.setText("买家发起仲裁申请");
                tvGroupBuyState.setText("等待仲裁");
                rlRefundReson.setVisibility(View.VISIBLE);
                llArbitrationImgEvidence.setVisibility(View.VISIBLE);
                orderStatus = 7;
                shipTime.setVisibility(View.VISIBLE);
                signTime.setVisibility(View.VISIBLE);
                tvInitiateRefund.setVisibility(View.VISIBLE);
                tvInitiateArbitration.setVisibility(View.VISIBLE);
                break;
        }
    }


    @OnClick({R.id.tv_bottom_detail_1, R.id.tv_bottom_detail_2, R.id.tv_bottom_detail_3, R.id.copy_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_bottom_detail_1:
                switch (source) {
                    case 1:

                        break;
                    case 2:
                        Intent intent = new Intent(cnt, SingleChatActivity.class);
                        intent.putExtra("receiverMemberId", dataBeans.getMemberId());
                        intent.putExtra("receiverName", dataBeans.getMemberName());
                        intent.putExtra("receiverHeadUrl", dataBeans.getMemberUrl());
                        break;
                    case 3:
                    case 4:
                        if ("1".equals(dataBeans.getSendType())) {
                            startActivity(new Intent(cnt, ChaKanWuLiuActivity.class).putExtra("orderId", dataBeans.getOrderId()));
                        } else if ("2".equals(dataBeans.getSendType())) {
                            GetToast.useString(cnt, "此订单自取");
                        } else if ("3".equals(dataBeans.getSendType())) {
                            GetToast.useString(cnt, "此订单送货上门");
                        }

                        break;
                    case 5:
                        SellOrderDetailModel.DataBean.OrderItemListBean orderItemListBean = dataBeans.getOrderItemList().get(0);
                        Intent intent1 = new Intent(cnt, ConfirmArbitrationActivity.class);
                        intent1.putExtra("groupId", groupId);
                        intent1.putExtra("productName", orderItemListBean.getProductName());
                        intent1.putExtra("productPropertySpecValue", orderItemListBean.getProductPropertySpecValue());
                        intent1.putExtra("deliveryFee", dataBeans.getDeliveryFee());
                        intent1.putExtra("productNum", orderItemListBean.getProductNum());
                        intent1.putExtra("totalProductAmount", dataBeans.getTotalProductAmount());
                        intent1.putExtra("img", orderItemListBean.getImg());
                        intent1.putExtra("orderId", dataBeans.getOrderId());
                        startActivity(intent1);
                        break;
                }
                break;

            case R.id.tv_bottom_detail_2:
                switch (source) {
                    case 1:
                    case 2:

                        break;
                    case 3:
                    case 4:
                    case 5:
                        Intent intent = new Intent(cnt, SingleChatActivity.class);
                        intent.putExtra("receiverMemberId", dataBeans.getMemberId());
                        intent.putExtra("receiverName", dataBeans.getMemberName());
                        intent.putExtra("receiverHeadUrl", dataBeans.getMemberUrl());
                        startActivity(intent);
                        break;
                }

                break;
            case R.id.tv_bottom_detail_3:
                Intent intent = new Intent(cnt, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", dataBeans.getShopMemberId());
                intent.putExtra("receiverName", dataBeans.getShopMemberName());
                intent.putExtra("receiverHeadUrl", "");
                startActivity(intent);
                break;
            case R.id.copy_order://复制
                RxTool.copyContent(sellerOrderName);
                break;
        }
    }


    /**
     * 查询销售订单
     */
    private void queryOrder() {
        QueryOrderJsonModel queryOrderJsonModel = new QueryOrderJsonModel();
        queryOrderJsonModel.setOrderStatus(orderStatus);
        queryOrderJsonModel.setKey(key);
        queryOrderJsonModel.setMemberId(UserModel.getUserModel().getMemberId());
        queryOrderJsonModel.setOrderId(serialNumber);
        final String jsonStr = JSON.toJSONString(queryOrderJsonModel);
        OkGo.<String>post(Urls.queryOrder)
                .tag(this)//
                .params("jsObject", jsonStr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.optBoolean("success")) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        SellOrderDetailModel sellOrderDetailModel = JSON.parseObject(response.body(), SellOrderDetailModel.class);
                                        dataBeans = sellOrderDetailModel.getData();
                                        String orderId = dataBeans.getOrderId();
                                        SellOrderDetailModel.DataBean.OrderItemListBean orderItemListBean = dataBeans.getOrderItemList().get(0);
                                        tvBuyerNickname.setText("买家昵称：" + dataBeans.getMemberName());
                                        receivingName.setText("收货人：" + dataBeans.getAddresseeName());
                                        receivingPhoneNu.setText(dataBeans.getAddresseePhone());
                                        receivingAddress.setText("收货地址：" + dataBeans.getAddresseeDetailed());
                                        sellerName.setText("卖家昵称  " + dataBeans.getShopMemberName());


                                        Glide.with(cnt).load(orderItemListBean.getImg()).into(ivSellContentUrl);
                                        tvSellContentTitle.setText(orderItemListBean.getProductName());
                                        tvSellContentDiscripe.setText(orderItemListBean.getProductPropertySpecValue());

                                        goodsCount.setText(orderItemListBean.getProductNum());
                                        goodsAllPrice.setText(getResources().getString(R.string.money_character) + StringUtil.getPrice(dataBeans.getTotalProductAmount()));
                                        realPayPrice.setText(getResources().getString(R.string.money_character) + StringUtil.getPrice(dataBeans.getTotalAmount()));

                                        sellerOrderName.setText("订单编号  " + orderId);
                                        // 0实物 1虚物   虚拟物品不显示运费
                                        if ("0".equals(orderItemListBean.getProductType())) {
                                            rlFreight.setVisibility(View.VISIBLE);
                                            receivingPrice.setText(getResources().getString(R.string.money_character) + " " + StringUtil.getPrice(dataBeans.getDeliveryFee()));
                                            goodsRealCost.setText("实付款(含运费)");
                                        } else {
                                            goodsRealCost.setText("实付款");
                                        }
                                        switch (dataBeans.getPaymentType()) {//支付方式     1支付宝, 2微信支付 ,3余额支付
                                            case "1":
                                                payModel.setText("支付方式  支付宝");
                                                break;
                                            case "2":
                                                payModel.setText("支付方式  微信支付");

                                                break;
                                            case "3":
                                                payModel.setText("支付方式  余额支付");
                                                break;
                                        }
                                        payTime.setText("付款时间  " + RxTimeTool.getDate(dataBeans.getPaymentTime(), "yyyy-MM-dd HH:mm:ss"));


                                        switch (source) {
                                            case 1:
                                                JSONArray serialMember = data.optJSONArray("serialMember");
                                                int pinTuanNum = Integer.parseInt(orderItemListBean.getSalesMemberSum()) - serialMember.length();
                                                tvGroupBuyInfo.setText("拼团中，还差" + pinTuanNum + "人拼团成功");
                                                String remainTime = DateTimeUtil.formatTime(Long.parseLong(dataBeans.getRemainTime()));
                                                tvGroupBuyState.setText("还剩" + remainTime + "自动取消成团");
                                                break;

                                            case 3:
                                                String autoReceiptTime = data.optString("autoReceiptTime");
                                                tvGroupBuyState.setText("还剩" + autoReceiptTime + "天自动确认收货");
                                                shipTime.setText("发货时间  " + RxTimeTool.getDate(dataBeans.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                                                break;

                                            case 4:
                                                shipTime.setText("发货时间  " + RxTimeTool.getDate(dataBeans.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                                                signTime.setText("签收时间  " + RxTimeTool.getDate(dataBeans.getConfigReceiptTime(), "yyyy-MM-dd HH:mm:ss"));
                                                break;

                                            case 5:
                                                shipTime.setText("发货时间  " + RxTimeTool.getDate(dataBeans.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                                                signTime.setText("签收时间  " + RxTimeTool.getDate(dataBeans.getConfigReceiptTime(), "yyyy-MM-dd HH:mm:ss"));
                                                tvInitiateRefund.setText("发起退款 " + RxTimeTool.getDate(dataBeans.getBeginReturnTime(), "yyyy-MM-dd HH:mm:ss"));
                                                tvInitiateArbitration.setText("发起仲裁 " + RxTimeTool.getDate(dataBeans.getArbitrateTime(), "yyyy-MM-dd HH:mm:ss"));
                                                tvRefundReson.setText(dataBeans.getReturnReason());
                                                toShowArbitrationReason();
                                                //交易类型 1:个人，2:c2c群，3:b2c(微商)群  只有c2c才能由群主/管理员仲裁
                                                if ("2".equals(dataBeans.getTransactionType())) {
//                                                    true:表示超过了24小时,false:表示在24小时内
                                                    boolean arbitrate_due = data.optBoolean("arbitrate_due");
                                                    if (!arbitrate_due) {
                                                        tvBottomDetail1.setVisibility(View.VISIBLE);
                                                        tvBottomDetail1.setText("仲裁退款");
                                                    }
                                                }
                                                break;
                                        }
                                    } else {
                                        String message = data.optString("message");
                                        if (!TextUtils.isEmpty(message)) {
                                            GetToast.useString(cnt, message);
                                        }
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

    private void toShowArbitrationReason() {
        List<String> arbitrateUrls = dataBeans.getArbitrateUrls();//申请仲裁的图片
        if (arbitrateUrls != null && arbitrateUrls.size() > 0) {
            llArbitrationImgEvidence.setVisibility(View.VISIBLE);
            arbitrationImgList.addAll(arbitrateUrls);
            arbitrationImgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
