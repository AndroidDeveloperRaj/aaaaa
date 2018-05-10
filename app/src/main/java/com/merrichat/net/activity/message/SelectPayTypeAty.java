package com.merrichat.net.activity.message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.KeyBoardAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.aLiPayUtils.AuthResult;
import com.merrichat.net.utils.aLiPayUtils.PayResult;
import com.merrichat.net.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.RedEnvelopesActivity.REQUEST_PAY_STATUS;

/**
 * Created by amssy on 2017/12/20.
 * 选择支付红包类型
 */

public class SelectPayTypeAty extends Activity implements AdapterView.OnItemClickListener {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.textAmount)
    TextView textAmount;
    @BindView(R.id.textShouxuFei)
    TextView textShouxuFei;
    @BindView(R.id.tv_over_content)
    TextView tvOverContent;
    @BindView(R.id.tv_wechat_pay)
    TextView tvWechatPay;
    @BindView(R.id.tv_alipay)
    TextView tvAlipay;
    @BindView(R.id.tv_pass1)
    TextView tvPass1;
    @BindView(R.id.img_pass1)
    ImageView imgPass1;
    @BindView(R.id.tv_pass2)
    TextView tvPass2;
    @BindView(R.id.img_pass2)
    ImageView imgPass2;
    @BindView(R.id.tv_pass3)
    TextView tvPass3;
    @BindView(R.id.img_pass3)
    ImageView imgPass3;
    @BindView(R.id.tv_pass4)
    TextView tvPass4;
    @BindView(R.id.img_pass4)
    ImageView imgPass4;
    @BindView(R.id.tv_pass5)
    TextView tvPass5;
    @BindView(R.id.img_pass5)
    ImageView imgPass5;
    @BindView(R.id.tv_pass6)
    TextView tvPass6;
    @BindView(R.id.img_pass6)
    ImageView imgPass6;
    @BindView(R.id.tv_forgetPwd)
    TextView tvForgetPwd;
    @BindView(R.id.linear_pass)
    LinearLayout linearPass;
    @BindView(R.id.lay_wechat_alipay)
    LinearLayout layWechatAlipay;

    @BindView(R.id.ll_pwd)
    LinearLayout llPwd;
    @BindView(R.id.sv)
    ScrollView sv;
    @BindView(R.id.v_temp)
    View vTemp;
    @BindView(R.id.gv_keybord)
    GridView gvKeybord;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.layoutBack)
    RelativeLayout layoutBack;
    @BindView(R.id.rl_keyboard)
    RelativeLayout rlKeyboard;

    private Context mContext;
    //键盘数字的集合
    private ArrayList<Map<String, String>> valueList;
    //用数组保存6个TextView
    private TextView[] tvList;
    //支付类型
    private TextView[] tvPayList;
    //用数组保存6个ImageView
    private ImageView[] imgList;
    //用于记录当前输入密码格位置
    private int currentIndex = -1;

    private double redMoney;
    private String redPackageContent = "恭喜发财，大吉大利！";
    private double cashBalance;//账户余额/讯美币余额
    private String collectMemberId;
    private int payType = 0;//0余额支付，1微信支付，2支付宝支付
    private String strPayType = "balance";//"balance"余额支付，"weixin"微信支付，"alipay"支付宝支付
    private IWXAPI api;
    private String tid;
    private int fromActivityId;

    //群红包
    private String groupId;
    private String group;
    private int redPgeNum;
    private int hairType;
    private int coinType;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("tid", tid);
                        intent.putExtra("redPackageContent", redPackageContent);
                        setResult(REQUEST_PAY_STATUS, intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(mContext,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(mContext,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pay_type);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        redMoney = getIntent().getDoubleExtra("money", 0.00);
        fromActivityId = getIntent().getIntExtra("activityId", -1);
        cashBalance = getIntent().getDoubleExtra("cashBalance", 0.00);
        redPackageContent = getIntent().getStringExtra("content");
        collectMemberId = getIntent().getStringExtra("collectMemberId");


        groupId = getIntent().getStringExtra("groupId");
        group = getIntent().getStringExtra("group");
        redPgeNum = getIntent().getIntExtra("redNum", 0);
        hairType = getIntent().getIntExtra("hairType", 0);
        coinType = getIntent().getIntExtra("coinType", 0);
        inintView();
    }

    private void inintView() {
        api = WXAPIFactory.createWXAPI(this, "" + R.string.weixin_app_id, false);
        api.registerApp("" + R.string.weixin_app_id);
        tvList = new TextView[]{tvPass1, tvPass2, tvPass3, tvPass4, tvPass5, tvPass6};
        tvPayList = new TextView[]{tvOverContent, tvWechatPay, tvAlipay};
        imgList = new ImageView[]{imgPass1, imgPass2, imgPass3, imgPass4, imgPass5, imgPass6};
        if (fromActivityId == ZhuanZhangAty.activityId) {
            strPayType = "gift";
            tvOverContent.setText("讯美币余额(剩余￥" + big2(cashBalance) + ")");
            layWechatAlipay.setVisibility(View.GONE);
        } else if (fromActivityId == GroupRedPackageAty.activityId) {
            if (hairType == 13) {
                strPayType = "gift";
                tvOverContent.setText("讯美币余额(剩余￥" + big2(cashBalance) + ")");
                layWechatAlipay.setVisibility(View.GONE);
            } else if (hairType == 14) {
                if (coinType == 0) {
                    layWechatAlipay.setVisibility(View.VISIBLE);
                    if (redMoney > cashBalance) {
                        tvOverContent.setText("现金余额(剩余￥" + big2(cashBalance) + ")，余额不足");
                    } else {
                        tvOverContent.setText("现金余额(剩余￥" + big2(cashBalance) + ")");
                    }
                } else if (coinType == 1) {
                    strPayType = "gift";
                    tvOverContent.setText("讯美币余额(剩余￥" + big2(cashBalance) + ")");
                    layWechatAlipay.setVisibility(View.GONE);
                }
            }
        } else {
            layWechatAlipay.setVisibility(View.VISIBLE);
            if (redMoney > cashBalance) {
                tvOverContent.setText("现金余额(剩余￥" + big2(cashBalance) + ")，余额不足");
            } else {
                tvOverContent.setText("现金余额(剩余￥" + big2(cashBalance) + ")");
            }
        }
        textAmount.setText("￥" + big2(redMoney));
        setSVNoScroll();
        initNumberKeyBoard();
        setOnFinishInput();

        //弹出输入键盘
        svPullToDown();
        rlKeyboard.setVisibility(View.VISIBLE);
        //弹出输入键盘
    }

    private void initNumberKeyBoard() {
        valueList = new ArrayList<>();
        // 初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }
        KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(mContext, R.layout.grid_item_virtual_keyboard, valueList);
        gvKeybord.setAdapter(keyBoardAdapter);
        gvKeybord.setOnItemClickListener(this);
    }

    @OnClick({R.id.tv_pass1, R.id.tv_pass2, R.id.tv_pass3, R.id.tv_pass4, R.id.tv_pass5, R.id.tv_pass6,
            R.id.img_pass1, R.id.img_pass2, R.id.img_pass3, R.id.img_pass4, R.id.img_pass5, R.id.img_pass6,
            R.id.layoutBack, R.id.img_cancel, R.id.tv_over_content, R.id.tv_wechat_pay, R.id.tv_alipay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_pass1:
            case R.id.tv_pass2:
            case R.id.tv_pass3:
            case R.id.tv_pass4:
            case R.id.tv_pass5:
            case R.id.tv_pass6:
            case R.id.img_pass1:
            case R.id.img_pass2:
            case R.id.img_pass3:
            case R.id.img_pass4:
            case R.id.img_pass5:
            case R.id.img_pass6:
                svPullToDown();
                rlKeyboard.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutBack:
                svPullToTop();
                rlKeyboard.setVisibility(View.GONE);
                break;
            case R.id.img_cancel:
                finish();
                break;
            case R.id.tv_over_content:
                strPayType = "balance";
                changePayTypeUI(0);

                break;
            case R.id.tv_wechat_pay:
                strPayType = "weixin";
                changePayTypeUI(1);
                if (!StringUtil.isWeixinAvilible(this)) {
                    GetToast.useString(this, "你还未安装微信");
                } else {
                    if (fromActivityId == GroupRedPackageAty.activityId) {
                        hairGroupRedPge();//群发红包
                    } else {
                        hairReds();//单聊发红包
                    }
                }
                break;
            case R.id.tv_alipay:
                strPayType = "alipay";
                changePayTypeUI(2);
                if (fromActivityId == GroupRedPackageAty.activityId) {
                    hairGroupRedPge();//群发红包
                } else {
                    hairReds();//单聊发红包
                }
                break;
        }
    }

    /**
     * ScrollView 滚动到顶部
     */
    private void svPullToTop() {
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    /**
     * ScrollView 滚动到底部
     */
    private void svPullToDown() {
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * ScrollView 不能手动滑动
     */
    private void setSVNoScroll() {
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //不能滑动
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 11 && position != 9) {    //点击0~9按钮
            if (currentIndex >= -1 && currentIndex < 5) {      //判断输入位置————要小心数组越界
                ++currentIndex;
                tvList[currentIndex].setText(valueList.get(position).get("name"));
                tvList[currentIndex].setVisibility(View.INVISIBLE);
                imgList[currentIndex].setVisibility(View.VISIBLE);
            }
        } else {
            if (position == 11) {      //点击退格键
                if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                    tvList[currentIndex].setText("");
                    tvList[currentIndex].setVisibility(View.VISIBLE);
                    imgList[currentIndex].setVisibility(View.INVISIBLE);
                    currentIndex--;
                }
            }
        }
    }

    //设置监听方法，在第6位输入完成后触发
    public void setOnFinishInput() {
        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    String strPassword = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }
                    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                    inputPassword(strPassword);
                }
            }
        });
    }

    /**
     * double大数据在前端不以科学计数方法显示
     *
     * @param d
     * @return
     */
    private static String big2(double d) {
        BigDecimal d1 = new BigDecimal(Double.toString(d));
        BigDecimal d2 = new BigDecimal(Integer.toString(1));
        // 四舍五入,保留2位小数
        return d1.divide(d2, 2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 发红包
     */
    private void hairReds() {
        if (payType == 0) {
            if (redMoney > cashBalance) {
                RxToast.showToast("余额不足！");
                return;
            }
        }
        OkGo.<String>post(Urls.hairReds)
                .params("hairMemberId", UserModel.getUserModel().getMemberId())
                .params("collectMemberId", collectMemberId)
                .params("csCode", strPayType)
                .params("turnGold", big2(redMoney))
                .params("blessings", redPackageContent)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        body:hairMemberId=315917552893952&collectMemberId=316069722243072&turnGold=1.00&blessings=恭喜发财，大吉大利！
                        //body:{"data":{"tid":"159469438823068672","msg":"","success":"true"},"success":true}
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                tid = data.optString("tid");
                                if (payType == 0) {
                                    Intent intent = new Intent();
                                    intent.putExtra("tid", tid);
                                    intent.putExtra("redPackageContent", redPackageContent);
                                    setResult(REQUEST_PAY_STATUS, intent);
                                    finish();
                                } else if (payType == 1) {
                                    WXPayEntryActivity.setPayType(2);//发红包
                                    WeChatPay(data.toString());//调起微信支付
                                } else if (payType == 2) {
                                    String sign = data.optString("sign");
                                    aLiPay(sign);
                                }

                            } else {
                                GetToast.showToast(mContext, jsonObject.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "服务器开小差了，请稍后重试");
                    }
                });
    }

    /**
     * 转账
     */
    private void turnAccount() {
        OkGo.<String>post(Urls.turnAccount)
                .params("hairMemberId", UserModel.getUserModel().getMemberId())
                .params("collectMemberId", collectMemberId)
                .params("csCode", strPayType)
                .params("turnGold", big2(redMoney))
                .params("blessings", redPackageContent)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        body:hairMemberId=315917552893952&collectMemberId=316069722243072&turnGold=1.00&blessings=恭喜发财，大吉大利！
                        //body:{"data":{"tid":"159469438823068672","msg":"","success":"true"},"success":true}
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                tid = data.optString("tid");
                                Intent intent = new Intent();
                                intent.putExtra("tid", tid);
                                intent.putExtra("money", big2(redMoney));
                                intent.putExtra("redPackageContent", redPackageContent);
                                setResult(REQUEST_PAY_STATUS, intent);
                                finish();
                            } else {
                                RxToast.showToast("转账失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "服务器开小差了，请稍后重试");
                    }
                });
    }

    /**
     * 群红包
     */
    private void hairGroupRedPge() {
        OkGo.<String>post(Urls.hairGroupRedPge)
                .params("hairMemberId", UserModel.getUserModel().getMemberId())
                .params("groupId", groupId)
                .params("group", group)
                .params("csCode", strPayType)
                .params("turnGold", big2(redMoney))
                .params("redPgeNum", redPgeNum)
                .params("blessings", redPackageContent)
                .params("hairType", hairType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        body:hairMemberId=315917552893952&collectMemberId=316069722243072&turnGold=1.00&blessings=恭喜发财，大吉大利！
                        //body:{"data":{"tid":"159469438823068672","msg":"","success":"true"},"success":true}
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");

                            if (jsonObject.optBoolean("success")) {
                                tid = data.optString("tid");
                                if (hairType == 13) {//手气红包讯美币
                                    Intent intent = new Intent();
                                    intent.putExtra("tid", tid);
                                    intent.putExtra("money", big2(redMoney));
                                    intent.putExtra("redPackageContent", redPackageContent);
                                    setResult(REQUEST_PAY_STATUS, intent);
                                    finish();
                                } else if (hairType == 14) {
                                    if (coinType == 0) {//拉人红包现金
                                        if (payType == 0) {
                                            Intent intent = new Intent();
                                            intent.putExtra("tid", tid);
                                            intent.putExtra("redPackageContent", redPackageContent);
                                            setResult(REQUEST_PAY_STATUS, intent);
                                            finish();
                                        } else if (payType == 1) {
                                            WXPayEntryActivity.setPayType(2);//发红包
                                            WeChatPay(data.toString());//调起微信支付
                                        } else if (payType == 2) {
                                            String sign = data.optString("sign");
                                            aLiPay(sign);
                                        }
                                    } else if (coinType == 1) {//拉人红包讯美币
                                        Intent intent = new Intent();
                                        intent.putExtra("tid", tid);
                                        intent.putExtra("money", big2(redMoney));
                                        intent.putExtra("redPackageContent", redPackageContent);
                                        setResult(REQUEST_PAY_STATUS, intent);
                                        finish();
                                    }
                                }
                            } else {
                                GetToast.showToast(mContext, jsonObject.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "服务器开小差了，请稍后重试");
                    }
                });
    }


    /**
     * 微信支付
     */
    private void WeChatPay(String data) {
        //data   {"sign":"1398238E3F8CA50325AB49D3F18FDE87","timestamp":"1514882181","noncestr":"6tdzpzy8n19xkyjc9q71xw5qr66m1tmt","partnerid":"1486139572","package":"Sign=WXPay","appid":"wxa1cb75818e93b070","tid":"160566806161928192","prepayId":"wx2018010216362160e65199370538062663","channelsCode":"weixin"}
        PayReq request = new PayReq();
        try {
            JSONObject object = new JSONObject(data);
            request.appId = object.optString("appid");
            request.partnerId = object.optString("partnerid");
            request.prepayId = object.optString("prepayId");
            request.packageValue = object.optString("package");
            request.nonceStr = object.optString("noncestr");
            request.timeStamp = object.optString("timestamp");
            request.sign = object.optString("sign");
            api.sendReq(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付宝支付
     */
    private void aLiPay(String info) {
        final String orderInfo = info;
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 密码是否正确
     */
    private void inputPassword(String pwd) {
        OkGo.<String>post(Urls.inputPassword)
                .params("hairMemberId", UserModel.getUserModel().getMemberId())
                .params("password", pwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("success")) {
                                    if (data.optString("isRight").equals("1")) {
                                        if (fromActivityId == ZhuanZhangAty.activityId) {
                                            turnAccount();
                                        } else if (fromActivityId == GroupRedPackageAty.activityId) {
                                            hairGroupRedPge();
                                        } else {
                                            hairReds();
                                        }
                                    } else {
                                        GetToast.showToast(mContext, "支付密码错误");
                                    }
                                } else {
                                    GetToast.showToast(mContext, data.optString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void changePayTypeUI(int index) {
        payType = index;
        for (int i = 0; i < tvPayList.length; i++) {
            if (i == index) {
                tvPayList[i].setTextColor(getResources().getColor(R.color.normal_red));
            } else {
                tvPayList[i].setTextColor(getResources().getColor(R.color.black_new_two));
            }
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_WECHAT_FAHONGBAO) {//关闭本页面
            Intent intent = new Intent();
            intent.putExtra("tid", tid);
            intent.putExtra("redPackageContent", redPackageContent);
            setResult(REQUEST_PAY_STATUS, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
