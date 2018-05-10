package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/23.
 * 确定仲裁
 */

public class ConfirmArbitrationActivity extends MerriActionBarActivity {

    /**
     * 商品图片
     */
    @BindView(R.id.iv_product_picture)
    ImageView ivProductPicture;

    /**
     * 商品title
     */
    @BindView(R.id.tv_product_title)
    TextView tvProductTitle;

    /**
     * 商品介绍
     */
    @BindView(R.id.tv_product_specifications)
    TextView tvProductSpecifications;

    @BindView(R.id.rl_arbitration_goods)
    RelativeLayout rlArbitrationGoods;

    /**
     * 运费
     */
    @BindView(R.id.tv_shipping_costs)
    TextView tvShippingCosts;

    /**
     * 商品数量
     */
    @BindView(R.id.tv_products_number)
    TextView tvProductsNumber;

    /**
     * 订单总金额
     */
    @BindView(R.id.tv_order_amount)
    TextView tvOrderAmount;

    /**
     * 同意
     */
    @BindView(R.id.ll_agree)
    LinearLayout llAgree;

    @BindView(R.id.iv_agree)
    ImageView ivAgree;

    @BindView(R.id.tv_agree)
    TextView tvAgree;

    /**
     * 不同意
     */
    @BindView(R.id.ll_disagree)
    LinearLayout llDisagree;

    @BindView(R.id.iv_disagree)
    ImageView ivDisagree;

    @BindView(R.id.tv_disagree)
    TextView tvDisagree;

    /**
     * 输入仲裁原因
     */
    @BindView(R.id.et_reason)
    EditText etReason;

    /**
     * 确定按钮
     */
    @BindView(R.id.ll_determine)
    LinearLayout llDetermine;


    /**
     * 是否同意仲裁退款flag
     * 同意仲裁退款 1
     * 不同意   2
     * 默认为1
     */
    private int arbitrationFlag = 1;

    /**
     * 群id
     */
    private String groupId = "";

    private String productName = "";
    private String productPropertySpecValue = "";
    private String deliveryFee = "";
    private String productNum = "";
    private String totalProductAmount = "";
    private String img = "";
    private String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_arbitration);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("确定仲裁");
        setLeftBack();
    }

    private void initView() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        productName = intent.getStringExtra("productName");
        productPropertySpecValue = intent.getStringExtra("productPropertySpecValue");
        deliveryFee = intent.getStringExtra("deliveryFee");
        productNum = intent.getStringExtra("productNum");
        totalProductAmount = intent.getStringExtra("totalProductAmount");
        img = intent.getStringExtra("img");
        orderId = intent.getStringExtra("orderId");

        tvProductTitle.setText(productName);
        tvProductSpecifications.setText(productPropertySpecValue);
        tvShippingCosts.setText(getResources().getString(R.string.money_character) + " " + StringUtil.getPrice(deliveryFee));
        tvProductsNumber.setText(productNum);
        tvOrderAmount.setText(getResources().getString(R.string.money_character) + StringUtil.getPrice(totalProductAmount));
        Glide.with(cnt).load(img).error(R.mipmap.load).into(ivProductPicture);



//        tvProductTitle.setText(orderItemListBean.getProductName());
//        tvProductSpecifications.setText(orderItemListBean.getProductPropertySpecValue());
//        tvShippingCosts.setText(getResources().getString(R.string.money_character) + " " + StringUtil.getPrice(dataBeans.getDeliveryFee()));
//        tvProductsNumber.setText(orderItemListBean.getProductNum());
//        tvOrderAmount.setText(getResources().getString(R.string.money_character) + StringUtil.getPrice(dataBeans.getTotalProductAmount()));
    }

    @OnClick({R.id.ll_agree, R.id.ll_disagree, R.id.ll_determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_agree://同意仲裁申请
                if (arbitrationFlag == 1) {
                    return;
                }
                arbitrationFlag = 1;
                ivAgree.setImageResource(R.mipmap.accept_2x_true);
                tvAgree.setTextColor(getResources().getColor(R.color.base_FF3D6F));

                ivDisagree.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                tvDisagree.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.ll_disagree://不同意仲裁申请
                if (arbitrationFlag == 2) {
                    return;
                }
                arbitrationFlag = 2;
                ivAgree.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                tvAgree.setTextColor(getResources().getColor(R.color.black));

                ivDisagree.setImageResource(R.mipmap.accept_2x_true);
                tvDisagree.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                break;
            case R.id.ll_determine://确定
                zhongCai();
                break;
        }
    }

    private void zhongCai() {
        if (TextUtils.isEmpty(etReason.getText().toString().trim())) {
            GetToast.useString(cnt, "请输入仲裁原因");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            if (arbitrationFlag == 1) {
                jsonObject.put("isExit", true);
            } else if (arbitrationFlag == 2) {
                jsonObject.put("isExit", false);
            }
            jsonObject.put("orderId", orderId);
            jsonObject.put("arbitrateResult", etReason.getText().toString().trim());
            jsonObject.put("groupId", groupId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkArbitrateInfo(jsonObject.toString());
    }

    /**
     * 审核仲裁信息
     */
    private void checkArbitrateInfo(String stringInfo) {
        OkGo.<String>post(Urls.checkArbitrateInfo)
                .params("jsonObject", stringInfo)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                    if (data.optBoolean("success")){
                                        GetToast.useString(cnt,"仲裁完成");
                                        MyEventBusModel eventBusModel = new MyEventBusModel();
                                        eventBusModel.FINISH_GROUP_ORDER_DETAILS = true;
                                        EventBus.getDefault().post(eventBusModel);
                                        finish();
                                    }else {
                                        String message = data.optString("message");
                                        if (!TextUtils.isEmpty(message)){
                                            GetToast.useString(cnt,message);
                                        }
                                    }
                            }else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)){
                                    GetToast.useString(cnt,message);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
