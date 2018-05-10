package com.merrichat.net.activity.login;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.utils.HeaderParser;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ContactModel;
import com.merrichat.net.model.LoginModel;
import com.merrichat.net.model.UploadModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WeiXinLoginModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.service.BackGroudService;
import com.merrichat.net.utils.CountDownTimerUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.LoginKeyboardUtil;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.ClearEditText;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;
import static com.merrichat.net.app.MerriApp.WX_CODE;

/**
 * 登录 注册
 * Created by amssy on 17/11/3.
 */

public class LoginActivity extends BaseActivity {
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    /**
     * 登录
     */
    @BindView(R.id.textView_login)
    TextView textViewLogin;
    /**
     * 注册
     */
    @BindView(R.id.textView_register)
    TextView textViewRegister;
    /**
     * 红线
     */
    @BindView(R.id.textView_line)
    View textViewLine;
    /**
     * 登录注册父容器
     */
    @BindView(R.id.lin_title)
    LinearLayout linTitle;
    /**
     * 登录的界面
     */
    @BindView(R.id.rel_login)
    RelativeLayout relLogin;
    /**
     * 注册的界面
     */
    @BindView(R.id.lin_register)
    LinearLayout linRegister;
    /**
     * 关闭按钮
     */
    @BindView(R.id.login_close)
    ImageView loginClose;
    /**
     * 登录－－手机号
     */
    @BindView(R.id.editText_phone)
    ClearEditText editTextPhone;
    /**
     * 登录－－密码
     */
    @BindView(R.id.editText_pwd)
    ClearEditText editTextPwd;
    /**
     * 登录－－密码可视按钮
     */
    @BindView(R.id.checkbox_login)
    CheckBox checkboxLogin;
    /**
     * 登录按钮
     */
    @BindView(R.id.btn_login)
    Button btnLogin;
    /**
     * 忘记密码
     */
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    /**
     * 微信登录
     */
    @BindView(R.id.weChat_login)
    ImageView weChatLogin;
    /**
     * 注册－－手机号
     */
    @BindView(R.id.editText_phone_register)
    ClearEditText editTextPhoneRegister;
    /**
     * 注册验证码
     */
    @BindView(R.id.editText_yzm_register)
    ClearEditText editTextYzmRegister;
    /**
     * 发送验证码按钮
     */
    @BindView(R.id.btn_yzm)
    Button btnYzm;
    /**
     * 注册－－密码
     */
    @BindView(R.id.editText_pwd_register)
    ClearEditText editTextPwdRegister;
    /**
     * 注册－－密码可视按钮
     */
    @BindView(R.id.checkbox_register)
    CheckBox checkboxRegister;
    /**
     * 注册按钮
     */
    @BindView(R.id.btn_register)
    Button btnRegister;

    @BindView(R.id.text_view_toast)
    TextView textViewToast;
    /**
     * 图形验证码
     */
    @BindView(R.id.editText_yzm_register_img)
    ClearEditText editTextYzmRegisterImg;
    /**
     * 获取图形验证码
     */
    @BindView(R.id.button_gain_yzm_register_img)
    Button buttonGainYzmRegisterImg;
    /**
     * 显示图形验证码
     */
    @BindView(R.id.simple_yzm_register_img)
    SimpleDraweeView simpleYzmRegisterImg;
    @BindView(R.id.rel_content)
    LinearLayout relContent;
    @BindView(R.id.ll_login_root)
    RelativeLayout llLoginRoot;

    private boolean isLogin = false;//是否可登录
    private boolean isRegister = false;//是否可注册
    public static boolean isCloseApp = false;//点击返回是否退出APP
    public static boolean isLoginOrRegister = true;//true:登录   false:注册
    private int LoginAndRegister = 1;//1：登录  2：注册
    private Context mContext;
    private int[] mIndexs = new int[6];

    //微信登录
    public static IWXAPI api; // 微信开放平台
    private int RESULT_BOUND_WECHAT = 0;//是否是去绑定微信
    private LoginModel loginModel;
    private String accessToken;//32位字符串，保存到本地md5加密，接口访问放入请求头
    private String refreshToken;//32位字符串，保存到本地md5加密，用来换取接口访问accessToken
    private String uploadFlag;//是否上传通讯录0:不需要上传 1:需要上传
    private String mobMemberLoginId;
    private String memberId;
    private String mobile;
    private String deviceToken;//设备号
    private ProgressDialog dialog_load;
    private String pwd;
    private String mob;
    private String key;
    private String num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (getIntent().getBooleanExtra("IS_OFF_LINE",false)) {
            showOffLine();
        }
        checkPermission();
        if (UserModel.getUserModel().getIsLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        mContext = this;
        isWeixinAvilible();
        initView();
        regToWx();//注册微信
    }

    private void initView() {

        onLoginChecked();
        onRegisterChecked();

        keepLoginBtnNotOver(llLoginRoot, relContent);

        //触摸外部，键盘消失
        llLoginRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LoginKeyboardUtil.closeKeyboard(LoginActivity.this);
                return false;
            }
        });

        String moblie = PrefAppStore.getLoginOutMoblie(this);
        if (!TextUtils.isEmpty(moblie)) {
            editTextPhone.setText(moblie);
        }
    }

    /**
     * 注册改变监听
     */
    private void onRegisterChecked() {
        editTextPhoneRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneRegister.getText().toString();
                String yzm = editTextYzmRegister.getText().toString();
                String pwd = editTextPwdRegister.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login_true);
                    isRegister = true;
                } else {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login);
                    isRegister = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextPhoneRegister.getText().toString().length() == 11) {//验证手机号是否注册
                    //verifyPhoneRegister(editTextPhoneRegister.getText().toString());
                }
            }
        });

        editTextYzmRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneRegister.getText().toString();
                String yzm = editTextYzmRegister.getText().toString();
                String pwd = editTextPwdRegister.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login_true);
                    isRegister = true;
                } else {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login);
                    isRegister = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextPwdRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhoneRegister.getText().toString();
                String yzm = editTextYzmRegister.getText().toString();
                String pwd = editTextPwdRegister.getText().toString();
                if (!phone.isEmpty() && !yzm.isEmpty() && !pwd.isEmpty()) {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login_true);
                    isRegister = true;
                } else {
                    btnRegister.setBackgroundResource(R.drawable.shape_button_login);
                    isRegister = false;
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
                    editTextPwdRegister.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editTextPwdRegister.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    /**
     * 登录改变监听
     */
    private void onLoginChecked() {
        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhone.getText().toString();
                String pwd = editTextPwd.getText().toString();
                if (!phone.isEmpty() && !pwd.isEmpty()) {
                    btnLogin.setBackgroundResource(R.drawable.shape_button_login_true);
                    isLogin = true;
                } else {
                    btnLogin.setBackgroundResource(R.drawable.shape_button_login);
                    isLogin = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextPhone.getText().toString().length() == 11) {
                    //verifyPhone(editTextPhone.getText().toString());
                }
            }
        });

        editTextPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = editTextPhone.getText().toString();
                String pwd = editTextPwd.getText().toString();
                if (!phone.isEmpty() && !pwd.isEmpty()) {
                    btnLogin.setBackgroundResource(R.drawable.shape_button_login_true);
                    isLogin = true;
                } else {
                    btnLogin.setBackgroundResource(R.drawable.shape_button_login);
                    isLogin = false;
                }

                if (!pwd.isEmpty()) {
                    checkboxLogin.setVisibility(View.VISIBLE);
                } else {
                    checkboxLogin.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkboxLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //选择状态 显示明文--设置为可见的密码
                    editTextPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editTextPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @OnClick({R.id.textView_login, R.id.textView_register, R.id.login_close, R.id.btn_login, R.id.btn_register, R.id.tv_forget_pwd
            , R.id.weChat_login, R.id.btn_yzm, R.id.button_gain_yzm_register_img, R.id.simple_yzm_register_img})
    public void onViewClick(View view) {
        KeyboardUtil.hideKeyboard(view);
        switch (view.getId()) {
            case R.id.textView_login://登录title
                InputMethodManager imm1 = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm1.isActive() && this.getCurrentFocus() != null) {
                    if (this.getCurrentFocus().getWindowToken() != null) {
                        imm1.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        isWeixinAvilible();
                        LoginAndRegister = 1;
                        textViewLogin.setTextColor(getResources().getColor(R.color.normal_black));
                        textViewRegister.setTextColor(getResources().getColor(R.color.normal_gray));
                        linRegister.setVisibility(View.GONE);
                        relLogin.setVisibility(View.VISIBLE);
                        /**
                         *  红线位移
                         */
                        ObjectAnimator animator1 = ObjectAnimator.ofFloat(textViewLine, "translationX", 0.0f);
                        animator1.setDuration(200);
                        animator1.start();
                    }
                }, 200);
                break;
            case R.id.textView_register://注册title
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && this.getCurrentFocus() != null) {
                    if (this.getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        weChatLogin.setVisibility(View.GONE);
                        LoginAndRegister = 2;
                        textViewLogin.setTextColor(getResources().getColor(R.color.normal_gray));
                        textViewRegister.setTextColor(getResources().getColor(R.color.normal_black));
                        linRegister.setVisibility(View.VISIBLE);
                        relLogin.setVisibility(View.GONE);
                        /**
                         *  红线位移
                         */
                        ObjectAnimator animator = ObjectAnimator.ofFloat(textViewLine, "translationX", linTitle.getWidth() - textViewLine.getWidth());
                        animator.setDuration(200);
                        animator.start();
                    }
                }, 200);
                break;
            /**
             * 返回
             */
            case R.id.login_close:
                if (isCloseApp) {
                    AppManager.getAppManager().AppExit(cnt);
                } else {
                    finish();
                }
                break;
            /**
             * 忘记密码
             */
            case R.id.tv_forget_pwd:
                startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class).putExtra("ISBOUND_FORGET", 2));
                break;

            /**
             * 微信登录
             */
            case R.id.weChat_login:
                //判断当前网络是否可用
                if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                    weixinLogin();
                } else {
                    RxToast.showToast("网络不可用,请检查网络连接");
                }
                break;

            /**
             * 登录
             */
            case R.id.btn_login:
                if (isLogin) {
                    if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                        if (!StringUtil.isMobilePhone(editTextPhone.getText().toString())) {
                            RxToast.showToast("手机号格式有误");
                        } else {
                            verifyPhone(editTextPhone.getText().toString());
                        }
                    } else {
                        RxToast.showToast("网络不可用,请检查网络连接");
                    }
                }
                break;
            /**
             * 注册
             */
            case R.id.btn_register:
                if (isRegister) {
                    if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                        if (editTextPwdRegister.getText().length() < 6) {
                            RxToast.showToast("密码最少输入6位数");
                        } else if (!StringUtil.isPassword(editTextPwdRegister.getText().toString())) {
                            RxToast.showToast("密码只能是数字或字母组成");
                        } else {
                            validRegSmsRegister();//校验验证码是否正确
                        }
                    } else {
                        RxToast.showToast("网络不可用,请检查网络连接");
                    }
                }
                break;
            /**
             * 注册获取验证码
             */
            case R.id.btn_yzm:
                if (TextUtils.isEmpty(editTextPhoneRegister.getText().toString())) {
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    editTextPhoneRegister.startAnimation(shake);
                    RxToast.showToast("请输入手机号");
                } else if (StringUtil.isMobilePhone(editTextPhoneRegister.getText().toString())) {
                    if (!TextUtils.isEmpty(editTextYzmRegisterImg.getText().toString())) {
                        sendSMSRegister();//发送验证码
                    } else {
                        RxToast.showToast("请输入图形验证码");
                    }
                } else {
                    RxToast.showToast("手机号格式有误");
                }
                break;
            /**
             * 注册获取图形验证码
             */
            case R.id.button_gain_yzm_register_img:
                if (TextUtils.isEmpty(editTextPhoneRegister.getText().toString())) {
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    editTextPhoneRegister.startAnimation(shake);
                    RxToast.showToast("请输入手机号");
                } else if (StringUtil.isMobilePhone(editTextPhoneRegister.getText().toString())) {
                    verifyPhoneRegister(editTextPhoneRegister.getText().toString());
                } else {
                    RxToast.showToast("手机号格式有误");
                }
                break;
            case R.id.simple_yzm_register_img:
                Uri imgUrl = Uri.parse(Urls.VERIFYCODE + "?mobile=" + editTextPhoneRegister.getText().toString() + "&time=" + System.currentTimeMillis());
                simpleYzmRegisterImg.setImageURI(imgUrl);
                break;
        }
    }

    /**
     * 验证手机号是否注册(登录)
     *
     * @param phone
     */
    private void verifyPhone(String phone) {
        OkGo.<String>post(Urls.MERRI_LOGIN_PHONE)
                .tag(this)
                .params("mobile", phone)
                .execute(new StringDialogCallback(this, "正在验证手机号...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    boolean ISREGISTER = data.optJSONObject("data").optBoolean("registered");
                                    if (ISREGISTER) {
                                        if (editTextPwd.getText().length() < 6) {
                                            RxToast.showToast("密码最少输入6位，最多10位");
                                        } else {
                                            pwd = editTextPwd.getText().toString();
                                            mob = editTextPhone.getText().toString();
                                            key = StringUtil.getBase64("pwd:" + pwd + "|mobile:" + mob);
                                            //dialog
                                            dialog_load = new ProgressDialog(LoginActivity.this);
                                            dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog_load.setCanceledOnTouchOutside(false);
                                            dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            dialog_load.setMessage("正在登录中...");
                                            dialog_load.show();
                                            //登录验证
                                            goLogin(key);
                                        }
                                    } else {
                                        RxToast.showToast("手机号未注册");
                                    }
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
     * 登录验证
     *
     * @param key
     */
    private void goLogin(String key) {
        OkGo.<String>get(Urls.MERRI_LOGIN)
                .tag(this)
                .params("key", key)//加密
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    loginModel = JSON.parseObject(response.body(), LoginModel.class);
                                    accessToken = loginModel.getData().getAccessToken();
                                    refreshToken = loginModel.getData().getRefreshToken();
                                    uploadFlag = loginModel.getData().getUploadFlag();
                                    //绑定微信
                                    if (loginModel.getData().getBinding() == 0 && StringUtil.isWeixinAvilible(LoginActivity.this)) {//未绑定
                                        dialog_load.dismiss();
                                        RxToast.showToast("您还未绑定微信，请绑定...");
                                        isLoginOrRegister = true;
                                        Intent intent = new Intent(cnt, BoundWechatActivity.class);
                                        intent.putExtra("memberId", String.valueOf(loginModel.getData().getMemberId()));
                                        intent.putExtra("accountId", String.valueOf(loginModel.getData().getAccountId()));
                                        intent.putExtra("mobile", String.valueOf(loginModel.getData().getMobile()));
                                        startActivityForResult(intent, 1);
                                    } else {//已绑定
                                        //验证是否选择性别
                                        if (loginModel.getData().getGender() == 0) {//未选择
                                            dialog_load.dismiss();
                                            isLoginOrRegister = true;
                                            Intent intent = new Intent(LoginActivity.this, ChooseSexActivity.class);
                                            switch (LoginAndRegister) {
                                                case 1:
                                                    intent.putExtra("mobile", editTextPhone.getText().toString());
                                                    intent.putExtra("password", editTextPwd.getText().toString());
                                                    break;
                                                case 2:
                                                    intent.putExtra("mobile", editTextPhoneRegister.getText().toString());
                                                    intent.putExtra("password", editTextPwdRegister.getText().toString());
                                                    break;
                                            }

                                            intent.putExtra("sex", loginModel.getData().getGender());
                                            intent.putExtra("memberId", data.optJSONObject("data").optString("memberId"));
                                            startActivity(intent);
                                        } else {//已选择
                                            String memberId = String.valueOf(loginModel.getData().getMemberId());
                                            String mobile = loginModel.getData().getMobile();
                                            //登录记录
                                            loginRecord(memberId, mobile, StringUtil.getIMEI(cnt));
                                        }
                                    }
                                } else {
                                    RxToast.showToast("手机号和密码不匹配");
                                    dialog_load.dismiss();
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
                        dialog_load.dismiss();
                    }
                });
    }

    /**
     * 登录记录
     *
     * @param memberId
     * @param memberPhone
     * @param deviceToken 设备号
     */
    private void loginRecord(String memberId, final String memberPhone, final String deviceToken) {
        OkGo.<String>get(Urls.MERRI_LOGIN_RECORD)
                .tag(this)
                .params("memberId", memberId)
                .params("memberPhone", memberPhone)
                .params("channelNo", "03")//固定传03
                .params("deviceType", "Android")
                .params("deviceToken", deviceToken)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    loginSave(editTextPhone.getText().toString(), deviceToken);
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                    dialog_load.dismiss();
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
                        dialog_load.dismiss();
                    }
                });
    }

    /**
     * 保存设备登录信息
     *
     * @param memberPhone
     * @param deviceToken
     */
    private void loginSave(String memberPhone, String deviceToken) {
        OkGo.<String>get(Urls.MERRI_LOGIN_SAVE)
                .tag(this)
                .params("channelNo", "03")//固定传03
                .params("channelId", memberPhone)
                .params("deviceType", "Android")
                .params("deviceToken", deviceToken)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.optBoolean("success")) {
                                    mobMemberLoginId = jsonObject.getJSONObject("data").optString("id");
                                    RxToast.showToast("登录成功");
                                    String mobile = loginModel.getData().getMobile();
                                    PrefAppStore.setLoginOutMoblie(LoginActivity.this, mobile);
//                                    HttpHeaders headers = new HttpHeaders();
//                                    headers.put("token", accessToken);
//                                    headers.put("memberid", loginModel.getData().getMemberId() + "");
//                                    headers.put("version", MerriApp.curVersion);
//                                    OkGo.getInstance().addCommonHeaders(headers);
                                    //保存用户信息到本地数据库

                                    UserModel userModel = UserModel.getUserModel();
                                    //292098650529792
                                    userModel.setMemberId(String.valueOf(loginModel.getData().getMemberId()));
                                    userModel.setAccountId(String.valueOf(loginModel.getData().getAccountId()));
                                    userModel.setBinding(String.valueOf(loginModel.getData().getBinding()));
                                    userModel.setGender(String.valueOf(loginModel.getData().getGender()));
                                    userModel.setImgUrl(loginModel.getData().getImgUrl());
                                    userModel.setMobile(loginModel.getData().getMobile());
                                    userModel.setRealname(loginModel.getData().getRealname());
                                    userModel.setStatus(loginModel.getData().getStatus());
                                    userModel.setUserFlag(loginModel.getData().getUserFlag());
                                    userModel.setWeixinAccountId(loginModel.getData().getWeixinAccountId());
                                    userModel.setMobMemberLoginId(mobMemberLoginId);
                                    userModel.setRegistTime(loginModel.getData().getRegistTime());
                                    userModel.setIsLogin(true);
                                    userModel.setAccessToken(accessToken);
                                    userModel.setRefreshToken(refreshToken);
                                    userModel.setUploadFlag(uploadFlag);
                                    UserModel.setUserModel(userModel);
                                    dialog_load.dismiss();
                                    firstLogin();
                                } else {
                                    dialog_load.dismiss();
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
                        dialog_load.dismiss();
                    }
                });
    }

    /**
     * 判断是否首次登录
     */
    private void firstLogin() {
        OkGo.<String>post(Urls.firstLogin)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("mobile", UserModel.getUserModel().getMobile())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                boolean firstLogin = data.optBoolean("firstLogin");
                                String url = data.optString("url");
                                String shareUrl = data.optString("shareUrl");
                                PrefAppStore.setIsFirstJiangliDialogShowTagValue(cnt, firstLogin);
                                PrefAppStore.setIsFirstJiangliDialogShowUrlValue(cnt, url);
                                PrefAppStore.setIsFirstJiangliDialogShareUrlKey(cnt, shareUrl);
                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
                                EventBus.getDefault().post(myEventBusModel);
                                sendLoginSucessBroadCostReciver();
                                if (!PrefAppStore.getIsAlreadyUploadTxl(cnt)) {
                                    PrefAppStore.setIsAlreadyUploadTxl(cnt, false);
                                }
                                if (isCloseApp) {
                                    startActivity(new Intent(cnt, MainActivity.class));
                                    finish();
                                } else {
                                    finish();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void sendLoginSucessBroadCostReciver() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.REFRESH_LOGIN_SUCESS_ENTER_LOGIN = true;
        EventBus.getDefault().post(myEventBusModel);
    }


    /**
     * 判断手机是否安装微信
     */
    private void isWeixinAvilible() {
        if (StringUtil.isWeixinAvilible(this)) {
            weChatLogin.setVisibility(VISIBLE);
        } else
            weChatLogin.setVisibility(View.GONE);
    }

    /**
     * 注册微信
     *
     * @return void 返回类型
     * @throws
     * @Title: regToWx
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void regToWx() {
        // TODO Auto-generated method stub
        api = WXAPIFactory.createWXAPI(cnt, cnt.getResources().getString(R.string.weixin_app_id), true);
        api.registerApp(cnt.getResources().getString(R.string.weixin_app_id));
    }

    /**
     * 调起微信
     *
     * @return void 返回类型
     * @throws
     * @Title: weixinLogin
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void weixinLogin() {
        // TODO Auto-generated method stub
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        MerriApp.sendReq = api.sendReq(req);
    }

    /**
     * 微信登录获取code
     *
     * @param code
     */
    private void getAccessToken(String code) {
        OkGo.<String>get(Urls.WX_LOGIN_CODE)
                .tag(this)
                .params("appid", cnt.getResources().getString(R.string.weixin_app_id))
                .params("secret", cnt.getResources().getString(R.string.weixin_app_secret))
                .params("code", code)
                .params("grant_type", "authorization_code")
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                String result = response.body();
                                try {
                                    JSONObject jsonResult = new JSONObject(result);
                                    String access_token = jsonResult
                                            .getString("access_token");
                                    String openid = jsonResult.getString("openid");
                                    if (!TextUtils.isEmpty(access_token)
                                            && !TextUtils.isEmpty(openid)) {
                                        getWeixinUserInfo(access_token, openid);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
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
     * 微信登录获取用户信息
     *
     * @param access_token
     * @param openid
     */
    private void getWeixinUserInfo(String access_token, String openid) {
        OkGo.<String>get(Urls.WX_LOGIN_USER_INFO)
                .tag(this)
                .params("access_token", access_token)
                .params("openid", openid)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                String result = response.body();
                                try {
                                    WeiXinLoginModel weiXinLoginModel = WeiXinLoginModel.parseJSON(result);
                                    String headimgurl = weiXinLoginModel.getHeadimgurl();
                                    String openid = weiXinLoginModel.getOpenid();
                                    String nickname = weiXinLoginModel.getNickname();
                                    String sex = weiXinLoginModel.getSex();
                                    String unionid = weiXinLoginModel.getUnionid();
                                    String country = weiXinLoginModel.getCountry();
                                    String city = weiXinLoginModel.getCity();
                                    String province = weiXinLoginModel.getProvince();

                                    //Log.d("LogTest","|||"+unionid+"|||"+ nickname+"|||"+headimgurl+"|||"+ openid+"|||"+country+"|||"+city+"|||"+province+"|||"+sex);
                                    check_bing(unionid, nickname, headimgurl, openid, country, city, province, sex);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
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
     * 微信登录
     *
     * @param unionid
     * @param nickName
     * @param headerUrl
     * @param openId
     * @param country
     * @param city
     * @param province
     * @param sex
     */
    private void check_bing(final String unionid, final String nickName, final String headerUrl, final String openId
            , final String country, final String city, final String province, final String sex) {
        OkGo.<String>get(Urls.WX_LOGIN)
                .tag(this)
                .params("uid", unionid)
                .execute(new StringDialogCallback(this, "正在登录中") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                boolean isSuccess = jsonObject.optBoolean("success");
                                if (isSuccess) {
                                    int binging = jsonObject.optJSONObject("data").optInt("binging");
                                    switch (binging) {
                                        case 0:  //未绑定微信账号(去绑定)
                                            Intent intent = new Intent(cnt, ForgetPwdActivity.class);
                                            intent.putExtra("ISBOUND_FORGET", 1);
                                            intent.putExtra("unionId", unionid);
                                            intent.putExtra("nickName", nickName);
                                            intent.putExtra("headerUrl", headerUrl);
                                            intent.putExtra("openId", openId);
                                            intent.putExtra("country", country);
                                            intent.putExtra("city", city);
                                            intent.putExtra("province", province);
                                            intent.putExtra("sex", sex);
                                            startActivity(intent);
                                            break;
                                        case 1:  //已绑定微信账号
                                            loginModel = JSON.parseObject(response.body(), LoginModel.class);
                                            deviceToken = StringUtil.getIMEI(LoginActivity.this);
                                            dialog_load = new ProgressDialog(LoginActivity.this);

                                            dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog_load.setCanceledOnTouchOutside(false);
                                            dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            dialog_load.setMessage("正在登录中...");
                                            dialog_load.show();
                                            //保存设备信息
                                            loginSave(loginModel.getData().getMobile(), deviceToken);
                                            break;
                                    }
                                }
                            } catch (Exception e) {

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
    protected void onResume() {
        super.onResume();
        //RxToast.showToast(""+sendReq);
        if (MerriApp.sendReq) {
            //RxToast.showToast("微信返回code："+WX_CODE);
            //通过code获取token
            getAccessToken(WX_CODE);
        }

    }

    /**
     * 强制下线dialog
     */
    private void showOffLine() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.title("提示").content("您好，您的账号已在其它设备上登录，请注意账户安全!")//
                .btnNum(1)
                .btnText("知道了")
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        if (AppManager.getAppManager().hasActivity(MainActivity.class)) {
                            AppManager.getAppManager().finishActivity(MainActivity.class);
                        }
                        isCloseApp = true;
                        dialog.dismiss();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MerriApp.sendReq = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {//绑定微信
            switch (resultCode) {
                case RESULT_OK:
                    memberId = String.valueOf(loginModel.getData().getMemberId());
                    mobile = loginModel.getData().getMobile();

                    dialog_load = new ProgressDialog(LoginActivity.this);
                    dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_load.setCanceledOnTouchOutside(false);
                    dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog_load.setMessage("正在保存登录信息...");
                    dialog_load.show();
                    //登录验证
                    goLogin(key);
                    break;
                case RESULT_CANCELED:
                    RxToast.showToast("取消绑定,仍处于未登录状态");
                    RxActivityTool.skipActivity(LoginActivity.this, MainActivity.class);
                    finish();
                    //未绑定微信  则不绑性别  直接登录失败
                    //验证是否选择性别
//                    if (loginModel.getData().getGender() == 0) {//未选择
//                        dialog_load.dismiss();
//                        isLoginOrRegister = true;
//                        Intent intent = new Intent(LoginActivity.this, ChooseSexActivity.class);
//                        switch (LoginAndRegister){
//                            case 1:
//                                intent.putExtra("mobile", editTextPhone.getText().toString());
//                                intent.putExtra("password", editTextPwd.getText().toString());
//                                break;
//                            case 2:
//                                intent.putExtra("mobile", editTextPhoneRegister.getText().toString());
//                                intent.putExtra("password", editTextPwdRegister.getText().toString());
//                                break;
//                        }
//                        intent.putExtra("sex", loginModel.getData().getGender());
//                        intent.putExtra("memberId", ""+loginModel.getData().getMemberId());
//                        startActivity(intent);
//                    } else {//已选择
//                        String memberId = String.valueOf(loginModel.getData().getMemberId());
//                        String mobile = loginModel.getData().getMobile();
//                        accessToken = loginModel.getData().getAccessToken();
//                        refreshToken = loginModel.getData().getRefreshToken();
//                        //登录记录
//                        loginRecord(memberId, mobile, StringUtil.getIMEI(cnt));
//                    }
                    break;
            }
        }

    }

    /**
     * 验证手机号是否注册（注册）
     *
     * @param phone
     */
    private void verifyPhoneRegister(String phone) {
        OkGo.<String>get(Urls.MERRI_LOGIN_PHONE)
                .tag(this)
                .params("mobile", phone)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(response.body());
                                boolean ISREGISTER = data.optJSONObject("data").optBoolean("registered");
                                if (ISREGISTER) {
                                    RxToast.showToast("手机号已注册");
                                } else {
                                    buttonGainYzmRegisterImg.setVisibility(View.GONE);
                                    simpleYzmRegisterImg.setVisibility(View.VISIBLE);
                                    Uri imgurl = Uri.parse(Urls.VERIFYCODE + "?mobile=" + editTextPhoneRegister.getText().toString() + "&time=" + System.currentTimeMillis());
                                    simpleYzmRegisterImg.setImageURI(imgurl);
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
     * 发送验证码(注册)
     */
    private void sendSMSRegister() {
        btnYzm.setEnabled(false);
        String key = StringUtil.getBase64("moblie:" + editTextPhoneRegister.getText().toString() + "|verifyCode:" + editTextYzmRegisterImg.getText().toString());
        String type = "reg";
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
                                RxToast.showToast("" + data.optJSONObject("data").optString("message"));
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
     * 验证验证码是否正确(注册)
     */
    private void validRegSmsRegister() {
        OkGo.<String>get(Urls.VALID_REGSMS)
                .tag(this)
                .params("mobile", editTextPhoneRegister.getText().toString())
                .params("smsCode", editTextYzmRegister.getText().toString())
                .execute(new StringDialogCallback(this, "正在验证信息") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                if (data.optJSONObject("data").optBoolean("result")) {
                                    //注册
                                    String register_key = "mobile:" + editTextPhoneRegister.getText().toString()
                                            + "|pwd:" + editTextPwdRegister.getText().toString()
                                            + "|deviceType:android|verifyCode:" + editTextYzmRegister.getText().toString()
                                            + "|deviceToken:" + StringUtil.getIMEI(cnt);
                                    //+ "|unionId:" + ""
                                    //+ "|weNumber:" + ""
                                    //+ "|weName:" + ""
                                    //+ "|weImageUrl:" + ""
                                    //+ "|country:" + ""
                                    //+ "|province" + ""
                                    //+ "city" + "";
                                    setRegist(register_key);
                                } else if (data.optJSONObject("data").optString("result").equals("invalid")) {
                                    RxToast.showToast("验证码已失效，请重新获取");
                                } else {
                                    RxToast.showToast("验证码输入错误");
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
    private void setRegist(String key) {
        OkGo.<String>get(Urls.REGISTER)
                .tag(this)
                .params("key", StringUtil.getBase64(key))
                .params("registerType", 0)//注册类型 0:手机号注册 1:微信注册
                .execute(new StringDialogCallback(LoginActivity.this, "正在注册") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast("注册成功");
                                    //注册成功 选择性别和出生日期
                                    isLoginOrRegister = false;
                                    Intent intent = new Intent(LoginActivity.this, ChooseSexActivity.class);
                                    intent.putExtra("mobile", editTextPhoneRegister.getText().toString());
                                    intent.putExtra("password", editTextPwdRegister.getText().toString());
                                    intent.putExtra("sex", 0);
                                    intent.putExtra("activityId", getIntent().getIntExtra("activityId", -1));
                                    intent.putExtra("memberId", data.optJSONObject("data").optString("memberId"));
                                    startActivity(intent);
                                    finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (isCloseApp) {
                AppManager.getAppManager().AppExit(cnt);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.CAMERA) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MerriApp.mContext, Manifest.permission.READ_CONTACTS) == PackageManager
                            .PERMISSION_GRANTED) {
                deviceToken = StringUtil.getIMEI(cnt);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS}, GET_PERMISSION_REQUEST);
            }
        } else {
            deviceToken = StringUtil.getIMEI(cnt);
        }
    }

    /**
     * 权限申请回调
     */
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                //相机权限
                int cameraPermissionResult = grantResults[0];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED; //相机权限
                if (!cameraPermissionGranted) {
                    size++;
                }
                //读写内存权限
                int writeResult = grantResults[1];
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[2];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED; //录音权限
                if (!recordPermissionGranted) {
                    size++;
                }

                //位置权限
                int permissionResult3 = grantResults[3];
                boolean permissionGranted3 = permissionResult3 == PackageManager.PERMISSION_GRANTED; //位置权限
                if (!permissionGranted3) {
                    size++;
                }

                //位置权限
                int permissionResult4 = grantResults[4];
                boolean permissionGranted4 = permissionResult4 == PackageManager.PERMISSION_GRANTED; //位置权限
                if (!permissionGranted4) {
                    size++;
                }

                //手机信息
                int permissionResult5 = grantResults[5];
                boolean permissionGranted5 = permissionResult5 == PackageManager.PERMISSION_GRANTED; //手机信息
                if (!permissionGranted5) {
                    size++;
                }  //手机信息
                int permissionResult6 = grantResults[6];
                boolean permissionGranted6 = permissionResult6 == PackageManager.PERMISSION_GRANTED; //联系人
                if (!permissionGranted6) {
                    size++;
                }
                if (size == 0) {
                    deviceToken = StringUtil.getIMEI(cnt);
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }

    /**
     * 保持登录按钮始终不会被覆盖
     *
     * @param root
     * @param subView
     */
    private void keepLoginBtnNotOver(final View root, final View subView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                // 获取root在窗体的可视区域、
                root.getWindowVisibleDisplayFrame(rect);
                // 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                // 若不可视区域高度大于200，则键盘显示,其实相当于键盘的高度
                if (rootInvisibleHeight > 200) {
                    // 显示键盘时
                    int srollHeight = rootInvisibleHeight - (root.getHeight() - subView.getHeight()) - LoginKeyboardUtil.getNavigationBarHeight(root.getContext());
                    if (srollHeight > 0) {//当键盘高度覆盖按钮时
                        root.scrollTo(0, srollHeight);
                    }
                } else {
                    // 隐藏键盘时
                    root.scrollTo(0, 0);
                }
            }
        });
    }

}
