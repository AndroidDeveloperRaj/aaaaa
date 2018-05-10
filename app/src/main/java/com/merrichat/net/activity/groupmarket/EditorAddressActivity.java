package com.merrichat.net.activity.groupmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AddressJsonModel;
import com.merrichat.net.model.AddressModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.SelectCityDialogUtils;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新增地址/编辑地址
 * Created by amssy on 18/1/23.
 */

public class EditorAddressActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    /**
     * 选择所属区县
     */
    @BindView(R.id.editText_county)
    TextView editTextCounty;
    @BindView(R.id.editText_phone)
    EditText editTextPhone;
    @BindView(R.id.editText_name)
    EditText editTextName;
    @BindView(R.id.editText_detail_address)
    EditText editTextDetailAddress;

    private String type;
    private String key = "rec";//vir:表示的虚拟的收货地址, rec:表示的实物的收货地址
    private String addressId;// 地址ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exitor_address);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            AddressModel addressModel = (AddressModel) intent.getSerializableExtra("addressModel");
            if (addressModel != null) {
                addressId = addressModel.getId();
                editTextPhone.setText(addressModel.getMobile());
                editTextPhone.setSelection(addressModel.getMobile().length());//设置光标位于最后
                editTextName.setText(addressModel.getName());
                editTextCounty.setText(addressModel.getRecAddress());
                editTextDetailAddress.setText(addressModel.getDetAddress());
            }
            if (RxDataTool.isNullString(addressId)) addressId = "";
        }
        if (type.equals("editor")) {
            tvTitleText.setText("编辑地址");
        } else {
            tvTitleText.setText("新增地址");
        }
        tvRight.setText("保存");
        tvRight.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.editText_county})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right://保存
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                String phoneString = editTextPhone.getText().toString().trim();
                String nameString = editTextName.getText().toString().trim();
                String countyString = editTextCounty.getText().toString().trim();
                String addressString = editTextDetailAddress.getText().toString().trim();

                if (TextUtils.isEmpty(phoneString)) {
                    RxToast.showToast("请输入收货人联系电话");
                    return;
                } else {
                    if (!StringUtil.isMobilePhone(phoneString)) {
                        RxToast.showToast("请输入正确的电话号码");
                        return;
                    }
                    if (TextUtils.isEmpty(nameString)) {
                        RxToast.showToast("请输入您的姓名");
                        return;
                    } else {
                        if (TextUtils.isEmpty(countyString)) {
                            RxToast.showToast("请选择您所属区县");
                            return;
                        } else {
                            if (TextUtils.isEmpty(addressString)) {
                                RxToast.showToast("请输入详细地址");
                                return;
                            }
                        }
                    }
                }


                AddressJsonModel addressJsonModel = new AddressJsonModel();
                addressJsonModel.setMemberId(UserModel.getUserModel().getMemberId());
                addressJsonModel.setKey(key);
                addressJsonModel.setId(addressId);//不为空时更新 为空时新建 实物地址
                addressJsonModel.setName(nameString);
                addressJsonModel.setMobile(phoneString);
                addressJsonModel.setRecAddress(countyString);
                addressJsonModel.setDetAddress(addressString);
                String jsonStr = JSON.toJSONString(addressJsonModel);
                upAdRecAddr(jsonStr);

                break;
            case R.id.editText_county://选择所属区县
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

                SelectCityDialogUtils.showSelectCityDialog(cnt, new SelectCityDialogUtils.CityDialogCallback() {
                    @Override
                    public void onSuccess(String sheng, String shi, String qu) {
                        editTextCounty.setText(sheng + shi + qu);
                    }

                    @Override
                    public void onFailed(String e) {

                    }
                });
                break;
        }
    }

    /**
     * 个人信息--增加/更新 收货地址
     *
     * @param jsonStr
     */
    private void upAdRecAddr(String jsonStr) {
        OkGo.<String>post(Urls.upAdRecAddr)//
                .tag(this)//
                .params("jsonStr", jsonStr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        RxToast.showToast(data.optString("message"));
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_RECEIVING_ADDRESS_REC = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        finish();
                                    }
                                } else {
                                    RxToast.showToast("保存失败，请重试！");
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
