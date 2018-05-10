package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MarketShopModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 确认订单
 * Created by amssy on 18/1/20.
 */

public class ConfirmOrderActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.btn_comfirm_order)
    Button btnComfirmOrder;
    @BindView(R.id.rel_address)
    RelativeLayout relAddress;
    @BindView(R.id.rel_address_none)
    RelativeLayout relAddressNone;
    /**
     * 商家名称
     */
    @BindView(R.id.tv_market_name)
    TextView tvMarketName;
    /**
     * 商品图片
     */
    @BindView(R.id.simple_order)
    SimpleDraweeView simpleOrder;
    /**
     * 商品名称
     */
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    /**
     * 商品规格描述
     */
    @BindView(R.id.tv_shop_sku)
    TextView tvShopSku;

    @BindView(R.id.ll_product_info)
    LinearLayout llProductInfo;
    /**
     * 邮费
     */
    @BindView(R.id.tv_free)
    TextView tvFree;
    /**
     * 商品数量
     */
    @BindView(R.id.tv_shop_num)
    TextView tvShopNum;
    /**
     * 商品价格
     */
    @BindView(R.id.tv_price)
    TextView tvPrice;
    /**
     * 商品总价
     */
    @BindView(R.id.tv_price_all)
    TextView tvPriceAll;
    /**
     * 商品总价
     */
    @BindView(R.id.tv_price_all_bottom)
    TextView tvPriceAllBottom;
    /**
     * 收货人
     */
    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    /**
     * 联系电话
     */
    @BindView(R.id.tv_address_phone)
    TextView tvAddressPhone;
    /**
     * 详细地址
     */
    @BindView(R.id.tv_address_des)
    TextView tvAddressDes;
    /**
     * 商品单价
     */
    @BindView(R.id.tv_unit_price)
    TextView tvUnitPrice;


    private int flag;//1:正常下单,2拼团下单,3参团下单
    private String ShopSku;//商品规格
    private String ShopNum;//商品数量

    private MarketShopModel marketShopModel;
    private double allPrice;
    private Double price;
    private String productDetailJson;//商品信息
    //以下两个参数在参团时传
    private long serialNumber;//拼团号
    private long seriaCreateMemberId;//创建团人id(拼团才有 serialNumber拼团号有值的时候 这个参数才有)

    private String transactionType;//交易类型 1:个人交易，2:c2c群 ,3:微商群
    private String addresseeJson = "";//收货人信息

    private int GET_ADDRESS = 1000;//获取地址标识
    private String addresseeAddress;
    private String addresseeTownName;
    private String addresseePhone;
    private String addresseeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //默认未选地址
        relAddress.setVisibility(View.GONE);
        relAddressNone.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null) {
            flag = intent.getIntExtra("flag", 0);
            marketShopModel = (MarketShopModel) intent.getSerializableExtra("marketShopModel");
            ShopSku = intent.getStringExtra("ShopSku");
            ShopNum = intent.getStringExtra("ShopNum");
            serialNumber = intent.getLongExtra("serialNumber", 0);
            seriaCreateMemberId = intent.getLongExtra("seriaCreateMemberId", 0);
        }
        tvTitleText.setText("确认订单");
        //商家名称
        tvMarketName.setText(marketShopModel.getData().getMemberNick());
        //商品图片
        simpleOrder.setImageURI(marketShopModel.getData().getImg());
        //商品名称
        tvShopName.setText(marketShopModel.getData().getProductName());
        //买家规格
        tvShopSku.setText("买家规格:" + ShopSku);
        //运费
        tvFree.setText(StringUtil.rounded(marketShopModel.getData().getFreight()) + "");
        //购买的商品数量
        tvShopNum.setText(ShopNum + "");
        //商品总价
        if (flag == 1) {
            //商品单价
            tvUnitPrice.setText(StringUtil.rounded(marketShopModel.getData().getProductPrice()));
            price = StringUtil.moneyMultiply(String.valueOf(marketShopModel.getData().getProductPrice()), ShopNum);
        } else {
            //商品单价
            tvUnitPrice.setText(StringUtil.rounded(marketShopModel.getData().getGroupPrice()));
            price = StringUtil.moneyMultiply(String.valueOf(marketShopModel.getData().getGroupPrice()), ShopNum);
        }
        tvPrice.setText(StringUtil.getPrice(String.valueOf(price)));
        //实际付款（含运费）
        allPrice = StringUtil.moneyAdd(String.valueOf(price), String.valueOf(marketShopModel.getData().getFreight()));
        tvPriceAll.setText(StringUtil.getPrice(String.valueOf(allPrice)));
        //总计
        tvPriceAllBottom.setText(StringUtil.getPrice(String.valueOf(allPrice)));
        //注册广播
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.iv_back, R.id.btn_comfirm_order, R.id.rel_address, R.id.rel_address_none})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_comfirm_order:
                //判断是否是快速点击
                if (MerriUtils.isFastDoubleClick2()) {
                    RxToast.showToast("正在生成订单,请勿重复点击...");
                    return;
                }
                if (TextUtils.isEmpty(addresseeJson)) {
                    RxToast.showToast("请添加收货信息");
                    return;
                }
                String shopMemberId = String.valueOf(marketShopModel.getData().getSeller()); //发布人id(卖家)
                String productId = String.valueOf(marketShopModel.getData().getId());//商品id（交易ID）
                String productName = marketShopModel.getData().getProductName();// 商品名
                String img = marketShopModel.getData().getImg();//商品url
                String productPropertySpecValue = ShopSku;//属性规格
                String productPrice = String.valueOf(marketShopModel.getData().getProductPrice());//商品单价
                String discount = "1";// 折扣比例(没有就写1)
                String salesPrice = String.valueOf(marketShopModel.getData().getGroupPrice());//商品活动价(拼团价格)
                String proNum = ShopNum;//商品数量
                String productAmount = String.valueOf(allPrice);//总价
                //String rebates = "";//扣点 (商品交易质保金比例)
                String deliveryFee = String.valueOf(marketShopModel.getData().getFreight());//邮费
                String flags = "1";//备用
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("shopMemberId", shopMemberId);
                    jsonObject.put("productId", productId);
                    jsonObject.put("productName", productName);
                    jsonObject.put("img", img);
                    jsonObject.put("productPropertySpecValue", productPropertySpecValue);
                    jsonObject.put("productPrice", productPrice);
                    jsonObject.put("discount", discount);
                    jsonObject.put("salesPrice", salesPrice);
                    jsonObject.put("productNum", proNum);
                    jsonObject.put("productAmount", productAmount);
                    //jsonObject.put("rebates",rebates);//折扣比例（暂时不用）
                    jsonObject.put("deliveryFee", deliveryFee);
                    jsonObject.put("flag", flags);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //商品信息
                productDetailJson = jsonObject.toString();
                //交易类型 0 个人私聊 1普通交流群 2 B2C 3 C2C
                transactionType = String.valueOf(marketShopModel.getData().getGroupType());

                createOrder();
                break;
            case R.id.rel_address://有地址的情况
                startActivityForResult(new Intent(ConfirmOrderActivity.this, AddressActivity.class).putExtra("productType", marketShopModel.getData().getProductType()), GET_ADDRESS);
                break;
            case R.id.rel_address_none://无地址的情况
                startActivityForResult(new Intent(ConfirmOrderActivity.this, AddressActivity.class).putExtra("productType", marketShopModel.getData().getProductType()), GET_ADDRESS);
                break;
        }
    }

    /**
     * 获取地址回掉
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_ADDRESS) {
            if (resultCode == RESULT_OK) {
                relAddress.setVisibility(View.VISIBLE);
                relAddressNone.setVisibility(View.GONE);
                //解析数据 用作展示
                addresseeJson = data.getStringExtra("addresseeJson");
                try {
                    JSONObject jsonObject = new JSONObject(addresseeJson);
                    addresseeName = jsonObject.optString("addresseeName");
                    addresseePhone = jsonObject.optString("addresseePhone");
                    addresseeTownName = jsonObject.optString("addresseeTownName");
                    addresseeAddress = jsonObject.optString("addresseeAddress");
                    tvAddressName.setText("收货人：" + addresseeName);
                    tvAddressPhone.setText(addresseePhone);
                    tvAddressDes.setText("收货地址：" + addresseeTownName + addresseeAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 提交订单（生成订单）
     */
    private void createOrder() {
        OkGo.<String>get(Urls.createOrder)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("shopId", marketShopModel.getData().getGroupId())//群ID
                .params("transactionType", transactionType)//0 个人私聊 1普通交流群 2 B2C 3 C2C
                .params("flag", flag)//1:正常下单,2拼团下单,3参团下单
                .params("serialNumber", serialNumber)//拼团号
                .params("seriaCreateMemberId", seriaCreateMemberId)//创建团人id
                .params("addresseeJson", addresseeJson)//收货人信息
                .params("productDetailJson", productDetailJson)//商品相关信息
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //flag为3成功  其他失败
                                    if (data.optJSONObject("data").optString("flag").equals("3")) {
                                        Intent intent = new Intent(ConfirmOrderActivity.this, OrderPayActivity.class);
                                        intent.putExtra("flag", flag);
                                        intent.putExtra("orderId", data.optJSONObject("data").optJSONObject("mxOrderInfo").optString("orderId"));
                                        intent.putExtra("tradeTotalAmount", allPrice);
                                        intent.putExtra("address", addresseeTownName + addresseeAddress);
                                        intent.putExtra("name", addresseeName);
                                        intent.putExtra("phone", addresseePhone);
                                        startActivity(intent);
                                    } else {
                                        //OrderModel orderModel = JSON.parseObject(response.body(),OrderModel.class);
                                        RxToast.showToast(data.optJSONObject("data").optString("message"));
                                    }
                                } else {
                                    RxToast.showToast(data.optString("数据异常，请联系相关技术人员"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接受广播关闭界面
     *
     * @param myEventBusModel
     */
    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_ORDER || myEventBusModel.CLOSE_NO_ORDER) {
            finish();
        }
    }
}
