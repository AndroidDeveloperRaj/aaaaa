package com.merrichat.net.activity.grouporder;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.model.SellOrderDetailModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/22.
 * <p>
 * 销售订单--拒绝退款
 */

public class RefusingRefundmentAty extends MerriActionBarActivity {
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
    @BindView(R.id.tv_pay_totall)
    TextView tvPayTotall;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    @BindView(R.id.fet_refuse_text)
    TextView fetRefuseText;
    @BindView(R.id.et_refuse_reason)
    EditText etRefuseReason;
    private String orderId;
    private int activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refusing_refundment);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("拒绝退款");
        initView();
    }

    private void initView() {
        activityId = getIntent().getIntExtra("activityId", -1);

        if (activityId == RefundMentOrderDetailAty.activityId) {//从 销售订单--退款详情 进入的
            SellOrderDetailModel.DataBean orderBeanInfo = (SellOrderDetailModel.DataBean) getIntent().getSerializableExtra("orderBeanInfo");
            if (orderBeanInfo == null) {
                RxToast.showToast("未获取订单信息，请重试！");
                return;
            }
            orderId = orderBeanInfo.getOrderId();

            SellOrderDetailModel.DataBean.OrderItemListBean orderItemListBean = orderBeanInfo.getOrderItemList().get(0);
            simpleOrder.setImageURI(orderItemListBean.getImg());
            tvShopName.setText(orderItemListBean.getProductName());
            tvShopSku.setText(orderItemListBean.getProductPropertySpecValue());
            tvExpressFee.setText(getResources().getString(R.string.money_character) + StringUtil.getPrice(orderBeanInfo.getDeliveryFee()));
            tvProductNum.setText(orderItemListBean.getProductNum());
            tvPayTotall.setText(getResources().getString(R.string.money_character) + orderBeanInfo.getTotalAmount());

        } else {//从 销售订单--退款列表 进入的
            PendingDeliveryModel.DataBean orderBeanInfo = (PendingDeliveryModel.DataBean) getIntent().getSerializableExtra("orderBeanInfo");
            if (orderBeanInfo == null) {
                RxToast.showToast("未获取订单信息，请重试！");
                return;
            }
            orderId = orderBeanInfo.getOrderId();

            PendingDeliveryModel.DataBean.OrderItemListBean orderItemListBean = orderBeanInfo.getOrderItemList().get(0);
            simpleOrder.setImageURI(orderItemListBean.getImg());
            tvShopName.setText(orderItemListBean.getProductName());
            tvShopSku.setText(orderItemListBean.getProductPropertySpecValue());
            tvExpressFee.setText(getResources().getString(R.string.money_character) + orderBeanInfo.getDeliveryFee());
            tvProductNum.setText(orderItemListBean.getProductNum());
            tvPayTotall.setText(getResources().getString(R.string.money_character) + orderBeanInfo.getTotalAmount());

        }
        SpannableString spanText = new SpannableString("＊ 拒绝原因");
        spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_FF3D6F)), 0, 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        fetRefuseText.append(spanText);

        etRefuseReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 200) {
                    RxToast.showToast("最多输入200字！");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 200) {

                    String refuseReasonString = etRefuseReason.getText().toString().trim();
                    etRefuseReason.setText(refuseReasonString.substring(0, 200));
                    etRefuseReason.setSelection(etRefuseReason.getText().toString().trim().length());
                }
            }
        });


    }

    @OnClick({R.id.ll_product_info, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_product_info:
                break;
            case R.id.tv_sure:
                String reasonStr = etRefuseReason.getText().toString().trim();
                if (RxDataTool.isNullString(reasonStr)) {
                    RxToast.showToast("拒绝原因不能为空！");
                    return;
                }
                confirmProduct(false, reasonStr);
                break;
        }
    }

    /**
     * 确认/拒绝退款
     *
     * @param flag      true:确认退款,false:取消退款
     * @param reasonStr
     */
    private void confirmProduct(boolean flag, String reasonStr) {

        OkGo.<String>post(Urls.confirmProduct)//
                .tag(this)//
                .params("flag", flag)
                .params("orderId", orderId)
                .params("reason", reasonStr)
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
                                        RxToast.showToast("已拒绝退款！");
                                        finish();
                                        if (activityId == RefundMentOrderDetailAty.activityId) {
                                            RxActivityTool.finishActivity(RefundMentOrderDetailAty.class);
                                        }
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
}
