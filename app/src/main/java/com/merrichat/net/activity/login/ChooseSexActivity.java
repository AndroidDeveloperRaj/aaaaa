package com.merrichat.net.activity.login;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.LoginModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录选择性别
 * Created by amssy on 17/11/4.
 */

public class ChooseSexActivity extends BaseActivity {
    /**
     * 选择出生日期
     */
    @BindView(R.id.tv_choose_birth)
    TextView tvChooseBirth;
    /**
     * 进入按钮
     */
    @BindView(R.id.btn_choose_finish)
    Button btnChooseFinish;
    @BindView(R.id.radio_man)
    RadioButton radioMan;
    @BindView(R.id.radio_women)
    RadioButton radioWomen;
    @BindView(R.id.choose_close)
    ImageView chooseClose;
    private int sex = 0;//性别 1男 2女 允许值: 1, 2
    private String dateStr = "";//生日
    private String mobile;
    private String password;
    private LoginModel loginModel;
    private String mobMemberLoginId;
    private String accessToken;//32位字符串，保存到本地md5加密，接口访问放入请求头
    private String refreshToken;//32位字符串，保存到本地md5加密，用来换取接口访问accessToken
    private String uploadFlag;//是否上传通讯录0:不需要上传 1:需要上传
    private ProgressDialog dialog_load;
    private String memberId;
    private CommomDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sex);
        ButterKnife.bind(this);
        initGetIntent();
        initView();
    }

    private void initView() {
        radioMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog = new CommomDialog(cnt, R.style.dialog, "选择性别后将无法修改，请确认选择无误", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                sex = 1;
                                if (!dateStr.equals("") || dateStr != "") {
                                    btnChooseFinish.setBackgroundResource(R.drawable.shape_button_login_true);
                                }
                                dialog.dismiss();
                                radioMan.setChecked(true);
                            } else {
                                radioMan.setChecked(false);
                            }
                        }
                    }).setTitle("温馨提示");
                    dialog.show();
                }
            }
        });
        radioWomen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog = new CommomDialog(cnt, R.style.dialog, "选择性别后将无法修改，请确认选择无误", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                sex = 2;
                                if (!dateStr.equals("") || dateStr != "") {
                                    btnChooseFinish.setBackgroundResource(R.drawable.shape_button_login_true);
                                }
                                dialog.dismiss();
                                radioWomen.setChecked(true);
                            } else {
                                radioWomen.setChecked(false);
                            }
                        }
                    }).setTitle("温馨提示");
                    dialog.show();
                }
            }
        });
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            sex = intent.getIntExtra("sex", 0);
            mobile = intent.getStringExtra("mobile");
            password = intent.getStringExtra("password");
            memberId = intent.getStringExtra("memberId");
            if (sex == 1) {
                radioMan.setChecked(true);
            } else if (sex == 2) {
                radioWomen.setChecked(true);
            }
        }
    }

    @OnClick({R.id.tv_choose_birth, R.id.btn_choose_finish, R.id.choose_close})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_choose_birth:
                setTvChooseBirth();
                break;
            case R.id.btn_choose_finish:
                if (sex != 0) {
                    if (!dateStr.equals("") || dateStr != "") {
                        if (NetUtils.isNetworkAvailable(ChooseSexActivity.this)) {
                            dialog_load = new ProgressDialog(ChooseSexActivity.this);
                            dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog_load.setCanceledOnTouchOutside(false);
                            dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog_load.setMessage("正在保存登录信息...");
                            dialog_load.show();
                            boundSEX(memberId, dateStr, sex);
                        } else {
                            RxToast.showToast("网络不可用,请检查网络连接");
                        }
                        break;
                    } else {
                        //RxToast.showToast("请选择出生日期");
                    }
                } else {
                    //RxToast.showToast("请选择性别");
                }
                break;
            case R.id.choose_close:
                RxToast.showToast("取消绑定,仍处于未登录状态");
                finish();
                RxActivityTool.skipActivity(ChooseSexActivity.this, MainActivity.class);
                break;
        }
    }

    /**
     * 返回键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            RxToast.showToast("取消绑定,仍处于未登录状态");
            finish();
            RxActivityTool.skipActivity(ChooseSexActivity.this, MainActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 调用系统日历
     * 选择出生日期
     */
    private void setTvChooseBirth() {
        final Calendar calender = Calendar.getInstance();
        DatePickerDialog dialog = new
                DatePickerDialog(ChooseSexActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int
                    dayOfMonth) {

                String mouth1, day1;
                if (monthOfYear <= 9) {
                    mouth1 = "0" + (monthOfYear + 1);
                } else {
                    mouth1 = String.valueOf(monthOfYear + 1);
                }
                if (dayOfMonth <= 9) {
                    day1 = "0" + dayOfMonth;
                } else {
                    day1 = String.valueOf(dayOfMonth);
                }
                dateStr = String.valueOf(year) + "-" + mouth1 + "-" + day1;
                tvChooseBirth.setText(dateStr);
                tvChooseBirth.setCompoundDrawables(null, null, null, null);

                if (sex != 0) {
                    btnChooseFinish.setBackgroundResource(R.drawable.shape_button_login_true);
                }

            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH));
        //设置最大日期
        DatePicker datePicker = dialog.getDatePicker();
        Date taday = Calendar.getInstance().getTime();//当天
        try {
            //datePicker.setMinDate(taday.getTime());// 最小日期
            datePicker.setMaxDate(taday.getTime());// 最大日期
        } catch (Exception e) {

        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                memberId = String.valueOf(loginModel.getData().getMemberId());
                mobile = loginModel.getData().getMobile();

                dialog_load = new ProgressDialog(ChooseSexActivity.this);
                dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_load.setCanceledOnTouchOutside(false);
                dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog_load.setMessage("正在保存登录信息...");
                dialog_load.show();
                //登录记录
                loginRecord(memberId, mobile, StringUtil.getIMEI(cnt));
            }
        }

    }

    /**
     * 绑定性别-出生日期
     *
     * @param memberId
     * @param birthday
     * @param gender
     */
    private void boundSEX(String memberId, String birthday, int gender) {
        OkGo.<String>get(Urls.BOUND_SEX_BIRTH)
                .tag(this)
                .params("memberId", memberId)
                .params("birthday", birthday)
                .params("gender", gender)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    goLogin();
                                } else {
                                    RxToast.showToast("绑定性别和出生日期失败，请重试");
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
     * 登录验证
     */
    private void goLogin() {
        OkGo.<String>get(Urls.MERRI_LOGIN)
                .tag(this)
                .params("key", StringUtil.getBase64("pwd:" + password + "|mobile:" + mobile))//加密
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
                                    //手机未安装微信直接跳过微信绑定
                                    if (loginModel.getData().getBinding() == 0 && StringUtil.isWeixinAvilible(ChooseSexActivity.this)) {//未绑定
                                        RxToast.showToast("您还未绑定微信，请绑定...");
                                        dialog_load.dismiss();
                                        Intent intent = new Intent(cnt, BoundWechatActivity.class);
                                        intent.putExtra("memberId", String.valueOf(loginModel.getData().getMemberId()));
                                        intent.putExtra("accountId", String.valueOf(loginModel.getData().getAccountId()));
                                        intent.putExtra("mobile", String.valueOf(loginModel.getData().getMobile()));
                                        startActivityForResult(intent, 1);
                                    } else {//已绑定
                                        String memberId = String.valueOf(loginModel.getData().getMemberId());
                                        String mobile = loginModel.getData().getMobile();
                                        accessToken = loginModel.getData().getAccessToken();
                                        refreshToken = loginModel.getData().getRefreshToken();
                                        //登录记录
                                        loginRecord(memberId, mobile, StringUtil.getIMEI(cnt));
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
                                    loginSave(mobile, deviceToken);
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
                                    PrefAppStore.setLoginOutMoblie(ChooseSexActivity.this, mobile);
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
                                    //注册成功首次登录
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

    private void sendLoginSucessBroadCostReciver() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.REFRESH_LOGIN_SUCESS_ENTER_LOGIN = true;
        EventBus.getDefault().post(myEventBusModel);
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
                                /*JSONObject data = jsonObject.optJSONObject("data");
                                boolean firstLogin = data.optBoolean("firstLogin");
                                String url = data.optString("url");
                                String shareUrl = data.optString("shareUrl");
                                PrefAppStore.setIsFirstJiangliDialogShowTagValue(cnt, firstLogin);
                                PrefAppStore.setIsFirstJiangliDialogShowUrlValue(cnt, url);
                                PrefAppStore.setIsFirstJiangliDialogShareUrlKey(cnt, shareUrl);
                                //登录成功 跳转首页
                                sendLoginSucessBroadCostReciver();
                                //上传手机通讯录
                                PrefAppStore.setIsAlreadyUploadTxl(cnt, false);
                                startActivity(new Intent(cnt, MainActivity.class));
                                finish();*/
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
                                startActivity(new Intent(cnt, MainActivity.class));
                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
