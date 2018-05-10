package com.merrichat.net.activity.grouporder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ScanAty;
import com.merrichat.net.activity.groupmanage.SelectExpressCompanyActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CompanyData;
import com.merrichat.net.model.ConfirmShipmentEpxJsonModel;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.model.SellOrderDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/24.
 * <p>
 * 确认发货
 */

public class ConfirmDeliveryGoodAty extends MerriActionBarActivity {
    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    @BindView(R.id.tv_address_phone)
    TextView tvAddressPhone;
    @BindView(R.id.tv_kuai_di)
    DrawableCenterTextView tvKuaiDi;
    @BindView(R.id.tv_zi_qu)
    DrawableCenterTextView tvZiQu;
    @BindView(R.id.tv_shang_men)
    DrawableCenterTextView tvShangMen;
    @BindView(R.id.rl_quanxian)
    LinearLayout rlQuanxian;
    @BindView(R.id.tv_exp_text)
    TextView tvExpText;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.ll_express_name)
    RelativeLayout llExpressName;
    @BindView(R.id.tv_num_text)
    TextView tvNumText;
    @BindView(R.id.tv_express_num)
    EditText tvExpressNum;
    @BindView(R.id.ll_express_num)
    RelativeLayout llExpressNum;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    @BindView(R.id.tv_address_address)
    TextView tvAddressAddress;
    private int REQUEST_CODE_SCAN = 0x001;
    private int REQUEST_CODE_EXPRESS = 0x002;
    private int sendType = 1;//1 快递 2 自取 3 送货上门
    private String orderId = ""; //订单号
    private CompanyData expressInfoModel;// 快递公司信息
    private int activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_delivery_good);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("确认发货");
        initView();
    }


    private void initView() {
        activityId = getIntent().getIntExtra("activityId", -1);
        if (activityId == SellOrderDetailAty.activityId) {// 从 销售订单--待发货--订单详情
            SellOrderDetailModel.DataBean orderBeanInfo = (SellOrderDetailModel.DataBean) getIntent().getSerializableExtra("orderBeanInfo");
            if (orderBeanInfo != null) {
                orderId = orderBeanInfo.getOrderId();
                tvAddressName.setText("收货人  " + orderBeanInfo.getAddresseeName());
                tvAddressPhone.setText(orderBeanInfo.getAddresseePhone());
                tvAddressAddress.setText(orderBeanInfo.getAddresseeDetailed());
            }
        } else {//销售订单--待发货列表

            PendingDeliveryModel.DataBean orderBeanInfo = (PendingDeliveryModel.DataBean) getIntent().getSerializableExtra("orderBeanInfo");
            if (orderBeanInfo != null) {
                orderId = orderBeanInfo.getOrderId();
                tvAddressName.setText("收货人  " + orderBeanInfo.getAddresseeName());
                tvAddressPhone.setText(orderBeanInfo.getAddresseePhone());
                tvAddressAddress.setText(orderBeanInfo.getAddresseeDetailed());
            }
        }
        choosePickupWay(tvKuaiDi);

    }

    @OnClick({R.id.tv_kuai_di, R.id.tv_zi_qu, R.id.tv_shang_men, R.id.ll_express_name, R.id.iv_sao_yun_hao, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_kuai_di://快递
                sendType = 1;
                choosePickupWay(tvKuaiDi);
                break;
            case R.id.tv_zi_qu://自取
                sendType = 2;

                choosePickupWay(tvZiQu);

                break;
            case R.id.tv_shang_men://送货上门
                sendType = 3;

                choosePickupWay(tvShangMen);

                break;
            case R.id.ll_express_name:
                RxActivityTool.skipActivityForResult(this, SelectExpressCompanyActivity.class, REQUEST_CODE_EXPRESS);
                break;
            case R.id.iv_sao_yun_hao:
                RxActivityTool.skipActivityForResult(this, ScanAty.class, REQUEST_CODE_SCAN);
                break;
            case R.id.tv_sure:
                String expressName = tvExpressName.getText().toString().trim();
                String expressNum = tvExpressNum.getText().toString().trim();
                if (sendType == 1) {//发货方式如果是快递 需要快递信息 自取或送货上门不需要
                    if (RxDataTool.isNullString(expressName)) {
                        RxToast.showToast("请选择快递公司！");
                        return;
                    }
                    if (RxDataTool.isNullString(expressNum)) {
                        RxToast.showToast("请输入运单号！");
                        return;
                    }
                    if (expressInfoModel != null) {
                        ConfirmShipmentEpxJsonModel confirmShipmentEpxJsonModel = new ConfirmShipmentEpxJsonModel(expressInfoModel.netId + "", expressInfoModel.netName, expressInfoModel.code, expressInfoModel.url, expressNum, expressInfoModel.telephone);
                        String expressInfoJsonStr = new Gson().toJson(confirmShipmentEpxJsonModel);
                        confirmShipment(expressInfoJsonStr);
                    }
                } else {
                    ConfirmShipmentEpxJsonModel confirmShipmentEpxJsonModel = new ConfirmShipmentEpxJsonModel();
                    String expressInfoJsonStr = new Gson().toJson(confirmShipmentEpxJsonModel);
                    confirmShipment(expressInfoJsonStr);

                }
                break;
        }
    }

    /**
     * 确认发货
     */
    private void confirmShipment(String orderJson) {
        if (RxDataTool.isNullString(orderId)) {
            RxToast.showToast("未获取到订单信息");
            return;

        }
        OkGo.<String>post(Urls.confirmShipment)//
                .tag(this)//
                .params("orderJson", orderJson)
                .params("orderId", orderId)
                .params("sendType", sendType)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("suc")) {
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_PENDING_DELIVERY = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        RxToast.showToast(data.optString("message"));
                                        finish();
                                        if (activityId == SellOrderDetailAty.activityId) {
                                            RxActivityTool.finishActivity(SellOrderDetailAty.class);
                                        }
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

    private void choosePickupWay(View view) {
        tvKuaiDi.setSelected(false);
        tvZiQu.setSelected(false);
        tvShangMen.setSelected(false);
        view.setSelected(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && data != null) {//扫描运单号
            String code = data.getStringExtra("code");
            tvExpressNum.setText(code);
            if (!RxDataTool.isNullString(code)) {
                tvExpressNum.setSelection(code.length());
            }

        }
        if (requestCode == REQUEST_CODE_EXPRESS && data != null) {//选择快递公司
            expressInfoModel = (CompanyData) data.getSerializableExtra("expressInfoModel");
            if (expressInfoModel != null) {
                String netName = expressInfoModel.netName;
                tvExpressName.setText(netName);
            }

        }
    }
}
