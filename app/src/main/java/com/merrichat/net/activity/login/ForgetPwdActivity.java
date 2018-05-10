package com.merrichat.net.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.utils.CountDownTimerUtils;
import com.merrichat.net.utils.KeyBoardHelper;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 忘记密码/绑定手机号
 * Created by amssy on 17/11/4.
 */

public class ForgetPwdActivity extends BaseActivity {
    /**
     * 返回
     */
    @BindView(R.id.forget_close)
    ImageView forgetClose;
    /**
     * title
     */
    @BindView(R.id.textView_forget_pwd)
    TextView textViewForgetPwd;
    /**
     * 手机号
     */
    @BindView(R.id.editText_phone_forget)
    ClearEditText editTextPhoneForget;
    /**
     * 验证码
     */
    @BindView(R.id.editText_yzm_forget)
    ClearEditText editTextYzmForget;
    /**
     * 获取验证码按钮
     */
    @BindView(R.id.btn_yzm)
    Button btnYzm;
    /**
     * 密码
     */
    @BindView(R.id.editText_pwd_forget)
    ClearEditText editTextPwdForget;
    /**
     * 密码可视按钮
     */
    @BindView(R.id.checkbox_register)
    CheckBox checkboxRegister;
    /**
     * 修改密码按钮
     */
    @BindView(R.id.btn_forget_pwd)
    Button btnForgetPwd;
    /**
     * 图形验证码输入框
     */
    @BindView(R.id.editText_yzm_forget_img)
    ClearEditText editTextYzmForgetImg;
    /**
     * 图形验证码获取按钮
     */
    @BindView(R.id.button_gain_yzm_forget_img)
    Button buttonGainYzmForgetImg;
    /**
     * 图形验证码
     */
    @BindView(R.id.simple_yzm_img)
    SimpleDraweeView simpleYzmImg;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private boolean isForgetPwd = false;//是否可修改密码
    private int ISBOUND_FORGET = 2;//绑定手机号 ： 1  忘记密码 2
    private String key; //加密的key 解密后格式：moblie:15212224545
    private String type = "reg";//业务类型：reg注册 login登录 找回密码：findpwd
    private String register_key;//加密前 mobile:125333|pwd:123|deviceType:android或者ios|verifyCode:253222|deviceToken:34642332432462|unionId:948r3748d943jsd49549
    private String unionid;
    private String nickName;
    private String headerUrl;
    private String channeNo = "03"; //固定03
    private String deviceType = "android";
    private String openId;
    private String country;
    private String province;
    private String city;
    private String sex;
    private String memberId;
    private boolean isSendSmsBound = false;
    private boolean isSendSmsForget = false;

    private KeyBoardHelper boardHelper;
    private int bottomHeight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        initGetIntent();
        initView();
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            ISBOUND_FORGET = intent.getIntExtra("ISBOUND_FORGET", 0);
            //这是微信信息
            unionid = intent.getStringExtra("unionId");
            nickName = intent.getStringExtra("nickName");
            headerUrl = intent.getStringExtra("headerUrl");
            openId = intent.getStringExtra("openId");
            country = intent.getStringExtra("country");
            city = intent.getStringExtra("city");
            province = intent.getStringExtra("province");
            sex = intent.getStringExtra("sex");

        }

        if (ISBOUND_FORGET == 1) {
            textViewForgetPwd.setText("绑定手机号");
            editTextPwdForget.setHint("请设置登录密码");
            btnForgetPwd.setText("绑定手机号");
        } else {
            textViewForgetPwd.setText("忘记密码");
            editTextPwdForget.setHint("请输入新密码");
            btnForgetPwd.setText("修改密码");
        }
    }

    private void initView() {
        editTextPhoneForget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneForget.getText().toString();
                String yzm = editTextYzmForget.getText().toString();
                String pwd = editTextPwdForget.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login_true);
                    isForgetPwd = true;
                } else {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login);
                    isForgetPwd = false;
                }
                if (!pwd.isEmpty()) {
                    checkboxRegister.setVisibility(View.VISIBLE);
                } else {
                    checkboxRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextPhoneForget.getText().toString().length() == 11) {
                    if (ISBOUND_FORGET == 1) {//绑定手机号
                        verifyPhone(editTextPhoneForget.getText().toString());
                    } else {//修改密码
                        verifyPhoneForgetPwd(editTextPhoneForget.getText().toString());
                    }
                }
            }
        });

        editTextYzmForget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneForget.getText().toString();
                String yzm = editTextYzmForget.getText().toString();
                String pwd = editTextPwdForget.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login_true);
                    isForgetPwd = true;
                } else {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login);
                    isForgetPwd = false;
                }
                if (!pwd.isEmpty()) {
                    checkboxRegister.setVisibility(View.VISIBLE);
                } else {
                    checkboxRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextPwdForget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneForget.getText().toString();
                String yzm = editTextYzmForget.getText().toString();
                String pwd = editTextPwdForget.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login_true);
                    isForgetPwd = true;
                } else {
                    btnForgetPwd.setBackgroundResource(R.drawable.shape_button_login);
                    isForgetPwd = false;
                }
                if (!pwd.isEmpty()) {
                    checkboxRegister.setVisibility(View.VISIBLE);
                } else {
                    checkboxRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkboxRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //选择状态 显示明文--设置为可见的密码
                    editTextPwdForget.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editTextPwdForget.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        boardHelper = new KeyBoardHelper(this);
        boardHelper.onCreate();
        boardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
        layoutBottom.post(new Runnable() {
            @Override
            public void run() {
                bottomHeight = layoutBottom.getHeight();
            }
        });

    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyBoardheight) {

            final int height = keyBoardheight;
            if (bottomHeight > height) {
                //layoutBottom.setVisibility(View.GONE);
            } else {
                int offset = bottomHeight - height;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutContent
                        .getLayoutParams();
                lp.topMargin = offset;
                layoutContent.setLayoutParams(lp);
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyBoardheight) {
            if (View.VISIBLE != layoutBottom.getVisibility()) {
                //layoutBottom.setVisibility(View.VISIBLE);
            }
            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutContent
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                layoutContent.setLayoutParams(lp);
            }

        }
    };

    @OnClick({R.id.forget_close, R.id.btn_yzm, R.id.btn_forget_pwd, R.id.button_gain_yzm_forget_img, R.id.simple_yzm_img})
    public void onViewClick(View view) {
        switch (view.getId()) {
            /**
             * 返回
             */
            case R.id.forget_close:
                finish();
                break;
            /**
             * 获取验证码
             */
            case R.id.btn_yzm:
                if (TextUtils.isEmpty(editTextPhoneForget.getText().toString())) {
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    editTextPhoneForget.startAnimation(shake);
                    RxToast.showToast("请输入手机号");
                } else if (!StringUtil.isMobilePhone(editTextPhoneForget.getText().toString())) {
                    RxToast.showToast("手机号格式有误");
                } else if (TextUtils.isEmpty(editTextYzmForgetImg.getText().toString())) {
                    RxToast.showToast("请输入图形验证码");
                } else {
                    key = StringUtil.getBase64("moblie:" + editTextPhoneForget.getText().toString() + "|verifyCode:" + editTextYzmForgetImg.getText().toString());
                    if (ISBOUND_FORGET == 1) {//绑定手机号获取验证码
                        //verifyPhone(editTextPhoneForget.getText().toString());
                        if (isSendSmsBound) {
                            sendSMS();
                        } else {
                            RxToast.showToast("手机号已注册");
                        }
                    } else {//修改密码获取验证码
                        //verifyPhoneForgetPwd(editTextPhoneForget.getText().toString());
                        if (isSendSmsForget) {
                            sendSMSForgetPwd(type);
                        }
                    }
                }
                break;
            /**
             * 获取图形验证码
             */
            case R.id.button_gain_yzm_forget_img:
                if (TextUtils.isEmpty(editTextPhoneForget.getText().toString())) {
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    editTextPhoneForget.startAnimation(shake);
                    RxToast.showToast("请输入手机号");
                } else if (!StringUtil.isMobilePhone(editTextPhoneForget.getText().toString())) {
                    RxToast.showToast("手机号格式有误");
                } else {
                    if (ISBOUND_FORGET == 1) {//绑定手机号获取验证码
                        //verifyPhone(editTextPhoneForget.getText().toString());
                        if (isSendSmsBound) {
                            buttonGainYzmForgetImg.setVisibility(View.GONE);
                            simpleYzmImg.setVisibility(View.VISIBLE);
                            Uri imgUrl = Uri.parse(Urls.VERIFYCODE + "?mobile=" + editTextPhoneForget.getText().toString() + "&time=" + System.currentTimeMillis());
                            Log.d("LogTest", "" + imgUrl);
                            simpleYzmImg.setImageURI(imgUrl);
                        } else {
                            RxToast.showToast("手机号已注册");
                        }
                    } else {//修改密码获取验证码
                        //verifyPhoneForgetPwd(editTextPhoneForget.getText().toString());
                        if (isSendSmsForget) {
                            buttonGainYzmForgetImg.setVisibility(View.GONE);
                            simpleYzmImg.setVisibility(View.VISIBLE);
                            Uri imgUrl = Uri.parse(Urls.VERIFYCODE + "?mobile=" + editTextPhoneForget.getText().toString() + "&time=" + System.currentTimeMillis());
                            Log.d("LogTest", "" + imgUrl);
                            simpleYzmImg.setImageURI(imgUrl);
                        } else {
                            RxToast.showToast("手机号未注册");
                        }
                    }
                }
                break;
            /**
             * 绑定手机号／修改密码按钮
             */
            case R.id.btn_forget_pwd:
                if (isForgetPwd) {
                    if (NetUtils.isNetworkAvailable(ForgetPwdActivity.this)) {
                        if (editTextPwdForget.getText().length() < 6) {
                            RxToast.showToast("密码最少输入6位数");
                        } else if (!StringUtil.isPassword(editTextPwdForget.getText().toString())) {
                            RxToast.showToast("密码只能是数字或字母组成");
                        } else {
                            if (ISBOUND_FORGET == 1) {
                                validRegSms();//验证验证码是否正确
                            } else {
                                validRegSmsForgetPwd();//验证验证码是否正确
                            }
                        }
                    } else {
                        RxToast.showToast("网络不可用,请检查网络连接");
                    }
                }
                break;
            /**
             * 图形验证码
             */
            case R.id.simple_yzm_img:
                Uri imgUrl = Uri.parse(Urls.VERIFYCODE + "?mobile=" + editTextPhoneForget.getText().toString() + "&time=" + System.currentTimeMillis());
                simpleYzmImg.setImageURI(imgUrl);
                break;
        }
    }

    /**
     * 验证手机号是否注册(绑定手机号)
     *
     * @param phone
     */
    private void verifyPhone(String phone) {
        OkGo.<String>get(Urls.MERRI_LOGIN_PHONE)
                .tag(this)
                .params("mobile", phone)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(response.body());
                                boolean ISREGISTER = data.optJSONObject("data").optBoolean("registered");
                                if (ISREGISTER) {
                                    RxToast.showToast("手机号已注册");
                                    isSendSmsBound = false;
                                } else {
                                    type = "reg";
                                    isSendSmsBound = true;
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
     * 发送验证码(绑定手机号)
     */
    private void sendSMS() {
        btnYzm.setEnabled(false);
        OkGo.<String>get(Urls.SEND_SMS)
                .tag(this)
                .params("key", key)
                .params("deviceToken", StringUtil.getIMEI(cnt))
                .params("type", type)
                .params("verifyCode", editTextYzmForgetImg.getText().toString())
                .execute(new StringDialogCallback(this, "正在发送验证码") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            boolean isSuccess = data.optBoolean("success");
                            if (isSuccess) {
                                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btnYzm, 60000, 1000);
                                mCountDownTimerUtils.start();
                                RxToast.showToast("验证码发送成功");
                            } else {
                                String msg = data.optJSONObject("data").optString("message");
                                if (!RxDataTool.isNullString(msg)) {
                                    RxToast.showToast(msg);
                                } else {
                                    RxToast.showToast("验证码发送失败,请重试！");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        btnYzm.setEnabled(true);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        btnYzm.setEnabled(true);
                    }
                });
    }

    /**
     * 验证验证码是否正确(绑定手机号)
     */
    private void validRegSms() {
        OkGo.<String>get(Urls.VALID_REGSMS)
                .tag(this)
                .params("mobile", editTextPhoneForget.getText().toString())
                .params("smsCode", editTextYzmForget.getText().toString())
                .execute(new StringDialogCallback(this, "正在验证信息") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                if (data.optJSONObject("data").optBoolean("result")) {
                                    //注册
                                    register_key = "mobile:" + editTextPhoneForget.getText().toString()
                                            + "|pwd:" + editTextPwdForget.getText().toString()
                                            + "|deviceType:android|verifyCode:" + editTextYzmForget.getText().toString()
                                            + "|deviceToken:" + StringUtil.getIMEI(cnt)
                                            + "|unionId:" + unionid
                                            + "|weNumber:" + openId
                                            + "|weName:" + nickName
                                            + "|weImageUrl:" + headerUrl
                                            + "|country:" + country
                                            + "|province" + province
                                            + "city" + city;
                                    setRegist();
                                } else {
                                    RxToast.showToast("验证码输入错误,请重新输入");
                                }
                            } else {
                                RxToast.showToast(data.optString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
     * 注册
     */
    private void setRegist() {
        OkGo.<String>get(Urls.REGISTER)
                .tag(this)
                .params("key", StringUtil.getBase64(register_key))
                .params("weName", nickName)
                .params("weImageUrl", headerUrl)
                .params("registerType", 1)
                .execute(new StringDialogCallback(this, "正在绑定手机号") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //注册成功 选择性别和出生日期
                                    Intent intent = new Intent(ForgetPwdActivity.this, ChooseSexActivity.class);
                                    intent.putExtra("mobile", editTextPhoneForget.getText().toString());
                                    intent.putExtra("password", editTextPwdForget.getText().toString());
                                    intent.putExtra("sex", sex);
                                    intent.putExtra("memberId", data.optJSONObject("data").optString("memberId"));
                                    startActivity(intent);
                                } else {
                                    RxToast.showToast(data.optString("message"));
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
     * 验证手机号是否注册(修改密码)
     *
     * @param phone
     */
    private void verifyPhoneForgetPwd(String phone) {
        OkGo.<String>get(Urls.MERRI_LOGIN_PHONE)
                .tag(this)
                .params("mobile", phone)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(response.body());
                                boolean ISREGISTER = data.optJSONObject("data").optBoolean("registered");
                                if (ISREGISTER) {
                                    memberId = data.optJSONObject("data").optString("memberId");
                                    type = "findpwd";
                                    isSendSmsForget = true;
                                } else {
                                    RxToast.showToast("手机号未注册");
                                    isSendSmsForget = false;
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
     * 发送验证码(修改密码)
     */
    private void sendSMSForgetPwd(String type) {
        btnYzm.setEnabled(false);
        OkGo.<String>get(Urls.SEND_SMS)
                .tag(this)
                .params("key", key)
                .params("deviceToken", StringUtil.getIMEI(cnt))
                .params("type", type)
                .execute(new StringDialogCallback(this, "正在发送验证码") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            boolean isSuccess = data.optBoolean("success");
                            if (isSuccess) {
                                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btnYzm, 60000, 1000);
                                mCountDownTimerUtils.start();
                                RxToast.showToast("验证码发送成功");
                            } else {
                                String msg = data.optJSONObject("data").optString("message");
                                if (!RxDataTool.isNullString(msg)) {
                                    RxToast.showToast(msg);
                                } else {
                                    RxToast.showToast("验证码发送失败,请重试！");
                                }
                            }
                            btnYzm.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        btnYzm.setEnabled(true);
                    }
                });
    }

    /**
     * 验证验证码是否正确(修改密码)
     */
    private void validRegSmsForgetPwd() {
        OkGo.<String>get(Urls.VALID_FIND_REGSMS)
                .tag(this)
                .params("mobile", editTextPhoneForget.getText().toString())
                .params("smsCode", editTextYzmForget.getText().toString())
                .execute(new StringDialogCallback(this, "正在验证信息") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                if (data.optJSONObject("data").optBoolean("result")) {
                                    updatePWD();
                                } else {
                                    RxToast.showToast("验证码输入错误,请重新输入");
                                }
                            } else {
                                RxToast.showToast(data.optString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
     * 修改密码
     */
    private void updatePWD() {
        OkGo.<String>get(Urls.UPDATE_PWD)
                .tag(this)
                .params("mobile", editTextPhoneForget.getText().toString())
                .params("memberId", memberId)
                .params("newPwd", StringUtil.getBase64(editTextPwdForget.getText().toString()))
                .execute(new StringDialogCallback(this, "正在修改密码") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                if (data.optJSONObject("data").optString("status").equals("1")) {
                                    RxToast.showToast("修改密码成功");
                                    finish();
                                } else {
                                    RxToast.showToast("修改密码失败,请稍后再试");
                                    finish();
                                }
                            } else {
                                RxToast.showToast(data.optString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
}
