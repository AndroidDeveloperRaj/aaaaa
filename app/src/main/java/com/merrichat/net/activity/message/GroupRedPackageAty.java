package com.merrichat.net.activity.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.my.mywallet.MyWalletActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.CashierInputFilter;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.RedEnvelopesActivity.REQUEST_PAY_STATUS;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_RED_PACKAGE_CODE;

/**
 * Created by amssy on 2018/2/2.
 * 群红包
 */

public class GroupRedPackageAty extends AppCompatActivity {
    public static final int activityId = MiscUtil.getActivityId();
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

    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;

    @BindView(R.id.ll_type)
    LinearLayout llType;

    @BindView(R.id.iv_xianjin_type)
    ImageView ivXianjinType;
    @BindView(R.id.tv_xianjin_type)
    TextView tvXianjinType;
    @BindView(R.id.ll_xianjin_type)
    LinearLayout llXianjinType;
    @BindView(R.id.iv_meibi_type)
    ImageView ivMeibiType;
    @BindView(R.id.tv_meibi_type)
    TextView tvMeibiType;
    @BindView(R.id.ll_meibi_type)
    LinearLayout llMeibiType;


    @BindView(R.id.et_total)
    EditText etTotal;
    @BindView(R.id.tv_yuan)
    TextView tvYuan;

    @BindView(R.id.tv_nums_tip)
    TextView tvNumsTip;
    @BindView(R.id.tv_end_tip)
    TextView tvEndTip;

    @BindView(R.id.et_num)
    EditText etNum;

    @BindView(R.id.et_write_text)
    EditText etWriteText;

    @BindView(R.id.tv_yuan_type)
    TextView tvYuanType;
    @BindView(R.id.tv_end_money)
    TextView tvEndMoney;

    @BindView(R.id.tv_btn)
    TextView tvBtn;
    @BindView(R.id.tv_mid_tip)
    TextView tvMidTip;

    private String[] mTitles = {"手气红包", "拉人红包"};

    private Context mContext;
    private int redPkgType = 0;//0"手气红包", 1"拉人红包"
    private int coinType = 0;//0现金,1讯美币
    private int memberNum;//群人数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setBackgroundDrawableResource(R.color.background);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 38);
        setContentView(R.layout.activity_group_red_package);
        ButterKnife.bind(this);
        mContext = this;
        initTab();
    }

    private void initTab() {
        memberNum = getIntent().getIntExtra("memberNum", 0);
        tvTitleText.setText("群红包");
        tvMidTip.setText("本群共" + memberNum + "人");
        llType.setVisibility(View.GONE);
        tvYuan.setText("讯美币");
        tvBtn.setText("塞讯美币进红包");
        tvNumsTip.setText("红包个数");
        tvEndTip.setText("个");
        etWriteText.setHint("恭喜发财，大吉大利！");
        tvYuanType.setVisibility(View.GONE);
        sgTablayout.setVisibility(View.VISIBLE);
        llType.setVisibility(View.GONE);
        etTotal.setSelection(etTotal.getText().toString().length());
        etNum.setSelection(etNum.getText().toString().length());
        sgTablayout.setTabData(mTitles);
        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                etWriteText.setText("");
                etNum.setText("");
                etTotal.setText("");
                switch (position) {
                    case 0://0"手气红包"
                        redPkgType = 0;
                        llType.setVisibility(View.GONE);
                        tvYuan.setText("讯美币");
                        tvNumsTip.setText("红包个数");
                        tvEndTip.setText("个");
                        tvBtn.setText("塞讯美币进红包");
                        etWriteText.setHint("恭喜发财，大吉大利！");
                        tvYuanType.setVisibility(View.GONE);
                        tvMidTip.setText("本群共" + memberNum + "人");
                        break;
                    case 1:// 1"拉人红包"
                        redPkgType = 1;
                        tvNumsTip.setText("邀请人数");
                        tvEndTip.setText("人");
                        llType.setVisibility(View.VISIBLE);
                        if (coinType == 0) {
                            tvYuan.setText("现金");
                            tvYuanType.setVisibility(View.VISIBLE);
                        } else if (coinType == 1) {
                            tvYuan.setText("讯美币");
                            tvYuanType.setVisibility(View.GONE);
                        }
                        tvBtn.setText("塞钱进红包");
                        tvMidTip.setText("邀请人和进群都将获得红包奖励，不限制领取个数~");
                        etWriteText.setHint("大家踊跃拉人进群！");
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        final DecimalFormat df = new DecimalFormat("#0.00");
        InputFilter[] filters = {new CashierInputFilter()};
        etTotal.setFilters(filters);
        etTotal.addTextChangedListener(new TextWatcher() {

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
                    tvEndMoney.setText(df.format(Double.parseDouble(s.toString())));
                } else {
                    tvEndMoney.setText("0.00");
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.ll_xianjin_type, R.id.ll_meibi_type, R.id.tv_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_xianjin_type:
                etWriteText.setText("");
                etNum.setText("");
                etTotal.setText("");
                coinType = 0;
                tvYuan.setText("现金");
                tvYuanType.setVisibility(View.VISIBLE);
                ivXianjinType.setImageResource(R.mipmap.accept_2x_true);
                tvXianjinType.setTextColor(getResources().getColor(R.color.normal_red));
                ivMeibiType.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                tvMeibiType.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.ll_meibi_type:
                etWriteText.setText("");
                etNum.setText("");
                etTotal.setText("");
                coinType = 1;
                tvYuan.setText("讯美币");
                tvYuanType.setVisibility(View.GONE);
                ivMeibiType.setImageResource(R.mipmap.accept_2x_true);
                tvMeibiType.setTextColor(getResources().getColor(R.color.normal_red));
                ivXianjinType.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                tvXianjinType.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_btn:
                redPackage();
                break;
        }
    }

    private void redPackage() {
        if (TextUtils.isEmpty(etTotal.getText().toString())) {
            RxToast.showToast("请输入总金额");
            return;
        }
        if (TextUtils.isEmpty(etNum.getText().toString())) {
            RxToast.showToast("请输入邀请人数");
            return;
        }
        double total = Double.parseDouble(etTotal.getText().toString());
        int num = Integer.parseInt(etNum.getText().toString());

        if (redPkgType == 0) {//0"手气红包"
            if (total > 888) {
                RxToast.showToast("红包不能大于888");
                return;
            }
            if (total < 1) {
                RxToast.showToast("总金额不能少于1");
                return;
            }
            if (num < 1) {
                RxToast.showToast("手气红包个数不能少于1");
                return;
            }
            if (num > total) {
                RxToast.showToast("手气红包个数不能大于总金额");
                return;
            }
        } else if (redPkgType == 1) {// 1"拉人红包"
            if (total < 1) {
                RxToast.showToast("拉人红包最少为1");
                return;
            }
            if (num < 1) {
                RxToast.showToast("邀请人数最小为1");
                return;
            }
            if (total / num * 2 < 0.01) {
                RxToast.showToast("单个红包不能少于0.01");
                return;
            }
        }
        String content = etWriteText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            if (redPkgType == 0) {
                content = "恭喜发财，大吉大利！";
            } else if (redPkgType == 1) {
                content = "大家踊跃拉人进群！";
            }

        }
        int hairType = -1;
        if (redPkgType == 0) {
            hairType = 13;
        } else {
            hairType = 14;
            num = num * 2;
        }
        getIsCheckPWD(total, num, content, hairType);
    }

    /**
     * 查询是否设置支付密码
     */
    private void getIsCheckPWD(final double dMoney, final int redNum, final String content, final int hairType) {
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
                                    CommomDialog dialog = new CommomDialog(mContext, R.style.dialog, "为保证账户安全，设置支付密码后才可发红包", new CommomDialog.OnCloseListener() {
                                        @Override
                                        public void onClick(Dialog dialog, boolean confirm) {
                                            if (confirm) {
                                                dialog.dismiss();
                                                startActivity(new Intent(mContext, MyWalletActivity.class));
                                            } else {
                                                dialog.dismiss();
                                            }
                                        }
                                    }).setTitle("提示").setNegativeButton("取消").setPositiveButton("去设置");
                                    dialog.show();
                                } else {//已设置
                                    getBalance(dMoney, redNum, content, hairType);
                                }
                            } else {
                                GetToast.showToast(mContext, "" + jsonObject.optString("error_msg"));
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
    private void getBalance(final double redMoney, final int redNum, final String content, final int hairType) {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("maxMoney", "")
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback((Activity) mContext) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                double cashBalance = 0.00;
                                if (redPkgType == 0) {
                                    //手气红包讯美币
                                    cashBalance = data.optDouble("giftBalance");//讯美币余额
                                    if (cashBalance < redMoney) {
                                        RxToast.showToast("讯美币余额不足！");
                                        return;
                                    }
                                } else if (redPkgType == 1) {
                                    //拉人红包
                                    if (coinType == 0) {//现金
                                        cashBalance = data.optDouble("cashBalance");//账户余额
                                        if (cashBalance < redMoney) {

                                        }
                                    } else if (coinType == 1) {//讯美币
                                        cashBalance = data.optDouble("giftBalance");//讯美币余额
                                        if (cashBalance < redMoney) {
                                            RxToast.showToast("讯美币余额不足！");
                                            return;
                                        }
                                    }
                                }
                                startActivityForResult(new Intent(mContext, SelectPayTypeAty.class)
                                        .putExtra("money", redMoney)
                                        .putExtra("activityId", activityId)
                                        .putExtra("redNum", redNum)
                                        .putExtra("cashBalance", cashBalance)
                                        .putExtra("group", getIntent().getStringExtra("group"))
                                        .putExtra("groupId", getIntent().getStringExtra("groupId"))
                                        .putExtra("hairType", hairType)
                                        .putExtra("coinType", coinType)
                                        .putExtra("content", content), REQUEST_PAY_STATUS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "请求服务器失败，请稍后重试");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAY_STATUS) {
            if (data != null) {
                String tid = data.getStringExtra("tid");
                String redPackageContent = data.getStringExtra("redPackageContent");
                Intent intent = new Intent();
                intent.putExtra("tid", tid);
                intent.putExtra("redPackageContent", redPackageContent);
                if (redPkgType == 0) {
                    intent.putExtra("hairType", 13);
                } else {
                    intent.putExtra("hairType", 14);
                }
                setResult(REQUEST_RED_PACKAGE_CODE, intent);
                finish();
            }
        }
    }
}
