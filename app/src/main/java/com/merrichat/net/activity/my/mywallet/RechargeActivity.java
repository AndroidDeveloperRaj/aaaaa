package com.merrichat.net.activity.my.mywallet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.setting.IdentityVerificationAty;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ACacheUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.PayFragment;
import com.merrichat.net.view.PayPwdView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by amssy on 17/7/1.
 * 提现
 */
public class RechargeActivity extends BaseActivity implements PayPwdView.InputCallBack {

    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;

    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;


    /**
     * 输入金额
     */
    @BindView(R.id.editText_money)
    EditText editTextMoney;

    /**
     * 提现按钮
     */
    @BindView(R.id.button_recharge)
    Button button_recharge;


    /**
     * 提现方式
     */
    @BindView(R.id.rl_withdrawal_method)
    RelativeLayout rlWithdrawalMethod;

    @BindView(R.id.tv_withdrawal_method)
    TextView tvWithdrawalMethod;


    /**
     * 现金余额
     */
    @BindView(R.id.tv_wallet_money)
    TextView tvWalletMoney;
    @BindView(R.id.lay_money1)
    LinearLayout layMoney1;
    @BindView(R.id.tv_money_in)
    TextView tvMoneyIn;
    @BindView(R.id.tv_money_out)
    TextView tvMoneyOut;
    @BindView(R.id.lay_money2)
    LinearLayout layMoney2;


    /**
     * 输入的提现金额
     */
    private double editMoney;


    /**
     * 提现方式
     * 1：微信
     * 2：支付宝
     */
    private int withdrawalMethodFlag = 0;


    /**
     * 现金余额
     */
    private double cashBalance = 0.0;


    /**
     * 支付宝账号id
     */
    private String aliPayAcountId = "";


    /**
     * 微信账号id
     */
    private String weixinAccountId = "";


    /**
     * 输入支付密码弹框
     */
    private PayFragment fragmentDialog;
    private CommomDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        initTitleBar();
        initLayout();
    }


    private void initTitleBar() {
        tvTitleText.setText("提现");
    }

    private void initLayout() {
        //未输入金额显示 .提现手续费：1%，输入金额后自动算出来实际到账金额在下方小字显示，输入金额就不会显示可提现金额一行了，替换为实际到账字样
        layMoney1.setVisibility(View.VISIBLE);
        layMoney2.setVisibility(View.GONE);

        editTextMoney.setSelection(editTextMoney.getText().length());//控制光标在金额后面

        editTextMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editTextMoney.setText(s);
                        editTextMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editTextMoney.setText(s);
                    editTextMoney.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editTextMoney.setText(s.subSequence(0, 1));
                        editTextMoney.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                editMoney = 0.0;
                try {
                    editMoney = Double.parseDouble(str);
                    if (editMoney == 10000) {
                        editTextMoney.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    } else if (editMoney > 10000) {
                        String strSub = String.valueOf(editMoney);
                        editTextMoney.setText(strSub.substring(0, 4));
                        editTextMoney.setSelection(4);
                        editMoney = Double.parseDouble(editTextMoney.getText().toString());
                    } else {
                        editTextMoney.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(editTextMoney.getText().toString())) {
                    //计算提现手续费
                    double serviceCharge = editMoney * 0.01;
                    //实际到账
                    double practical = StringUtil.moneySubtract(String.valueOf(editMoney), String.valueOf(serviceCharge));

                    tvMoneyIn.setText("¥ " + practical);
                    tvMoneyOut.setText("¥ " + serviceCharge);
                    layMoney1.setVisibility(View.GONE);
                    layMoney2.setVisibility(View.VISIBLE);
                }else {
                    layMoney2.setVisibility(View.GONE);
                    layMoney1.setVisibility(View.VISIBLE);
                }
            }
        });

        queryWalletInfo();

    }


    /**
     * 查询现金余额
     */
    private void queryWalletInfo() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("maxMoney", "")
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.optBoolean("success")) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    cashBalance = data.optDouble("cashBalance");//账户余额

                                    tvWalletMoney.setText("￥" + cashBalance);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.button_recharge, R.id.rl_withdrawal_method})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.button_recharge://提现
                tiXianMoney();
                break;

            case R.id.rl_withdrawal_method://选择支付方式
                startActivityForResult(new Intent(cnt, WithdrawalMethodActivity.class), WithdrawalMethodActivity.activityid);
                break;
        }
    }


    /**
     * 查询是否设置支付密码
     */
    private void getIsCheckPWD() {
        OkGo.<String>post(Urls.checkAccountPwdIsExist)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.getJSONObject("data").optString("isSetPassword").equals("0")) {//0：没设置
                                    dialog = new CommomDialog(cnt, R.style.dialog, "请先设置支付密码才可以进行现金提现", new CommomDialog.OnCloseListener() {
                                        @Override
                                        public void onClick(Dialog dialog, boolean confirm) {
                                            if (confirm) {
                                                dialog.dismiss();
                                                //去设置支付密码
                                                MyWalletActivity.isSetPassword = false;
                                                startActivity(new Intent(RechargeActivity.this, PasswardActivity.class));
                                            } else {

                                            }
                                        }
                                    }).setTitle("温馨提示");
                                    dialog.setPositiveButton("去设置");
                                    dialog.show();
                                } else {//已设置
                                    if (cashBalance < editMoney) {
                                        GetToast.useString(cnt, "余额不足");
                                        return;
                                    }

                                    if (withdrawalMethodFlag == 1) {
                                        weixinAccountId = ACacheUtils.getWeChatAccountId(cnt);
                                        if (TextUtils.isEmpty(weixinAccountId)) {
                                            GetToast.useString(cnt, "您还未绑定微信,请先绑定微信");
                                            return;
                                        }
                                    }

                                    if (withdrawalMethodFlag == 2) {
                                        aliPayAcountId = ACacheUtils.getAliPayAcountId(cnt);
                                        if (TextUtils.isEmpty(aliPayAcountId)) {
                                            GetToast.useString(cnt, "您还未绑定支付宝账户，请先绑定支付宝账户");
                                            return;
                                        }
                                    }
                                    //输入支付密码
                                    Bundle bundle = new Bundle();
                                    bundle.putString(PayFragment.EXTRA_CONTENT, "提现：¥ " + editMoney);
                                    fragmentDialog = new PayFragment();
                                    fragmentDialog.setArguments(bundle);
                                    fragmentDialog.setPaySuccessCallBack(RechargeActivity.this);
                                    fragmentDialog.show(getSupportFragmentManager(), "Pay");

                                }
                            } else {
                                GetToast.showToast(cnt, "" + jsonObject.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 提现
     */
    private void tiXianMoney() {
        if (withdrawalMethodFlag == 0) {
            GetToast.useString(cnt, "请选择提现方式");
            return;
        }

        if (editMoney == 0.00 || TextUtils.isEmpty(editTextMoney.getText().toString())) {
            GetToast.useString(cnt, "请输入提现金额");
            return;
        }
        queryRealNameVerfyStatus();
    }


    /**
     * 提交提现申请
     */
    private void withdrawCreate() {
        OkGo.<String>post(Urls.withdrawCreate)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("uid", UserModel.getUserModel().getMemberId())
                .params("totalAmount", editMoney)
                .params("type", withdrawalMethodFlag)
                .params("aliAcountId", aliPayAcountId)
                .params("remark", "")
                .params("bankName", "")
                .params("cardId", "")
                .params("bankCardId", "")
                .params("weixinAccountId", weixinAccountId)
                .params("platformId", "100005")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                GetToast.useString(cnt, "申请提现成功");
                                MyEventBusModel eventBusModel = new MyEventBusModel();
                                eventBusModel.FINISH_WALLET_BALANCE = true;
                                EventBus.getDefault().post(eventBusModel);
                                finish();
                                Intent intent = new Intent(RechargeActivity.this, TiXianDetailActivity.class);
                                intent.putExtra("withdrawalMethodFlag", withdrawalMethodFlag);
                                intent.putExtra("price", editMoney);
                                startActivity(intent);
                            } else {
                                String error_msg = jsonObject.optString("error_msg");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(cnt, error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == WithdrawalMethodActivity.activityid) {
                int withdrawalMethod = data.getIntExtra("withdrawalMethod", 0);
                if (withdrawalMethod == 1) {
                    withdrawalMethodFlag = 1;
                    tvWithdrawalMethod.setText("微信");
                } else if (withdrawalMethod == 2) {
                    withdrawalMethodFlag = 2;
                    tvWithdrawalMethod.setText("支付宝");
                }
            }
        }
    }


    @Override
    public void onInputFinish(String result) {
        getValidatePwd(result);
    }


    /**
     * 验证密码是否正确
     */
    private void getValidatePwd(String result) {
        OkGo.<String>post(Urls.validatePassword)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("password", result)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if ("1".equals(jsonObject.optJSONObject("data").optString("isRight"))) {
                                fragmentDialog.dismiss();
                                withdrawCreate();
                            } else {
                                GetToast.showToast(cnt, "支付密码错误,请重试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 金额验证
     */
    private void checkWithdrawStatus() {
        OkGo.<String>post(Urls.checkWithdrawStatus)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("amount", editMoney)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (editMoney < 100 || editMoney % 100 != 0) {
                                    GetToast.useString(cnt, "提现金额需为100的整数倍");
                                    return;
                                }
                                getIsCheckPWD();
                            } else {
                                String error_msg = jsonObject.optString("error_msg");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(cnt, error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 查询实名认证状态
     */
    private void queryRealNameVerfyStatus() {
        OkGo.<String>post(Urls.queryRealNameVerfyStatus)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("accountId", UserModel.getUserModel().getAccountId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                //状态码0:尚未实名认证 1:已实名认证
                                int realNameStatus = jsonObject.optJSONObject("data").optInt("realNameStatus");
                                switch (realNameStatus) {
                                    case 0:
                                        dialog = new CommomDialog(cnt, R.style.dialog, "账户未进行实名认证无法提现，请进行认证后提现", new CommomDialog.OnCloseListener() {
                                            @Override
                                            public void onClick(Dialog dialog, boolean confirm) {
                                                if (confirm) {
                                                    RxActivityTool.skipActivity(RechargeActivity.this, IdentityVerificationAty.class);
                                                    dialog.dismiss();
                                                } else {

                                                }
                                            }
                                        }).setTitle("温馨提示");
                                        dialog.setPositiveButton("去认证");
                                        dialog.show();
                                        break;
                                    case 1:
                                        //金额／提现次数／最高金额／最低金额
                                        checkWithdrawStatus();
                                        break;
                                }
                            } else {
                                String error_msg = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(cnt, error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

}
