package com.merrichat.net.activity.groupmarket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MarketShopModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.utils.StringUtil.judgeNumber;

/**
 * 购买选择规格弹出框
 * Created by amssy on 18/1/20.
 */

public class MarketDialogActivity extends Activity {
    @BindView(R.id.dialog_parent)
    RelativeLayout dialogParent;
    @BindView(R.id.rel_child)
    RelativeLayout relChild;
    @BindView(R.id.rel_group)
    RelativeLayout relGroup;
    /**
     * 立即购买
     */
    @BindView(R.id.dialog_ok)
    Button dialogOk;
    /**
     * 商品价格
     */
    @BindView(R.id.dialog_price)
    TextView dialogPrice;
    /**
     * 商品库存
     */
    @BindView(R.id.dialog_strock)
    TextView dialogStrock;
    /**
     * 选择商品尺码
     */
    @BindView(R.id.textView_dialog)
    TextView textViewDialog;
    /**
     * 商品规格
     */
    @BindView(R.id.editText_shop_sku)
    EditText editTextShopSku;
    /**
     * 商品数量
     */
    @BindView(R.id.editText_shop_num)
    EditText editTextShopNum;
    /**
     * 商品总价
     */
    @BindView(R.id.tv_shop_all_price)
    TextView tvShopAllPrice;
    /**
     * 商品图片
     */
    @BindView(R.id.dialog_simple_draweeView)
    SimpleDraweeView dialogSimpleDraweeView;

    private String type = "";//market_buy:立即购买  market_tuan:拼团购买
    private MarketShopModel marketShopModel;
    private long serialNumber;//拼团号
    private long seriaCreateMemberId;//创建团人ID
    private double price;//商品价格
    private CommomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_sku);
        ButterKnife.bind(this);
        //透明状态栏
        StatusBarUtil.setTranslucent(MarketDialogActivity.this);
        initView();
    }

    private void initView() {
        StringUtil.setRelHeight(dialogParent, this, 0.75f);
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            marketShopModel = (MarketShopModel) intent.getSerializableExtra("marketShopModel");
            serialNumber = intent.getLongExtra("serialNumber", 0);
            seriaCreateMemberId = intent.getLongExtra("seriaCreateMemberId", 0);
        }
        if (type.equals("market_buy")) {//立即购买
            dialogOk.setText("立即购买");
        } else {//拼团购买
            dialogOk.setText("拼团购买");
        }

        dialogSimpleDraweeView.setImageURI(marketShopModel.getData().getImg());
        if (type.equals("market_buy")) {//立即购买
            dialogPrice.setText(StringUtil.rounded(marketShopModel.getData().getProductPrice()) + "");
            tvShopAllPrice.setText(StringUtil.rounded(marketShopModel.getData().getProductPrice()) + "");
        } else {//拼团购买
            dialogPrice.setText(StringUtil.rounded(marketShopModel.getData().getGroupPrice()) + "");
            tvShopAllPrice.setText(StringUtil.rounded(marketShopModel.getData().getGroupPrice()) + "");
        }
        dialogStrock.setText("库存" + marketShopModel.getData().getAvailableSupplyOrPurchaseQuantity() + "件");

        //商品规格监听
        editTextShopSku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(editTextShopSku.getText().toString())) {
                    textViewDialog.setText("选择尺码及颜色");
                } else {
                    textViewDialog.setText("已选:" + editTextShopSku.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //只能输入数字和小数点
        editTextShopNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //购买数量监听
        editTextShopNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //实物商品
                if (marketShopModel.getData().getProductType() == 0) {
                    //数量输入框中的内容限制（最大：小数点前9位，小数点后2位）
                    judgeNumber(s, editTextShopNum, 9, 2);
                } else {//虚拟商品
                    //数量输入框中的内容限制（最大：小数点前9位，小数点后8位）
                    judgeNumber(s, editTextShopNum, 9, 8);
                }
                //计算金额
                String edNum = editTextShopNum.getText().toString();
                if (!TextUtils.isEmpty(edNum)) {
                    if (type.equals("market_buy")) {//立即购买
                        price = StringUtil.moneyMultiply(String.valueOf(marketShopModel.getData().getProductPrice()), edNum);
                    } else {//拼团购买
                        price = StringUtil.moneyMultiply(String.valueOf(marketShopModel.getData().getGroupPrice()), edNum);
                    }
                    tvShopAllPrice.setText(price + "");
                }
            }
        });
        //注册广播
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.rel_group, R.id.rel_child, R.id.dialog_ok})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rel_group:
                finish();
                //关闭窗体动画显示
                this.overridePendingTransition(0,R.anim.push_dialog_bottom_out);
                break;
            case R.id.rel_child:
                //关闭软键盘
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                break;
            case R.id.dialog_ok:
                if (TextUtils.isEmpty(editTextShopNum.getText().toString())) {
                    RxToast.showToast("请输入购买数量");
                    return;
                } else if (price == 0) {
                    RxToast.showToast("商品价格不能为0");
                    return;
                } else if (marketShopModel.getData().getAvailableSupplyOrPurchaseQuantity() <= 0) {
                    RxToast.showToast("商品库存不足");
                    return;
                } else if (!TextUtils.isEmpty(editTextShopNum.getText().toString()) && marketShopModel.getData().getAvailableSupplyOrPurchaseQuantity() < Double.valueOf(editTextShopNum.getText().toString())) {
                    RxToast.showToast("商品库存不足");
                    return;
                } else {

                    if (TextUtils.isEmpty(editTextShopSku.getText().toString())) {
                        //弹出提示框
                        if (dialog != null) {
                            dialog.show();
                        } else {
                            dialog = new CommomDialog(this, R.style.dialog, "您未输入想购买的商品规格，是否继续购买？", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        //判断商家质保金是否充足
                                        checkProduct();
                                    }
                                }
                            }).setTitle("提示");
                            dialog.show();
                        }
                    } else {
                        //判断商家质保金是否充足
                        checkProduct();
                    }

                }
                break;
        }
    }

    /**
     * 判断商家质保金是否充足
     */
    private void checkProduct() {
        int flag;
        if (type.equals("market_buy")) {//立即购买
            flag = 1;
        } else if (type.equals("market_tuan")) {//拼团购买
            flag = 2;
        } else {//参团下单
            flag = 3;
        }
        OkGo.<String>get(Urls.checkProduct)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("productId", marketShopModel.getData().getId())
                .params("productNum", editTextShopNum.getText().toString())
                .params("price", price)
                .params("type", flag)//1正常下单，2拼团，3参团
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    if (jsonObjectEx.optJSONObject("data").optString("flag").equals("3")) {
                                        //质保金充足即可到确认订单界面
                                        Intent intent = new Intent(MarketDialogActivity.this, ConfirmOrderActivity.class);
                                        if (type.equals("market_buy")) {//立即购买
                                            intent.putExtra("flag", 1);
                                        } else if (type.equals("market_tuan")) {//拼团购买
                                            intent.putExtra("flag", 2);
                                        } else {//参团下单
                                            intent.putExtra("flag", 3);
                                        }
                                        intent.putExtra("marketShopModel", marketShopModel);
                                        intent.putExtra("ShopSku", editTextShopSku.getText().toString());
                                        intent.putExtra("ShopNum", editTextShopNum.getText().toString());
                                        intent.putExtra("serialNumber", serialNumber);
                                        intent.putExtra("seriaCreateMemberId", seriaCreateMemberId);
                                        startActivity(intent);
                                    } else {
                                        RxToast.showToast(jsonObjectEx.optJSONObject("data").optString("message"));
                                    }
                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
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
     * 点击空白位置 隐藏软键盘
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
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
