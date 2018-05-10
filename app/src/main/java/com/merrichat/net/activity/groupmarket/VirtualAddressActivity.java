package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
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
import com.merrichat.net.model.AboutHomeSettingModel;
import com.merrichat.net.model.AddressJsonModel;
import com.merrichat.net.model.AddressModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 虚拟地址
 * Created by amssy on 18/1/23.
 */
public class VirtualAddressActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;

    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.editText_name)
    EditText editTextName;
    /**
     * 地址
     */
    @BindView(R.id.editText_detail_address)
    EditText editTextDetailAddress;
    /**
     * 重复地址
     */
    @BindView(R.id.et_re_detail_address)
    EditText etReDetailAddress;
    private String type;
    private String key = "vir";//vir:表示的虚拟的收货地址, rec:表示的实物的收货地址
    private String addressId;// 地址ID
    private int REQUEST_CODE_ACCOUNTTYPE = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_address);
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
                tvPhone.setText(addressModel.getType());
                editTextName.setText(addressModel.getName());
                etReDetailAddress.setText(addressModel.getAccountAddr());
                editTextDetailAddress.setText(addressModel.getAccountAddr());
            }
            if (RxDataTool.isNullString(addressId)) addressId = "";
        }
        if (type.equals("editor")) {
            tvTitleText.setText("其他账号");
        } else {
            tvTitleText.setText("其他账号");
        }
        tvRight.setText("保存");
        tvRight.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.iv_back, R.id.tv_phone, R.id.tv_right})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                String phoneString = tvPhone.getText().toString().trim();
                String nameString = editTextName.getText().toString().trim();
                String addressString = editTextDetailAddress.getText().toString().trim();
                String etReDetailAddressString = etReDetailAddress.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    RxToast.showToast("请输入账号类型");
                    return;
                } else {
                    if (TextUtils.isEmpty(nameString)) {
                        RxToast.showToast("请输入账号名称");
                        return;
                    } else {
                        if (TextUtils.isEmpty(addressString)) {
                            RxToast.showToast("请输入详细地址");
                            return;
                        } else {
                            if (TextUtils.isEmpty(etReDetailAddressString)) {
                                RxToast.showToast("请在此输入详细地址");
                                return;
                            }
                        }
                    }
                }
                if (!addressString.equals(etReDetailAddressString)) {
                    RxToast.showToast("请确保账号地址一致！");
                    return;
                }
                AddressJsonModel addressJsonModel = new AddressJsonModel();
                addressJsonModel.setMemberId(UserModel.getUserModel().getMemberId());
                addressJsonModel.setKey(key);
                addressJsonModel.setId(addressId);//不为空时更新 为空时新建 实物地址
                addressJsonModel.setName(nameString);
                addressJsonModel.setType(phoneString);
                addressJsonModel.setAccountAddr(addressString);
                String jsonStr = JSON.toJSONString(addressJsonModel);
                upAdRecAddr(jsonStr);
                break;
            case R.id.tv_phone:
                RxActivityTool.skipActivityForResult(VirtualAddressActivity.this, AccountTypeAty.class, REQUEST_CODE_ACCOUNTTYPE);
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
                                        myEventBusModel.REFRESH_RECEIVING_ADDRESS_VIR = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACCOUNTTYPE) {
            if (resultCode == RESULT_OK && data != null) {
                AboutHomeSettingModel aboutHomeSettingModel = (AboutHomeSettingModel) data.getSerializableExtra("to_account_type");
                tvPhone.setText(aboutHomeSettingModel.getSettingItemName());
            }
        }
    }
}
