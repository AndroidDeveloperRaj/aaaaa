package com.merrichat.net.activity.message;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.TImage;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.message.setting.MasterWalletsActivity;
import com.merrichat.net.activity.my.mywallet.RechargeMoneyActivity;
import com.merrichat.net.adapter.ImageAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GoodsTradingModel;
import com.merrichat.net.model.ProductListModel;
import com.merrichat.net.model.ServiceFeeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.utils.CashierInputFilter;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.weidget.RecycleViewDivider;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by amssy on 2018/1/25.
 * 发布交易（群发布交易，单聊发布交易--0普通交流群 1 个人私聊）
 */

public class SendDealAty extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_PRODUCTLIST = 4;
    private static final int UPLOAD_SUC = 2;
    private static final int UPLOAD_Fail = 3;
    /**
     * title
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    //服务费
    @BindView(R.id.tv_service_fee)
    TextView tv_service_fee;

    /**
     * tab
     */
    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;
    /**
     * 指定商品
     */
    @BindView(R.id.lay_guding_shop)
    LinearLayout layGuDingShop;
    /**
     * 商品名称
     */
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    /**
     * 未指定商品
     */
    @BindView(R.id.lay_no_guding_shop)
    LinearLayout layNoGuDingShop;
    /**
     * 商品名称
     */
    @BindView(R.id.et_shop_name)
    EditText etShopName;
    /**
     * 商品单价
     */
    @BindView(R.id.et_shop_unit_price)
    EditText etShopUnitPrice;
    /**
     * 商品团购价
     */
    @BindView(R.id.et_shop_group_price)
    EditText etShopGroupPrice;
    /**
     * 商品成团人数
     */
    @BindView(R.id.et_shop_group_people_num)
    EditText etShopGroupPeopleNum;
    /**
     * 商品数量
     */
    @BindView(R.id.et_shop_num)
    EditText etShopNum;
    /**
     * 商品运费
     */
    @BindView(R.id.et_shop_shipping_costs)
    EditText etShopShippingCosts;
    /**
     * 商品总价
     */
    @BindView(R.id.tv_shop_total_price)
    TextView tvShopTotalPrice;
    /**
     * 商品冻结质保金
     */
    @BindView(R.id.tv_zbj)
    TextView tvZbj;
    /**
     * 商品文字描述
     */
    @BindView(R.id.et_word_description)
    EditText etWordDescription;
    /**
     * 商品规格
     */
    @BindView(R.id.et_shop_format)
    EditText etShopFormat;
    /**
     * 添加图片
     */
    @BindView(R.id.tv_add_picture)
    TextView tvAddPicture;
    /**
     * 图片列表
     */
    @BindView(R.id.rv_img)
    RecyclerView rvImg;

    @BindView(R.id.lay_shop_group_people_num)
    LinearLayout layShopGroupPeopleNum;
    @BindView(R.id.lay_shop_group_price)
    LinearLayout layShopGroupPrice;
    @BindView(R.id.lay_shop_shipping_costs)
    LinearLayout layShopShippingCosts;

    private Context mContext;
    private String[] mTitles = {"实物商品", "虚拟物品"};
    private String groupId;
    private int groupType = 0;//0普通交流群 1 个人私聊 2 C2C 3 B2C
    private int isSpecifiedProduct = 0;//0：不指定 1：指定
    private int productType = 0;//0实物 1虚物
    private int transInfoType = 0;//0 销售 1求购(这一版本只有销售)
    private ArrayList<String> imgUrlList;
    private ArrayList<String> upImgList;//图片上传成功后 文件名的集合
    private ImageAdapter adapter;
    private String fileSuffix;
    private String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    private LoadingDialog loadingDialog;
    private List<GoodsTradingModel> produstList;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:
                    String img = "";
                    String imgUrls = "";
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < upImgList.size(); i++) {
                        jsonArray.put(upImgList.get(i));
                    }
                    img = upImgList.get(0);
                    imgUrls = jsonArray.toString();
                    createTransInfo(img, imgUrls);
                    handled = true;
                    break;
                case UPLOAD_Fail:
                    handled = true;
                    closeDialog();
                    break;
            }
            return handled;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setBackgroundDrawableResource(R.color.background);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 38);
        setContentView(R.layout.activity_send_deal);
        ButterKnife.bind(this);
        mContext = this;
        getAllOrderFee();
        initView();
    }

    private void initTab() {
        sgTablayout.setVisibility(View.VISIBLE);
        sgTablayout.setTabData(mTitles);
        layShopGroupPrice.setVisibility(View.GONE);
        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        layShopShippingCosts.setVisibility(View.VISIBLE);
                        productType = 0;
                        getAllOrderFee();
                        break;
                    case 1:
                        layShopShippingCosts.setVisibility(View.GONE);
                        productType = 1;
                        getAllOrderFee();
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        groupType = getIntent().getIntExtra("groupType", 0);
        tvTitleText.setText("发布交易");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");
        produstList = new ArrayList<>();
        loadingDialog = new LoadingDialog(this).setLoadingText("发布交易中...").setInterceptBack(true);
        tvRight.setTextColor(getResources().getColor(R.color.base_9E9FAB));
        tvRight.setEnabled(false);
        if (groupType == 1) {
            initTab();
        } else {
            sgTablayout.setVisibility(View.GONE);
        }
        if (groupType == 2 || groupType == 3) {
            queryProductList();
        }
        imgUrlList = new ArrayList<>();
        upImgList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvImg.setLayoutManager(layoutManager);
        rvImg.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.line_grayD9)));
        rvImg.setNestedScrollingEnabled(false);
        adapter = new ImageAdapter(R.layout.item_img, imgUrlList);
        rvImg.setAdapter(adapter);

        InputFilter[] filters = {new CashierInputFilter()};
        etShopGroupPrice.setFilters(filters);
        etShopUnitPrice.setFilters(filters);
        tvShopTotalPrice.setFilters(filters);
        etShopNum.setFilters(filters);
        etShopShippingCosts.setFilters(filters);

        TextChange textChange = new TextChange();
        etShopName.addTextChangedListener(textChange);
        etShopGroupPrice.addTextChangedListener(textChange);
        etShopNum.addTextChangedListener(textChange);
        etShopUnitPrice.addTextChangedListener(textChange);
        tvShopName.addTextChangedListener(textChange);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, ChatAmplificationActivity.class);
                intent.putStringArrayListExtra("imgUrl", imgUrlList);
                mContext.startActivity(intent);
                ((SendDealAty) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleItemDialog(position);
                return false;
            }
        });
    }

    private void showDeleItemDialog(final int index) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("确定要删除此张照片吗?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(null)//
                .dismissAnim(null)//
                .show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        imgUrlList.remove(index);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.tv_add_picture, R.id.lay_guding_shop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                fabu();
                break;
            case R.id.tv_add_picture:
                selectPic();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.lay_guding_shop:
                startActivityForResult(new Intent(mContext, ProductListAty.class).putExtra("produstList", (Serializable) produstList), REQUEST_PRODUCTLIST);
                break;
        }
    }

    /**
     * 发布
     */
    private void fabu() {
        if (isSpecifiedProduct == 0) {
            if (TextUtils.isEmpty(etShopName.getText())) {
                RxToast.showToast("请先补全发布信息");
                return;
            }
        } else if (isSpecifiedProduct == 1) {
            if (TextUtils.isEmpty(tvShopName.getText())) {
                RxToast.showToast("请先补全发布信息");
                return;
            }
        }
        if (productType == 0) {
            if (TextUtils.isEmpty(etShopUnitPrice.getText()) ||
                    TextUtils.isEmpty(etShopNum.getText())
                    ) {
                RxToast.showToast("请先补全发布信息");
                return;
            } else {
                if (!TextUtils.isEmpty(etShopGroupPrice.getText())) {
                    if (TextUtils.isEmpty(etShopGroupPeopleNum.getText())) {
                        RxToast.showToast("请先补全发布信息");
                        return;
                    }
                    if (Double.parseDouble(etShopGroupPrice.getText().toString()) > Double.parseDouble(etShopUnitPrice.getText().toString())) {
                        RxToast.showToast("团购价不能高于单价");
                        return;
                    }
                    if (Double.parseDouble(etShopGroupPrice.getText().toString()) == 0) {
                        RxToast.showToast("团购价不能为0");
                        return;
                    }
                    if (Integer.parseInt(etShopGroupPeopleNum.getText().toString()) < 1) {
                        RxToast.showToast("拼团人数最少为2");
                        return;
                    }
                }
            }
        } else {
            if (TextUtils.isEmpty(etShopUnitPrice.getText()) ||
                    TextUtils.isEmpty(etShopNum.getText())
                    ) {
                RxToast.showToast("请先补全发布信息");
                return;
            }
        }

        if (Double.parseDouble(etShopUnitPrice.getText().toString()) < 0.01) {
            RxToast.showToast("单价最少为0.01元");
            return;
        }
        if (Double.parseDouble(etShopNum.getText().toString()) == 0) {
            RxToast.showToast("商品数量不能为0");
            return;
        }

        if (imgUrlList.size() > 0) {
            if (loadingDialog != null) {
                loadingDialog.show();
            } else {
                loadingDialog = new LoadingDialog(this).setLoadingText("发布交易中...").setInterceptBack(true);
                loadingDialog.show();
            }
            compressImg(imgUrlList);
        } else {
            createTransInfo("", "");
        }
    }

    /**
     * 添加图片
     */
    private void selectPic() {
        MultiImageSelector selector = MultiImageSelector.create(mContext);
        selector.showCamera(true);
        selector.count(9);
        selector.multi();
        selector.origin(imgUrlList);
        selector.start((Activity) mContext, REQUEST_IMAGE);
    }

    /**
     * 查询所有费用设置
     */
    private void getAllOrderFee() {
        OkGo.<String>post(Urls.getAllOrderFee)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ServiceFeeModel model = new Gson().fromJson(response.body(), ServiceFeeModel.class);
                        //0交易发布服务费用 1(实物)交易质保币比例 2(虚物)交易质保币比例
                        for (int i = 0; i < model.getData().size(); i++) {
                            int feeType = model.getData().get(i).getFeeType();
                            if (feeType == 0) {//0交易发布服务费用
                                if (groupType == 1) {//个人聊天
                                    tv_service_fee.setText("发布交易需要交纳技术服务费" + model.getData().get(i).getPersonalValue() + "讯美币");
                                } else if (groupType == 2) {//C2C
                                    tv_service_fee.setText("发布交易需要交纳技术服务费" + model.getData().get(i).getC2CValue() + "讯美币");
                                } else if (groupType == 3) {//B2C
                                    tv_service_fee.setText("发布交易需要交纳技术服务费" + model.getData().get(i).getB2CValue() + "讯美币");
                                }
                            }
                            if (feeType == 1 && productType == 0) {//实物质保金
                                if (groupType == 1) {//个人聊天
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getPersonalValue()) * 100 + "%讯美币");
                                } else if (groupType == 2) {//C2C
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getC2CValue()) * 100 + "%讯美币");
                                } else if (groupType == 3) {//B2C
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getB2CValue()) * 100 + "%讯美币");
                                }
                            }
                            if (feeType == 2 && productType == 1) {//虚物质保金
                                if (groupType == 1) {//个人聊天
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getPersonalValue()) * 100 + "%讯美币");
                                } else if (groupType == 2) {//C2C
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getC2CValue()) * 100 + "%讯美币");
                                } else if (groupType == 3) {//B2C
                                    tvZbj.setText("单个商品冻结" + Float.parseFloat(model.getData().get(i).getB2CValue()) * 100 + "%讯美币");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 发布交易查询商品类型和商品列表
     */
    private void queryProductList() {
        OkGo.<String>post(Urls.queryProductList)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ProductListModel model = new Gson().fromJson(response.body(), ProductListModel.class);
                        isSpecifiedProduct = model.getData().getIsSpecifiedProduct();
                        productType = model.getData().getProductType();
                        if (isSpecifiedProduct == 0) {
                            layNoGuDingShop.setVisibility(View.VISIBLE);
                            layGuDingShop.setVisibility(View.GONE);
                        } else if (isSpecifiedProduct == 1) {
                            layNoGuDingShop.setVisibility(View.GONE);
                            layGuDingShop.setVisibility(View.VISIBLE);
                        }
                        if (productType == 0) {
                            layShopGroupPrice.setVisibility(View.VISIBLE);
                            layShopShippingCosts.setVisibility(View.VISIBLE);
                        } else if (productType == 1) {
                            layShopGroupPrice.setVisibility(View.GONE);
                            layShopShippingCosts.setVisibility(View.GONE);
                        }
                        produstList.clear();
                        produstList.addAll(model.getData().getList());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 发布交易
     */
    private void createTransInfo(String img, String imgUrls) {
        String productName = "";
        if (isSpecifiedProduct == 0) {
            productName = etShopName.getText().toString();
        } else if (isSpecifiedProduct == 1) {
            productName = tvShopName.getText().toString();
        }
        OkGo.<String>post(Urls.createTransInfo)
                .params("groupType", groupType)
                .params("groupId", groupId)
                .params("creator", UserModel.getUserModel().getMemberId())
                .params("seller", UserModel.getUserModel().getMemberId())
                .params("buyer", "")
                .params("transInfoType", transInfoType)
                .params("productType", productType)
                .params("productCategoryId", "")
                .params("productId", "")
                .params("productName", productName)
                .params("productModelDesc", etShopFormat.getText().toString())
                .params("productPrice", etShopUnitPrice.getText().toString())
                .params("groupPrice", etShopGroupPrice.getText().toString())
                .params("groupNum", etShopGroupPeopleNum.getText().toString())
                .params("supplyOrPurchaseQuantity", etShopNum.getText().toString())
                .params("availableSupplyOrPurchaseQuantity", etShopNum.getText().toString())
                .params("freight", etShopShippingCosts.getText().toString())
                .params("productDescribe", etWordDescription.getText().toString())
                .params("img", img)
                .params("imgUrls", imgUrls)
                .params("outAccountId", UserModel.getUserModel().getAccountId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        closeDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("result")) {
                                    JSONObject transInfo = data.optJSONObject("transInfo");
                                    RxToast.showToast("发布成功");
                                    Intent intent = new Intent();
                                    intent.putExtra("transInfo", transInfo.toString());
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    if (data.optString("msg").equals("扣除服务费失败,余额不足")) {
                                        showChongZhiDialog();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        closeDialog();
                    }
                });
    }

    private void showChongZhiDialog() {
        final MaterialDialog dialog = new MaterialDialog(mContext);
        dialog.title("提示").content("您的讯美币余额不足，无法发布")//
                .btnText("取消", "充值")
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        startActivity(new Intent(mContext, RechargeMoneyActivity.class));
                        dialog.dismiss();
                    }
                }

        );
    }


    //EditText的监听器
    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (etShopUnitPrice.length() > 0 && etShopNum.length() > 0) {//求的总价
                String price = etShopUnitPrice.getText().toString();
                String num = etShopNum.getText().toString();
//                tvShopTotalPrice.setText((new BigDecimal(price.length() == 0 ? "10" : price).multiply(new BigDecimal(num.length() == 0 ? "1" : num))) + "");
                tvShopTotalPrice.setText(StringUtil.moneyMultiply(price, num) + "");
            }
            if (etShopGroupPrice.length() > 0) {//如果有团购价，补全团购人数
                layShopGroupPeopleNum.setVisibility(View.VISIBLE);
            } else {
                layShopGroupPeopleNum.setVisibility(View.GONE);
                etShopGroupPeopleNum.setText("");
            }

            if (isSpecifiedProduct == 1) {
                if (tvShopName.length() > 0 &&
                        etShopUnitPrice.length() > 0 &&
                        etShopNum.length() > 0) {
                    tvRight.setTextColor(getResources().getColor(R.color.normal_red));
                    tvRight.setEnabled(true);
                } else {
                    tvRight.setTextColor(getResources().getColor(R.color.base_9E9FAB));
                    tvRight.setEnabled(false);
                }
            } else {
                if (etShopName.length() > 0 &&
                        etShopUnitPrice.length() > 0 &&
                        etShopNum.length() > 0) {
                    tvRight.setTextColor(getResources().getColor(R.color.normal_red));
                    tvRight.setEnabled(true);
                } else {
                    tvRight.setTextColor(getResources().getColor(R.color.base_9E9FAB));
                    tvRight.setEnabled(false);
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                imgUrlList.clear();
                imgUrlList.addAll(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_PRODUCTLIST) {
            if (data != null) {
                GoodsTradingModel model = (GoodsTradingModel) data.getSerializableExtra("goodsTradingModel");
                tvShopName.setText(model.getProductName());
            }
        }
    }


    /**
     * 批量压缩图片
     *
     * @param urlsList
     */
    private void compressImg(final ArrayList<String> urlsList) {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(200 * 1024)
                .enableReserveRaw(true)
                .create();
        CompressImageImpl.of(this, config, getTImagesWithUris(urlsList, TImage.FromType.OTHER), new CompressImage.CompressListener() {
            @Override
            public void onCompressSuccess(ArrayList<TImage> images) {
                ossUpload(images);
            }

            @Override
            public void onCompressFailed(ArrayList<TImage> images, String msg) {
                Logger.e("onCompressFailed", msg);
                closeDialog();
            }
        }).compress();
    }

    /**
     * 将Uri集合转换成TImage集合
     *
     * @param urls
     * @return
     */
    public static ArrayList<TImage> getTImagesWithUris(ArrayList<String> urls, TImage.FromType fromType) {
        ArrayList<TImage> tImages = new ArrayList();
        for (String url : urls) {
            TImage tImage = TImage.of(url, fromType);
            tImage.setCompressPath(url);
            tImages.add(tImage);
        }
        return tImages;
    }


    /**
     * 阿里云OSS上传（默认是异步多文件上传）
     *
     * @param images
     */
    private void ossUpload(final ArrayList<TImage> images) {
        if (images.size() <= 0) {
            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
            return;// 这个return必须有，否则下面报越界异常，原因自己思考下哈
        }
        final String url = images.get(0).getCompressPath();
        if (TextUtils.isEmpty(url)) {
            images.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(images);
            return;
        }

        File file = new File(url);
        if (null == file || !file.exists()) {
            images.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(images);
            return;
        }

        fileSuffix = "MerriChat_Deal_" + FileUtils.getCurrentDate() + ".jpg";
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, fileSuffix, url);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    images.remove(0);
                    Logger.e("onSuccess：：：：", "onSuccess::上传成功！");
                    upImgList.add(publicImgUrl + fileSuffix);
                    if (images.size() == 0) {
                        handler.sendEmptyMessage(UPLOAD_SUC);
                    }
                    ossUpload(images);// 递归同步效果
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    Logger.e("onFailure：：：：", "onFailure::上传失败！");
                    handler.sendEmptyMessage(UPLOAD_Fail);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                }
            });
        }
    }

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialog();
    }

    private void closeDialog() {
        if (loadingDialog != null) {
            loadingDialog.close();
            loadingDialog = null;
        }
    }
}
