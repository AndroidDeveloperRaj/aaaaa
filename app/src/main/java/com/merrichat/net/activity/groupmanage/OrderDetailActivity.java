package com.merrichat.net.activity.groupmanage;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmarket.PinTuanShareActivity;
import com.merrichat.net.activity.grouporder.ChaKanWuLiuActivity;
import com.merrichat.net.activity.meiyu.NewMeetNiceActivity;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.DeleteOrderModel;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.OrderSignByOrderIdModel;
import com.merrichat.net.model.QueryOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WaiteGroupBuyDetailModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.TimeTools;
import com.merrichat.net.view.CommomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangweiwei on 2018/1/20.
 */

public class OrderDetailActivity extends MerriActionBarActivity {
    /**
     * 当页要显示的model
     * 0待成团订单详情
     * 1待发货订单详情
     * 2待收货订单详情
     * 3已结束订单详情
     * 4退款
     */
    private int currentPosition;
    private String orderId;

    private RelativeLayout mBuyType;    //交易方式
    private TextView mGroupBuyInfo;     //团购状态  title 1  标题1
    private TextView mGroupbuyTime;     //状态    tittle 2

    private TextView mBottomDetail1;    //底部按钮1
    private TextView mBottomDetail2;    //底部按钮2
    private TextView mBottomDetail3;    //底部按钮3

    private RelativeLayout mRlReasonText;  //拒绝退款
    private TextView mReasonText;      //拒绝退款原因

    private RelativeLayout mRefundReason;  //退款原因
    private RelativeLayout mPhotoList;      //图片列表

    private TextView mReceivingName;   //收货人名称
    private TextView mReceivingPhoneNu;  //收货人手机号
    private TextView mReceivingAddress;  //收货人地址

    private TextView mSellerName;    //卖家名称
    private TextView mSellContentTitle;  //商品名称
    private TextView mSellContentDiscripe;  //商品描述
    private ImageView mSellContentUrl;  //图片
    private TextView mTransType;   //交易方式

    private TextView mSinglePrice;  //单价
    private TextView mReceivingPrice;  //运费
    private TextView mGoodsCount;  //商品数量
    private TextView mGoodsAllPrice;  // 商品总价
    private TextView mRealPayPrice;  //实际支付

    private TextView mSellerName_;  //卖家名称
    private TextView mSellerOrderName;  //订单号
    private TextView mPayModel;  //支付方式
    private TextView mPayTime;  //付款时间
    private TextView mSignTime;  //签收时间
    private TextView mCopyOrder; //复制订单

    private TextView mDeliverTime; // 发货时间
    private TextView mReciveTime; //tv_receive_time
    private List<String> returnUrls;  //图片列表
    private LayoutInflater mInflater;
    private TextView mReturnReason;  //卖家申请退款原因
    private TextView mTransTypeLabel;

    private LinearLayout mGallery;//选择证据布局


    private TextView mTransTypeRightIc;
    private Thread threadStarAnim;  //邀请好友倒计时线程


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setLeftBack();
        setTitle("订单详情");
        Intent intent = getIntent();
        mInflater = LayoutInflater.from(this);
        currentPosition = intent.getIntExtra("current_position", -1);
        orderId = intent.getStringExtra("orderId");
        initView();

    }


    private void initView() {
        mBuyType = (RelativeLayout) findViewById(R.id.rl_buy_type);  //交易方式布局
        mGroupBuyInfo = (TextView) findViewById(R.id.tv_group_buy_info);  //标题1
        mGroupbuyTime = (TextView) findViewById(R.id.tv_group_buy_time);  //标题2
        mRlReasonText = (RelativeLayout) findViewById(R.id.rl_reason_text);
        mReasonText = (TextView) findViewById(R.id.tv_reason_text);

        mBottomDetail1 = (TextView) findViewById(R.id.tv_bottom_detail_1);
        mBottomDetail2 = (TextView) findViewById(R.id.tv_bottom_detail_2);
        mBottomDetail3 = (TextView) findViewById(R.id.tv_bottom_detail_3);
        mRefundReason = (RelativeLayout) findViewById(R.id.rl_refund_reson);
        mPhotoList = (RelativeLayout) findViewById(R.id.rl_photo_list);

        mReceivingName = (TextView) findViewById(R.id.receiving_name);
        mReceivingPhoneNu = (TextView) findViewById(R.id.receiving_phone_nu);
        mReceivingAddress = (TextView) findViewById(R.id.receiving_address);

        mSellerName = (TextView) findViewById(R.id.tv_seller_name);
        mSellContentTitle = (TextView) findViewById(R.id.tv_sell_content_title);
        mSellContentDiscripe = (TextView) findViewById(R.id.tv_sell_content_discripe);
        mSellContentUrl = (ImageView) findViewById(R.id.iv_sell_content_url);
        mTransType = (TextView) findViewById(R.id.tv_trans_type);
        mTransTypeRightIc = (TextView) findViewById(R.id.textView);
        mTransTypeLabel = (TextView) findViewById(R.id.tv_trans_type_label);

        mSinglePrice = (TextView) findViewById(R.id.single_price);
        mReceivingPrice = (TextView) findViewById(R.id.receiving_price);
        mGoodsCount = (TextView) findViewById(R.id.goods_count);
        mGoodsAllPrice = (TextView) findViewById(R.id.goods_all_price);
        mRealPayPrice = (TextView) findViewById(R.id.real_pay_price);

        mSellerName_ = (TextView) findViewById(R.id.seller_name);
        mSellerOrderName = (TextView) findViewById(R.id.seller_order_name);
        mPayModel = (TextView) findViewById(R.id.pay_model);
        mPayTime = (TextView) findViewById(R.id.pay_time);
        mSignTime = (TextView)findViewById(R.id.tv_sign_time);
        mCopyOrder = (TextView) findViewById(R.id.copy_order);
        mDeliverTime = (TextView) findViewById(R.id.deliver_time);
        mReciveTime = (TextView) findViewById(R.id.tv_receive_time);

        mReturnReason = (TextView) findViewById(R.id.tv_return_reason);
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);

        getOrderData(orderId);
        initClick();


    }


    /**
     * 订单详情
     *
     * @param model
     */
    public void getOrderData(WaiteGroupBuyDetailModel model) {
        if (model.data == null || model.data.orderItemList == null) {
            return;
        }
        mReceivingName.setText("收货人：" + model.data.addresseeName);
        mReceivingPhoneNu.setText(model.data.addresseePhone);
        mReceivingAddress.setText("收货地址：" + model.data.addresseeDetailed);
        mSellerName.setText("卖家昵称：" + model.data.shopMemberName);

        mSellContentTitle.setText(model.data.orderItemList.get(0).productName);
        mSellContentDiscripe.setText(model.data.orderItemList.get(0).productPropertySpecValue);
        Glide.with(this).load(model.data.orderItemList.get(0).img).centerCrop().override(200, 200).into(mSellContentUrl);
        mTransType.setText("");

        setTransType(model.data.transactionType);
        mSinglePrice.setText("￥" + model.data.orderItemList.get(0).salesPrice);
        mReceivingPrice.setText("￥" + model.data.deliveryFee);
        mGoodsCount.setText("" + model.data.orderItemList.get(0).productNum);
        mGoodsAllPrice.setText("￥" + model.data.orderItemList.get(0).productAmount);
        mRealPayPrice.setText("￥" +model.data.totalAmount);

        mSellerName_.setText("卖家昵称 " + model.data.shopMemberName);
        mSellerOrderName.setText("订单编号 " + model.data.orderId);


        if (model.data.paymentType != null) {
            switch (model.data.paymentType) {
                case "1":
                    mPayModel.setText("支付方式 支付宝");
                    break;
                case "2":
                    mPayModel.setText("支付方式 微信");
                    break;
                case "3":
                    mPayModel.setText("支付方式 余额");
                    break;
                default:
                    mPayModel.setText("");
                    break;
            }
        }
        //付款时间
        if (model.data.paymentTime > 0) {
            setPayTime(model.data.paymentTime);
        }

        //发货时间显示
        if (model.data.sendTime > 0) {
            setDeliverTime(model.data.sendTime);
        }

        //收货时间
        if (model.data.configReceiptTime > 0) {
            configReceiptTime(model.data.configReceiptTime);
        }


        if (model.data.returnUrls != null && model.data.returnUrls.size() > 0) {
            for (int i = 0; i < model.data.returnUrls.size(); i++) {  //下方选择照片
                View view = mInflater.inflate(R.layout.item_image, mGallery, false);
                SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.item_img);
                Glide.with(this).load(model.data.returnUrls.get(i)).centerCrop().override(200, 200).error(R.mipmap.ic_preloading).into(img);
                final int posion = i;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                mGallery.addView(view);
            }
        }

        if (currentPosition > 3) {
            mGroupbuyTime.setText(detailModel.data.returnReason);
        }


        switch (currentPosition) {  //判断当前页面的状态
            case 0:  //当前页面是待成团
                mBottomDetail2.setVisibility(View.GONE);
                if (detailModel.data.remainTime > 0) {
                    threadStarAnim = new Thread(new ThreadShow()); //倒计时线程
                    threadStarAnim.start();
                } else {
                    mBottomDetail3.setText("邀请好友 " + TimeTools.getCountTimeByLong((detailModel.data.remainTime)));
                }
                mGroupBuyInfo.setText("拼团中，还差" + (model.data.orderItemList.get(0).salesMemberSum - model.data.serialMember.size()) + "人拼团成功");

                break;
            case 1:
                mGroupBuyInfo.setText("已付款");
                mGroupbuyTime.setText("等待卖家发货");
                mBottomDetail3.setText("取消订单");
                mBottomDetail1.setVisibility(View.GONE);
                mBuyType.setVisibility(View.VISIBLE);

                break;
            case 2:
                mBottomDetail1.setBackgroundResource(R.drawable.shape_contact_cancle);
                mBottomDetail1.setTextColor(getResources().getColorStateList(R.color.base_888888));
                mBottomDetail1.setText("查看物流");
                mBottomDetail3.setText("确认收货");
                mBuyType.setVisibility(View.VISIBLE);
                mGroupBuyInfo.setText("已发货");

                final long l = 3600l * 24 * 1000 * 15;
                final long l2 = System.currentTimeMillis() - detailModel.data.paymentTime;
                System.out.println((l - l2) / (3600l * 24 * 1000));

                mGroupbuyTime.setText("等待收货，自动收货时间" + ((l - l2) / (3600l * 24 * 1000)) + "天" + ((l - l2) % (3600l * 24 * 1000)) / (3600_000) + "小时");

                break;
            case 3:
                mBottomDetail1.setVisibility(View.GONE);
                mBuyType.setVisibility(View.VISIBLE);
                mGroupBuyInfo.setText("买家已收货");
                mGroupbuyTime.setText("订单完成");
                mBottomDetail3.setText("申请退款");
                if (detailModel.data.iquidateStatus == 1){
                    mBottomDetail3.setVisibility(View.GONE);
                }else {
                    mBottomDetail3.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                mBottomDetail1.setVisibility(View.GONE);
                mBuyType.setVisibility(View.VISIBLE);
                mReturnReason.setText("已发起退款");
                mBottomDetail3.setText("申请仲裁");
                mRefundReason.setVisibility(View.VISIBLE);
                mPhotoList.setVisibility(View.VISIBLE);
                mGroupBuyInfo.setText(detailModel.data.reStatusText); //  标题1
                mGroupbuyTime.setText(detailModel.data.reStatusText);  //标题2
                mReturnReason.setText(detailModel.data.returnReason); //退款原因

                if (detailModel.data.remainTime !=0){
                    setSignTime(detailModel.data.remainTime);
                }

                if (detailModel.data.reStatus == 3 || detailModel.data.reStatus == 4 || detailModel.data.reStatus == 5 || detailModel.data.reStatus == 6 || detailModel.data.iquidateStatus == 1) {
                    mBottomDetail3.setVisibility(View.GONE);
                    if (detailModel.data.reStatus == 4) {
                        mGroupbuyTime.setText("等待仲裁");
                    } else if (detailModel.data.reStatus == 5) {
                        mGroupbuyTime.setText("申请仲裁失败");
                    } else if (detailModel.data.reStatus == 6) {
                        mGroupbuyTime.setText("订单仲裁已完成");
                    } else if (detailModel.data.reStatus == 3) {
                        mGroupbuyTime.setText("卖家同意退款");
                    }
                } else {
                    mBottomDetail3.setVisibility(View.VISIBLE);
                    if (detailModel.data.reStatus == 1) {
                        mGroupbuyTime.setText("等待退款中");
                    } else if (detailModel.data.reStatus == 2) {
                        mGroupbuyTime.setText("退款失败");
                        mBottomDetail3.setText("申请退款");
                        mRlReasonText.setVisibility(View.VISIBLE);
                        mReasonText.setText(detailModel.data.reasonText);
                    }
                }
                break;
        }


    }

    /**
     * 显示付款时间
     *
     * @param payTime
     */
    private void setPayTime(final long payTime) {
        mPayTime.setVisibility(View.VISIBLE);
        mPayTime.setText("付款时间 " + DateTimeUtil.format0(new Date(payTime)));
    }


    /**
     * 发货时间
     *
     * @param deliverTime 发货时间戳
     */
    private void setDeliverTime(final long deliverTime) {
        if (currentPosition > 1) {
            mDeliverTime.setVisibility(View.VISIBLE);
            mDeliverTime.setText("发货时间 " + DateTimeUtil.format0(new Date(deliverTime)));
        }
    }


    /**
     * 确认收货时间
     *
     * @param configReceiptTime
     */
    private void configReceiptTime(final long configReceiptTime) {
        if (currentPosition > 2) {
            mReciveTime.setVisibility(View.VISIBLE);
            mReciveTime.setText("签收时间 " + DateTimeUtil.format0(new Date(configReceiptTime)));
        }
    }


    /**
     * 发起时间
     *
     * @param deliverTime 发起时间
     */
    private void setSignTime(final long deliverTime) {
        if (currentPosition > 1) {
            mSignTime.setVisibility(View.VISIBLE);
            mSignTime.setText("发起时间 " + DateTimeUtil.format0(new Date(deliverTime)));
        }
    }

    /**
     * 交易方式
     *
     * @param transType 1:个人，2:c2c群，3:b2c(微商)群
     */
    private void setTransType(final int transType) {
        mTransType.setVisibility(View.VISIBLE);
        switch (transType) {
            case 1:
                mTransTypeLabel.setText("交易方式");
                mTransType.setText("私信交易");
                mTransTypeRightIc.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTransTypeLabel.setText("交易群");
                mTransTypeRightIc.setVisibility(View.VISIBLE);
                mTransType.setText("c2c群");
                if (detailModel.data.shopName != null) {
                    mTransType.setText(detailModel.data.shopName);
                }
                break;
            case 3:
                mTransTypeLabel.setText("交易群");
                mTransTypeRightIc.setVisibility(View.VISIBLE);
                mTransType.setText("b2c(微商)群交易");
                if (detailModel.data.shopName != null) {
                    mTransType.setText(detailModel.data.shopName);
                }
                break;
            default:
                mTransType.setText("");
                break;
        }

        mTransType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transType == 3 || transType == 2) {
                    isBannedOrTrade(detailModel.data.shopId, detailModel.data.shopName, detailModel.data.shopUrl);
                }
            }
        });
    }

    /**
     * 查询是不是群成员
     */
    private void isBannedOrTrade(final String groupId, final String groupName, final String groupOfImgUrl) {
        OkGo.<String>post(Urls.isBannedOrTrade)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(cnt) {
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

    private WaiteGroupBuyDetailModel detailModel = new WaiteGroupBuyDetailModel();

    /**
     * 根据  orderId 加载订单详情的具体信息
     *
     * @param orderId
     */
    public void getOrderData(String orderId) {
        if (null == orderId || orderId.length() == 0) {
            return;
        }
        QueryOrderModel.QueryOrderRequestParams requestParams = new QueryOrderModel.QueryOrderRequestParams();
        requestParams.key = "buy";

        switch (currentPosition) {
            case 0:
                requestParams.orderStatus = "" + 0;
                requestParams.orderId = orderId;
                break;
            case 1:
                requestParams.orderStatus = "" + 2;
                requestParams.orderId = orderId;
                break;
            case 2:
                requestParams.orderStatus = "" + 3;
                requestParams.orderId = orderId;
                break;
            case 3:
                requestParams.orderStatus = "" + 4;
                requestParams.orderId = orderId;
                break;
            case 4:
                requestParams.orderStatus = "" + 7;
                requestParams.orderId = orderId;
                break;
        }

        requestParams.memberId = UserModel.getUserModel().getMemberId();
        final Gson gson = new Gson();

        ApiManager.getApiManager().getService(WebApiService.class).queryOrderDetail(gson.toJson(requestParams))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<WaiteGroupBuyDetailModel>() {
                    @Override
                    public void onNext(WaiteGroupBuyDetailModel buyDetailModel) {
                        if (buyDetailModel.success) {
                            detailModel = buyDetailModel;
                            getOrderData(detailModel);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(OrderDetailActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void initClick() {
        mBottomDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(cnt, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", detailModel.data.shopMemberId);
                intent.putExtra("receiverHeadUrl", "");
                intent.putExtra("activityId", MiscUtil.getActivityId());
                intent.putExtra("receiverName", detailModel.data.shopMemberName);
                startActivity(intent);

            }
        });


        mBottomDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                if (currentPosition == 2) {
                    switch (detailModel.data.sendType) {
                        case "1":
                            Intent intent = new Intent(OrderDetailActivity.this, ChaKanWuLiuActivity.class);
                            intent.putExtra("orderId", orderId + "");
                            startActivity(intent);
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


                } else {
                    Toast.makeText(OrderDetailActivity.this, currentPosition + "   " + orderId, Toast.LENGTH_LONG).show();

                }
            }
        });


        mCopyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                ClipboardManager cm = (ClipboardManager) getSystemService(OrderDetailActivity.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(orderId);
                Toast.makeText(OrderDetailActivity.this, "复制成功" + orderId, Toast.LENGTH_LONG).show();

            }
        });


        mBottomDetail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                switch (currentPosition) {
                    case 0:
                        Intent intent = new Intent(OrderDetailActivity.this, PinTuanShareActivity.class);
                        intent.putExtra("currentTimeMillis", detailModel.data.remainTime);
                        intent.putExtra("orderId", Long.valueOf(detailModel.data.orderId));
                        intent.putExtra("sum", (detailModel.data.orderItemList.get(0).salesMemberSum - detailModel.data.orderItemList.size()));
                        intent.putExtra("shareImage", detailModel.data.orderItemList.get(0).img);
                        startActivity(intent);
                        break;
                    case 1:
                        CommomDialog dialog = new CommomDialog(cnt, R.style.dialog, "您确定取消订单么？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    deleteOrder(detailModel.data.orderId);

                                }
                            }
                        }).setTitle("取消订单");
                        dialog.show();

                        break;
                    case 2:
                        CommomDialog dialog2 = new CommomDialog(cnt, R.style.dialog, "您确认收货么？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    orderSignByOrderId(orderId);

                                }
                            }
                        }).setTitle("确认收货");
                        dialog2.show();

                        break;
                    case 3:
                        QueryOrderModel.QueryOrderRequestParams queryOrderRequestParams = new QueryOrderModel.QueryOrderRequestParams();
                        queryOrderRequestParams.orderId = orderId;
                        queryOrderRequestParams.orderStatus = "" + 4;

                        Intent intent2 = new Intent(OrderDetailActivity.this, ApplyRefundActivity.class);
                        intent2.putExtra("queryOrderRequestParams", new Gson().toJson(queryOrderRequestParams));
                        startActivityForResult(intent2, currentPosition);
                        break;
                    case 4:
                        if (detailModel.data.reStatus == 2) {
                            QueryOrderModel.QueryOrderRequestParams orderRequestParams = new QueryOrderModel.QueryOrderRequestParams();
                            orderRequestParams.orderId = orderId;
                            orderRequestParams.orderStatus = "" + 7;
                            Intent i = new Intent(OrderDetailActivity.this, ApplyRefundActivity.class);
                            i.putExtra("queryOrderRequestParams", new Gson().toJson(orderRequestParams));
                            startActivityForResult(i, currentPosition);
                        } else {
                            Intent intent3 = new Intent(OrderDetailActivity.this, ApplyArbitramentActivity.class);
                            intent3.putExtra("orderId", orderId);
                            startActivityForResult(intent3, currentPosition);
                        }
                        break;
                }
            }
        });


    }


    /**
     * 确认收货
     *
     * @param orderId 账单id
     */
    private void orderSignByOrderId(String orderId) {
        ApiManager.getApiManager().getService(WebApiService.class).orderSignByOrderId(UserModel.getUserModel().getMemberId(), orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<OrderSignByOrderIdModel>() {
                    @Override
                    public void onNext(OrderSignByOrderIdModel orderIdModel) {
                        if (orderIdModel.success) {
                            Logger.e("------------>>>>", orderIdModel.data.message);
                            Toast.makeText(OrderDetailActivity.this, "确认收货成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("result", -1); //将计算的值回传回去
                            //通过intent对象返回结果，必须要调用一个setResult方法，
                            //setResult(resultCode, data);第一个参数表示结果返回码，一般只要大于1就可以，但是
                            setResult(2, intent);
                            finish();
                        } else {
                            Toast.makeText(OrderDetailActivity.this, orderIdModel.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(OrderDetailActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });

    }


    /**
     * 取消订单
     *
     * @param orderId 账单id
     */
    private void deleteOrder(String orderId) {
        ApiManager.getApiManager().getService(WebApiService.class).deleteOrder(UserModel.getUserModel().getMemberId(), orderId, "0", "2", "buy")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<DeleteOrderModel>() {
                    @Override
                    public void onNext(DeleteOrderModel orderIdModel) {
                        if (orderIdModel.success) {
                            GetToast.useString(cnt, "取消订单成功～！");
                            Intent intent = new Intent();
                            intent.putExtra("result", -1); //将计算的值回传回去
                            //通过intent对象返回结果，必须要调用一个setResult方法，
                            //setResult(resultCode, data);第一个参数表示结果返回码，一般只要大于1就可以，但是
                            setResult(2, intent);
                            finish(); //结束当前的activity的生命周期

                        } else {
                            Toast.makeText(cnt, orderIdModel.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });


    }


    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (detailModel.data.remainTime > 0) {
                        detailModel.data.remainTime = detailModel.data.remainTime - 1000;
                        mBottomDetail3.setText("邀请好友 " + TimeTools.getCountTimeByLong((detailModel.data.remainTime)));
                        mGroupbuyTime.setText("还剩" + DateTimeUtil.getCountTimeByLong((detailModel.data.remainTime)) + "自动取消成团");

                    } else {
                        mBottomDetail3.setText("邀请好友 " + TimeTools.getCountTimeByLong(0l));
                        mGroupbuyTime.setText("还剩" + DateTimeUtil.format6(new Date(0)) + "小时自动取消成团");

                    }
                    break;

            }
        }
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == currentPosition) {
            if (resultCode == 2) {
                Intent intent = new Intent();
                intent.putExtra("result", -1); //将计算的值回传回去
                //通过intent对象返回结果，必须要调用一个setResult方法，
                //setResult(resultCode, data);第一个参数表示结果返回码，一般只要大于1就可以，但是
                setResult(2, intent);
                finish();
            }
        }
    }

}
