package com.merrichat.net.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.WheelTime;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.my.MinZuActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.utils.StringUtil.generateMd5;

/**
 * Created by AMSSY1 on 2017/11/6.
 * <p>
 * 身份认证
 */

public class IdentityVerificationAty extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 102;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_name_text)
    TextView tvNameText;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.rl_name)
    RelativeLayout rlName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tv_sex_text)
    TextView tvSexText;
    @BindView(R.id.tv_nation)
    TextView tvNation;
    @BindView(R.id.rl_nation)
    RelativeLayout rlNation;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @BindView(R.id.tv_id_text)
    TextView tvIdText;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.rl_id_number)
    RelativeLayout rlIdNumber;
    @BindView(R.id.tv_addrress)
    TextView tvAddrress;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.rl_detail_address)
    RelativeLayout rlDetailAddress;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.ll_linearlayout)
    LinearLayout llLinearlayout;
    @BindView(R.id.lin_sao_miao)
    LinearLayout linSaoMiao;
    @BindView(R.id.lin_message)
    LinearLayout linMessage;
    @BindView(R.id.iv_result)
    ImageView ivResult;

    private IDCardResult idCardResult;
    private ProgressDialog dialog_load;
    private boolean isClicked = false;

    private Gson gson;
    private OptionsPickerView pvCustomOptions;
    private List<String> sexList;
    private TimePickerView pvTime;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identityverification);
        ButterKnife.bind(this);
        //初始化OCR
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, getApplicationContext());

        CameraNativeHelper.init(this, OCR.getInstance().getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "加载so失败，请确保apk中存在ui部分的so";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "授权本地质量控制token获取失败";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = "本地质量控制";
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        Logger.e("本地质量控制初始化错误，错误原因： " + msg);
                    }
                });
        initView();
        initData();
    }

    private void initData() {
        gson = new Gson();
        String[] sexString = {"男", "女"};
        sexList = Arrays.asList(sexString);
    }

    private void initView() {
        tvTitleText.setText("身份认证");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("拍照");

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvRight.setText("重新拍照");
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });

        tvSex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });

        tvNation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });

        tvBirthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });

        etDetailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });

        etIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(tvSex.getText().toString())
                        && !TextUtils.isEmpty(tvNation.getText().toString()) && !TextUtils.isEmpty(tvBirthday.getText().toString())
                        && !TextUtils.isEmpty(etDetailAddress.getText().toString()) && !TextUtils.isEmpty(etIdNumber.getText().toString())) {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login_true);
                    isClicked = true;
                } else {
                    tvCommit.setBackgroundResource(R.drawable.shape_button_login);
                    isClicked = false;
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.rl_sex, R.id.rl_birthday, R.id.tv_commit, R.id.lin_sao_miao,R.id.tv_nation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                filePath = getSaveFile(getApplicationContext()).getAbsolutePath();
                //拍照
//                Intent intent = new Intent(IdentityVerificationAty.this, CameraActivity.class);
//                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
//                        getSaveFile(getApplication()).getAbsolutePath());
//                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
//                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                Intent intent = new Intent(IdentityVerificationAty.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        filePath);
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                        true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL,
                        true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);

                break;
            case R.id.lin_sao_miao:
                filePath = getSaveFile(getApplicationContext()).getAbsolutePath();
                //扫描
                Intent intent1 = new Intent(IdentityVerificationAty.this, CameraActivity.class);
                intent1.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        filePath);
                intent1.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                        true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent1.putExtra(CameraActivity.KEY_NATIVE_MANUAL,
                        true);
                intent1.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent1, REQUEST_CODE_CAMERA);
                break;
            case R.id.tv_commit:
                if (isClicked) {
                    //实名认证
                    realNameVerify();
                }
                break;
            case R.id.rl_sex:
                //关闭软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rlSex.getWindowToken(), 0);
                initCustomOptionPicker();
                pvCustomOptions.show();
                break;
            case R.id.rl_birthday:
                //关闭软键盘
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(rlBirthday.getWindowToken(), 0);
                initTimePicker();
                pvTime.show();
                break;
            case R.id.tv_nation:
                //关闭软键盘
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(tvNation.getWindowToken(), 0);
                Intent intent_nation = new Intent(this, MinZuActivity.class);
                startActivityForResult(intent_nation, MinZuActivity.activityId);
                break;
        }
    }

    private File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), System.currentTimeMillis()+"pic.jpg");
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                if (!TextUtils.isEmpty(contentType)) {
                    dialog_load = new ProgressDialog(IdentityVerificationAty.this);
                    dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_load.setCanceledOnTouchOutside(false);
                    dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog_load.setMessage("正在识别身份信息,请稍等...");
                    dialog_load.show();

                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    }
                }
            }
        }
        //选择名族返回值
        if (requestCode == MinZuActivity.activityId && data != null) {
            String minZuText = data.getStringExtra("minZuText");
            tvNation.setText(minZuText);
        }
    }

    private void recIDCard(String idCardSide, final String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                llLinearlayout.setVisibility(View.VISIBLE);
                linSaoMiao.setVisibility(View.GONE);
                ivResult.setVisibility(View.VISIBLE);

                ivResult.setImageURI(Uri.parse(filePath));

                tvCommit.setVisibility(View.VISIBLE);
                if (result != null) {
                    dialog_load.dismiss();
                    idCardResult = result;
                    etName.setText("" + idCardResult.getName());
                    tvSex.setText("" + idCardResult.getGender().toString());
                    tvNation.setText("" + idCardResult.getEthnic().toString());
                    tvBirthday.setText("" + idCardResult.getBirthday().toString());
                    etDetailAddress.setText("" + idCardResult.getAddress().toString());
                    etIdNumber.setText("" + idCardResult.getIdNumber().toString());
                }
            }

            @Override
            public void onError(OCRError error) {
                Logger.e("" + error.getMessage());
                dialog_load.dismiss();
                RxToast.showToast("识别失败，请重新扫描");
            }
        });
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String xueXing = sexList.get(options1);
                tvSex.setText(xueXing);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        TextView tvPopTitle = (TextView) v.findViewById(R.id.title);
                        final TextView tvOk = (TextView) v.findViewById(R.id.ok);
                        TextView tvCancel = (TextView) v.findViewById(R.id.cancel);

                        tvPopTitle.setText("性别");
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });


                    }
                })
                .setSelectOptions(2)
                .setOutSideCancelable(false)
                .isDialog(true)
                .build();
        pvCustomOptions.setPicker(sexList);//添加数据}


    }

    /**
     * 时间选择器
     */
    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2019, 11, 28);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                tvBirthday.setText(RxTimeTool.date2String(date, new SimpleDateFormat("yyyy-MM-dd")));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
//                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }

    /**
     * 实名认证
     */
    private void realNameVerify() {
        dialog_load = new ProgressDialog(IdentityVerificationAty.this);
        dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_load.setCanceledOnTouchOutside(false);
        dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog_load.setMessage("正在认证身份信息,请稍等...");
        dialog_load.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String key = StringUtil.getBase64("memberId:"+UserModel.getUserModel().getMemberId()+"|mobile:"+UserModel.getUserModel().getMobile()+"|accountId:"+UserModel.getUserModel().getAccountId()+"|cardNo:"+etIdNumber.getText().toString()+"|realName:"+etName.getText().toString() +"|nation:"+tvNation.getText().toString());
        String encStr = generateMd5(UserModel.getUserModel().getMemberId() + etIdNumber.getText().toString(), "merriChat@0!8" + timeStamp);
        OkGo.<String>post(Urls.realNameVerify)
                .params("key", key)
                .params("encStr", encStr)
                .params("timeStamp", timeStamp)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            dialog_load.dismiss();
                            if (jsonObject.optBoolean("success")) {
                                //状态码0:当前身份证号已被认证 1:实名认证通过 2:实名认证失败
                                int status = jsonObject.optJSONObject("data").optInt("status");
                                switch (status){
                                    case 0:
                                        break;
                                    case 1:
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_NOTICE_ATY = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        RxActivityTool.skipActivity(IdentityVerificationAty.this,IdentityVerificationSuccessAty.class);
                                        finish();
                                        break;
                                    case 2:
                                        break;
                                }
                                RxToast.showToast(jsonObject.optJSONObject("data").optString("msg"));
                            }else {
                                RxToast.showToast(jsonObject.optString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog_load.dismiss();
                        }
                    }
                });
    }
}
