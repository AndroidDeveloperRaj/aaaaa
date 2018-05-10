package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/19.
 * 群交易设置
 */

public class TradingSettingActivity extends MerriActionBarActivity {


    private static final int SHIWU_YONGJIN = 1;
    private static final int XUNI_YONGJIN = 2;
    /**
     * 交流群
     */
    @BindView(R.id.ll_exchange_group)
    LinearLayout llExchangeGroup;

    @BindView(R.id.iv_exchange_group)
    ImageView ivExchangeGroup;


    /**
     * BTC
     */
    @BindView(R.id.ll_btc)
    LinearLayout llBtc;

    @BindView(R.id.iv_btc)
    ImageView ivBtc;


    /**
     * CTC
     */
    @BindView(R.id.ll_ctc)
    LinearLayout llCtc;

    @BindView(R.id.iv_ctc)
    ImageView ivCtc;

    /**
     * 选择交易人数
     */
    @BindView(R.id.ll_choose_number)
    LinearLayout llChooseNumber;

    @BindView(R.id.tv_number)
    TextView tvNumber;

    /**
     * 商品交易设置
     */
    @BindView(R.id.rl_trading_setting)
    RelativeLayout rlTradingSetting;

    /**
     * 实物佣金
     */
    @BindView(R.id.rl_inkind_commission)
    RelativeLayout rlInkindCommission;

    @BindView(R.id.tv_nkind_commission)
    TextView tvInkindCommission;

    /**
     * 虚拟交易群佣金
     */
    @BindView(R.id.rl_virtual_commission)
    RelativeLayout rlVirtualCommission;

    @BindView(R.id.tv_virtual_commission)
    TextView tvVirtualCommission;


    @BindView(R.id.ll_btc_set)
    LinearLayout llBtcSet;

    @BindView(R.id.ll_ctc_set)
    LinearLayout llCtcSet;


    /***********以下为接口返回数据*****************/

    /**
     * 群人数
     */
    private int memberNum;


    /**
     * 群类型0：交流群，1：微商群（BTC） 2：集市群(CTC)
     */
    private int type;


    /**
     * 虚拟交易佣金
     */
    private String virtualCommission;

    /**
     * 实物交易佣金
     */
    private String realCommission;

    /**
     * 群id
     */
    private String groupId = "";


    /**
     * 是否指定商品类型 0：不指定 1：指定
     */
    private String isSpecifiedProduct;

    /**
     * 商品类型默认 0：实物商品 1：虚拟商品
     */
    private String productType;


    /**
     * CTC
     */
//    private String valC2CMin;//CTC虚拟最小
//    private String valC2CMax;//CTC虚拟最大
//
//    private String realC2CMax;//CTC实物最大
//    private String realC2CMin;//CTC实物最小


    /**
     * BTC
     * 值是固定的
     */
//    private String valB2C;//BTC 虚拟佣金比例
//    private String realB2C;//BTC实物佣金比例


    private OptionsPickerView pvCustomOptions;

//    private List<String> shiWuList;
//    private List<String> xuNiList;


    private List<String> yongJinList;
    /**
     * 群类别flag
     * 1交流群
     * 2 CTC
     * 3 BTC
     * <p>
     * 0：交流群，1：微商群（BTC） 2：集市群(CTC)
     */
    private int groupFlag;


    /**
     * 点击完成交易设置 上传的冻结/补交讯美币
     */
    private String freezeCurrency = "";


    /**
     * 商品名字的json类型数组字符串[{"productName":"大米"},{"productName":"饼干"}]
     */
    private String productJson = "";

    /**
     * 点击完成交易设置  上传的虚拟交易佣金
     */
    private String xuNiYongJin = "";

    /**
     * 点击完成交易设置 上传的实物交易佣金
     */
    private String shiWuYongJin = "";

    /**
     * 点击完成交易设置 上传的群人数
     */
    private String carryOutMemberNum = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_setting);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("群交易设置");
        setLeftBack();
        setRightText("完成", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupFlag == 0) {
                    finishTradeSetUp();
                } else if (groupFlag == 2){
                    if (TextUtils.isEmpty(tvInkindCommission.getText().toString()) ||  TextUtils.isEmpty(tvVirtualCommission.getText().toString())){
                        GetToast.useString(cnt, "请选择交易佣金范围");
                        return;
                    }else {
                        finishTradeSetUp();
                    }
                }else {
                    if (TextUtils.isEmpty(carryOutMemberNum)) {
                        GetToast.useString(cnt, "请设置群规模");
                        return;
                    }
                    finishTradeSetUp();
                }
            }
        });
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        yongJinList = getListData();
        communityTradeSetUp();
    }


    private List<String> getListData() {
        List<String> list = new ArrayList<>();
        list.add("0.05");
        list.add("0.1");
        list.add("0.15");
        list.add("0.2");
        list.add("0.25");
        list.add("0.5");
        for (int i = 1; i < 31; i++) {
            list.add(i + "");
        }
        return list;

    }


    /**
     * 完成群交易设置
     */
    private void finishTradeSetUp() {
        OkGo.<String>post(Urls.finishTradeSetUp)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("type", groupFlag)
                .params("memberNum", carryOutMemberNum)
                .params("freezeCurrency", freezeCurrency)
                .params("productType", productType)
                .params("isSpecifiedProduct", isSpecifiedProduct)
                .params("productJson", productJson)
                .params("realCommission", shiWuYongJin)
                .params("virtualCommission", xuNiYongJin)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "设置成功");
                                    finish();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 群交易设置页面
     */
    private void communityTradeSetUp() {
        OkGo.<String>post(Urls.communityTradeSetUp)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    memberNum = data.optInt("memberNum");
                                    type = data.optInt("type");
                                    virtualCommission = data.optString("virtualCommission");
                                    realCommission = data.optString("realCommission");
                                    isSpecifiedProduct = data.optString("isSpecifiedProduct");
                                    setYeMainText();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 设置页面显示
     */
    private void setYeMainText() {
        groupFlag = type;
        setButton(groupFlag);
        if (type == 0) {//交流群
            llBtcSet.setVisibility(View.GONE);
            llCtcSet.setVisibility(View.GONE);
        } else if (type == 1) {//微商群（BTC）
            llBtcSet.setVisibility(View.VISIBLE);
            llCtcSet.setVisibility(View.GONE);
            carryOutMemberNum = memberNum + "";
            tvNumber.setText(carryOutMemberNum + "人");
        } else if (type == 2) {//集市群(CTC)
            llBtcSet.setVisibility(View.VISIBLE);
            llCtcSet.setVisibility(View.VISIBLE);
            carryOutMemberNum = memberNum + "";
            tvNumber.setText(carryOutMemberNum + "人");

            if (!TextUtils.isEmpty(virtualCommission)) {
                tvVirtualCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(virtualCommission)));
            } else {
                tvVirtualCommission.setText("");
            }

            if (!TextUtils.isEmpty(realCommission)) {
                tvInkindCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(realCommission)));
            } else {
                tvInkindCommission.setText("");
            }
            xuNiYongJin = virtualCommission;
            shiWuYongJin = realCommission;
        }
    }


    @OnClick({R.id.ll_exchange_group, R.id.ll_btc, R.id.ll_ctc, R.id.ll_choose_number, R.id.rl_trading_setting, R.id.rl_inkind_commission, R.id.rl_virtual_commission})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.ll_exchange_group:
                if (groupFlag == 0) {
                    return;
                }
                llBtcSet.setVisibility(View.GONE);
                llCtcSet.setVisibility(View.GONE);
                groupFlag = 0;
                setButton(groupFlag);
                break;
            case R.id.ll_ctc://CTC
                if (groupFlag == 2) {
                    return;
                }

                llBtcSet.setVisibility(View.VISIBLE);
                llCtcSet.setVisibility(View.VISIBLE);
                groupFlag = 2;
                setButton(groupFlag);


                xuNiYongJin = virtualCommission;
                shiWuYongJin = realCommission;
                if (!TextUtils.isEmpty(virtualCommission)) {
                    tvVirtualCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(virtualCommission)));
                } else {
                    tvVirtualCommission.setText("");
                }

                if (!TextUtils.isEmpty(realCommission)) {
                    tvInkindCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(realCommission)));
                } else {
                    tvInkindCommission.setText("");
                }

                break;
            case R.id.ll_btc://BTC
                if (groupFlag == 1) {
                    return;
                }

                llBtcSet.setVisibility(View.VISIBLE);
                llCtcSet.setVisibility(View.GONE);
                groupFlag = 1;
                setButton(groupFlag);
                break;
            case R.id.ll_choose_number://设置/修改群规模
                startActivityForResult(new Intent(cnt, ChooseNumberTradesActivity.class).putExtra("memberNum", memberNum).putExtra("type", type), ChooseNumberTradesActivity.activityId);
                break;
            case R.id.rl_trading_setting://商品交易设置
                startActivityForResult(new Intent(cnt, SetGoodsTradingActivity.class).putExtra("groupId", groupId), SetGoodsTradingActivity.activityId);
                break;
            case R.id.rl_inkind_commission://实物佣金
                if (groupFlag == 2) {
                    initCustomOptionPicker(SHIWU_YONGJIN);
                    pvCustomOptions.show();
                }
                break;
            case R.id.rl_virtual_commission://虚拟交易群佣金
                if (groupFlag == 2) {
                    initCustomOptionPicker(XUNI_YONGJIN);
                    pvCustomOptions.show();
                }
                break;
        }
    }

    private void setButton(int groupFlag) {
        switch (groupFlag) {
            case 0:
                ivExchangeGroup.setImageResource(R.mipmap.jubao_click_xuanzhong_2x);
                ivBtc.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                ivCtc.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                break;

            case 1:
                ivExchangeGroup.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                ivBtc.setImageResource(R.mipmap.jubao_click_xuanzhong_2x);
                ivCtc.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                break;

            case 2:
                ivExchangeGroup.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                ivBtc.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                ivCtc.setImageResource(R.mipmap.jubao_click_xuanzhong_2x);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ChooseNumberTradesActivity.activityId) {
                String number = data.getStringExtra("number");
                String temp = data.getStringExtra("freezeCurrency");
                if(!TextUtils.isEmpty(temp)&&"0".equals(temp)){
                    freezeCurrency = "";
                }else {
                    freezeCurrency = temp;
                }
                carryOutMemberNum = number;
                tvNumber.setText(carryOutMemberNum + "人");
            }
            if (requestCode == SetGoodsTradingActivity.activityId) {
                isSpecifiedProduct = data.getStringExtra("isSpecifiedProduct");
                productType = data.getStringExtra("productType");
                productJson = data.getStringExtra("productJson");
            }
        }
    }


    private void initCustomOptionPicker(final int popupType) {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                switch (popupType) {
                    case SHIWU_YONGJIN:
                        String shiWu = yongJinList.get(options1);
                        shiWuYongJin = new DecimalFormat("0.0000").format(Float.parseFloat(shiWu) / 100);
                        tvInkindCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(shiWuYongJin)));
                        break;
                    case XUNI_YONGJIN:
                        String xuNi = yongJinList.get(options1);
                        xuNiYongJin = new DecimalFormat("0.0000").format(Float.parseFloat(xuNi) / 100);
                        tvVirtualCommission.setText(new DecimalFormat("0.00%").format(Float.parseFloat(xuNiYongJin)));
                        break;
                }
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        TextView tvPopTitle = (TextView) v.findViewById(R.id.title);
                        final TextView tvOk = (TextView) v.findViewById(R.id.ok);
                        TextView tvCancel = (TextView) v.findViewById(R.id.cancel);

                        switch (popupType) {
                            case SHIWU_YONGJIN:
                                tvPopTitle.setText("实物佣金比例设置");
                                break;
                            case XUNI_YONGJIN:
                                tvPopTitle.setText("虚拟佣金比例设置");
                                break;

                        }
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });
                    }
                })
                .setOutSideCancelable(false)
                .isDialog(true)
                .build();
        switch (popupType) {
            case SHIWU_YONGJIN:
                pvCustomOptions.setPicker(yongJinList);//添加数据}
                break;
            case XUNI_YONGJIN:
                pvCustomOptions.setPicker(yongJinList);//添加数据}
                break;
        }

    }
}
