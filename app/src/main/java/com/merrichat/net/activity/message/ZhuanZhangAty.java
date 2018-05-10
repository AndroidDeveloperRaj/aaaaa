package com.merrichat.net.activity.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.my.mywallet.MyWalletActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.CashierInputFilter;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.MusicUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_RED_PACKAGE_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_ZHUANZHANG_CODE;

/**
 * Created by amssy on 2018/1/31.
 * 讯美币转账
 */

public class ZhuanZhangAty extends BaseActivity {

    public static final int activityId = MiscUtil.getActivityId();

    /**
     * 充值type请求码
     */
    public final static int REQUEST_PAY_STATUS = 1;
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
     * ￥
     */
    @BindView(R.id.tv_money_tip)
    TextView tvMonetTip;
    /**
     * 元
     */
    @BindView(R.id.tv_yuan)
    TextView tvYuan;

    /**
     * 输入转账讯美币
     */
    @BindView(R.id.et_money)
    EditText etMoney;

    /**
     * 输入想要说的话
     */
    @BindView(R.id.et_write_text)
    EditText etWriteText;

    /**
     * 输入讯美币后  在下面放大显示
     */
    @BindView(R.id.tv_end_money)
    TextView tvEndMoney;
    /**
     * tvBtn
     */
    @BindView(R.id.tv_btn)
    TextView tvBtn;
    /**
     * 转账按钮
     */
    @BindView(R.id.ll_fa_red_bao)
    LinearLayout llFaRedBao;

    private String collectMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelopes);
        collectMemberId = getIntent().getStringExtra("collectMemberId");
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        tvTitleText.setText("讯美币转账");
        tvBtn.setText("转账");
        tvYuan.setText("讯美币");
        etWriteText.setHint("添加转账说明，最多10个字");
        tvMonetTip.setVisibility(View.GONE);
        tvEndMoney.setText("0.00讯美币");
        final DecimalFormat df = new DecimalFormat("#0.00");
        InputFilter[] filters = {new CashierInputFilter()};
        etMoney.setFilters(filters);
        etMoney.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && StringUtil.isNumber(s.toString())) {
                    // 输入后的监听
                    tvEndMoney.setText("");
                    tvEndMoney.setText(df.format(Double.parseDouble(s.toString())) + "讯美币");
                } else {
                    tvEndMoney.setText("0.00讯美币");
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.ll_fa_red_bao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_fa_red_bao:
                String money = tvEndMoney.getText().toString().trim();
                if (!TextUtils.isEmpty(money)) {
                    money = money.replace("讯美币", "");
                    if (StringUtil.isNumber(money)) {
                        double dMoney = Double.parseDouble(money);
                        if (dMoney > 0) {
                            String content = etWriteText.getText().toString();
                            if (TextUtils.isEmpty(content)) {
                                content = "恭喜发财，大吉大利！";
                            }
                            getIsCheckPWD(dMoney, content);
                        } else {
                            GetToast.showToast(cnt, "请输入转账金额");
                        }
                    }
                }
                break;
        }
    }


    /**
     * 查询是否设置支付密码
     */
    private void getIsCheckPWD(final double dMoney, final String content) {
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
                                    CommomDialog dialog = new CommomDialog(cnt, R.style.dialog, "为保证账户安全，设置支付密码后才可发红包", new CommomDialog.OnCloseListener() {
                                        @Override
                                        public void onClick(Dialog dialog, boolean confirm) {
                                            if (confirm) {
                                                dialog.dismiss();
                                                startActivity(new Intent(cnt, MyWalletActivity.class));
                                            } else {
                                                dialog.dismiss();
                                            }
                                        }
                                    }).setTitle("提示").setNegativeButton("取消").setPositiveButton("去设置");
                                    dialog.show();
                                } else {//已设置
                                    getBalance(dMoney, content);
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
     * 查询余额、红包和金币
     */
    private void getBalance(final double redMoney, final String content) {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("maxMoney", "")
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback((Activity) cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.optBoolean("success")) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    double giftBalance = data.optDouble("giftBalance");//讯美币余额
                                    startActivityForResult(new Intent(cnt, SelectPayTypeAty.class)
                                            .putExtra("money", redMoney)
                                            .putExtra("activityId", activityId)
                                            .putExtra("cashBalance", giftBalance)
                                            .putExtra("collectMemberId", collectMemberId)
                                            .putExtra("content", content), REQUEST_PAY_STATUS);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(cnt, "请求服务器失败，请稍后重试");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAY_STATUS) {
            if (data != null) {
                String tid = data.getStringExtra("tid");
                String redPackageContent = data.getStringExtra("redPackageContent");
                String money = data.getStringExtra("money");
                Intent intent = new Intent();
                intent.putExtra("tid", tid);
                intent.putExtra("money", money);
                intent.putExtra("redPackageContent", redPackageContent);
                setResult(REQUEST_ZHUANZHANG_CODE, intent);
                finish();
            }
        }
    }
}
