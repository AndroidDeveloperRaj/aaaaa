package com.merrichat.net.activity.my.mywallet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.merrifunction.ITakePictureUIAty;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.AnimUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.MyGridView;
import com.merrichat.net.view.VirtualKeyboardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/7/4.
 * 设置支付密码
 */
public class PasswardActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.virtualKeyboardView)
    VirtualKeyboardView virtualKeyboardView;


    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_title_text)
    TextView tv_title;
    @BindView(R.id.tv_top_tip)
    TextView tvTopTip;
    @BindView(R.id.tv_pass1)
    TextView tvPass1;
    @BindView(R.id.tv_pass2)
    TextView tvPass2;
    @BindView(R.id.tv_pass3)
    TextView tvPass3;
    @BindView(R.id.tv_pass4)
    TextView tvPass4;
    @BindView(R.id.tv_pass5)
    TextView tvPass5;
    @BindView(R.id.tv_pass6)
    TextView tvPass6;
    @BindView(R.id.lay_input)
    LinearLayout layInput;
    @BindView(R.id.img_pass1)
    ImageView imgPass1;
    @BindView(R.id.img_pass2)
    ImageView imgPass2;
    @BindView(R.id.img_pass3)
    ImageView imgPass3;
    @BindView(R.id.img_pass4)
    ImageView imgPass4;
    @BindView(R.id.img_pass5)
    ImageView imgPass5;
    @BindView(R.id.img_pass6)
    ImageView imgPass6;

    private String password_old = "";
    private String password_new_again = "";
    private String password_new = "";
    private UserModel userModel;

    private ArrayList<Map<String, String>> valueList;
    private Animation enterAnim;
    private Animation exitAnim;
    private MyGridView gridView;

    private int currentIndex = -1;    //用于记录当前输入密码格位置
    private TextView[] tvList;
    private ImageView[] imgList;
    private boolean isKeyboardViewShow;
    private int step_pwd = -1;//0是原密码,1是新密码,2是确认新密码

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passward);
        ButterKnife.bind(this);
        initTitleBar();
        initAnim();
        initView();

    }

    private void initAnim() {
        enterAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        exitAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
    }

    private void initView() {
        if (MyWalletActivity.isSetPassword) {
            tvTopTip.setText("请输入原支付密码");
            step_pwd = 0;
        } else {
            tvTopTip.setText("请输入支付密码");
            step_pwd = 1;
        }
        tvList = new TextView[6];
        imgList = new ImageView[6];
        tvList[0] = tvPass1;
        tvList[1] = tvPass2;
        tvList[2] = tvPass3;
        tvList[3] = tvPass4;
        tvList[4] = tvPass5;
        tvList[5] = tvPass6;

        imgList[0] = imgPass1;
        imgList[1] = imgPass2;
        imgList[2] = imgPass3;
        imgList[3] = imgPass4;
        imgList[4] = imgPass5;
        imgList[5] = imgPass6;
        isKeyboardViewShow = true;
        userModel = UserModel.getUserModel();
        valueList = virtualKeyboardView.getValueList();
        virtualKeyboardView.getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.startAnimation(exitAnim);
                virtualKeyboardView.setVisibility(View.GONE);
                isKeyboardViewShow = false;
            }
        });
        gridView = virtualKeyboardView.getGridView();
        gridView.setOnItemClickListener(this);
        setOnFinishInput();
    }

    /**
     * 初始化titleBar
     */
    private void initTitleBar() {
        if (MyWalletActivity.isSetPassword) {
            tv_title.setText("修改支付密码");
        } else {
            tv_title.setText("设置支付密码");
        }
    }

    /**
     * 创建支付密码
     */
    private void setPassword() {
        OkGo.<String>post(Urls.setWalletPassword)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("password", password_new)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                GetToast.showToast(cnt, "设置成功");
                                finish();
                            } else {
                                GetToast.showToast(cnt, data.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 验证密码是否正确
     */
    private void getValidatePwd() {
        OkGo.<String>post(Urls.validatePassword)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("password", password_old)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if ("1".equals(jsonObject.getJSONObject("data").optString("isRight"))) {
                                step_pwd = 1;
                                clearInputBload();
                                tvTopTip.setTextColor(getResources().getColor(R.color.black_new_two));
                                tvTopTip.setText("请输入支付密码");
                                tvTopTip.setVisibility(View.GONE);
                                tvTopTip.setVisibility(View.VISIBLE);
                                // 向左边移入
                                tvTopTip.setAnimation(AnimationUtils.makeInAnimation(cnt, false));
                                layInput.setVisibility(View.GONE);
                                layInput.setVisibility(View.VISIBLE);
                                layInput.setAnimation(AnimationUtils.makeInAnimation(cnt, false));
                            } else {
                                step_pwd = 0;
                                tvTopTip.setTextColor(getResources().getColor(R.color.red));
                                tvTopTip.setText("原支付密码错误");
                                Animation shake = AnimationUtils.loadAnimation(cnt, R.anim.shake);
                                tvTopTip.startAnimation(shake);
                                clearInputBload();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setUpdatePwd() {

        OkGo.<String>post(Urls.updateAccountPwd)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("oldPassword", password_old)
                .params("newPassword", password_new)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.optBoolean("success")) {
                                GetToast.showToast(cnt, "支付密码修改成功");
                                finish();
                            } else {
                                GetToast.showToast(cnt, "修改密码失败");
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 11 && position != 9) {    //点击0~9按钮

            if (currentIndex >= -1 && currentIndex < 5) {      //判断输入位置————要小心数组越界
                ++currentIndex;
                tvList[currentIndex].setVisibility(View.INVISIBLE);
                imgList[currentIndex].setVisibility(View.VISIBLE);
                tvList[currentIndex].setText(valueList.get(position).get("name"));
            }
        } else {
            if (position == 11) {      //点击退格键
                if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                    tvList[currentIndex].setVisibility(View.VISIBLE);
                    imgList[currentIndex].setVisibility(View.INVISIBLE);
                    tvList[currentIndex].setText("");
                    currentIndex--;
                }
            }
        }
    }

    private void clearInputBload() {
        for (int i = 0; i < tvList.length; i++) {
            tvList[i].setText("");
            tvList[i].setVisibility(View.VISIBLE);
            imgList[i].setVisibility(View.INVISIBLE);
        }
        currentIndex = -1;
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
                    if (step_pwd == 0) {
                        password_old = strPassword;
                        if (MyWalletActivity.isSetPassword) {//修改密码
                            getValidatePwd();
                        }
                    } else if (step_pwd == 1) {
                        password_new = strPassword;
                        step_pwd = 2;
                        tvTopTip.setTextColor(getResources().getColor(R.color.black_new_two));
                        tvTopTip.setText("请确认支付密码");
                        tvTopTip.setVisibility(View.GONE);
                        tvTopTip.setVisibility(View.VISIBLE);
                        // 向左边移入
                        tvTopTip.setAnimation(AnimationUtils.makeInAnimation(cnt, false));
                        layInput.setVisibility(View.GONE);
                        layInput.setVisibility(View.VISIBLE);
                        layInput.setAnimation(AnimationUtils.makeInAnimation(cnt, false));
                        clearInputBload();
                    } else if (step_pwd == 2) {
                        password_new_again = strPassword;
                        if (MyWalletActivity.isSetPassword) {//修改密码
                            if (password_new.equals(password_new_again)) {
                                setUpdatePwd();
                            } else {
                                step_pwd = 2;
                                tvTopTip.setTextColor(getResources().getColor(R.color.red));
                                tvTopTip.setText("两次输入密码不一致");
                                Animation shake = AnimationUtils.loadAnimation(cnt, R.anim.shake);
                                tvTopTip.startAnimation(shake);
                                clearInputBload();
                            }
                        } else {
                            if (password_new.equals(password_new_again)) {
                                setPassword();
                            } else {
                                step_pwd = 2;
                                tvTopTip.setTextColor(getResources().getColor(R.color.red));
                                tvTopTip.setText("两次输入密码不一致");
                                Animation shake = AnimationUtils.loadAnimation(cnt, R.anim.shake);
                                tvTopTip.startAnimation(shake);
                                clearInputBload();
                            }
                        }
                    }
                }
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_pass1, R.id.tv_pass2, R.id.tv_pass3, R.id.tv_pass4, R.id.tv_pass5, R.id.tv_pass6, R.id.img_pass1, R.id.img_pass2, R.id.img_pass3, R.id.img_pass4, R.id.img_pass5, R.id.img_pass6})
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
                if (!isKeyboardViewShow) {
                    virtualKeyboardView.startAnimation(enterAnim);
                    virtualKeyboardView.setVisibility(View.VISIBLE);
                    isKeyboardViewShow = true;
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
