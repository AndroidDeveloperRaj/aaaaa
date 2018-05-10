package com.merrichat.net.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/6.
 */

public class OpinionFeedbackAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.editText_feedback)
    EditText editTextFeedback;
    @BindView(R.id.textView_number)
    TextView textViewNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_feedback);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("意见反馈");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("提交");
        editTextFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 200) {
                    RxToast.showToast("您想说的话已经到极限了！");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 200) {
                    String titleText = editTextFeedback.getText().toString().trim();
                    editTextFeedback.setText(titleText.substring(0, 200));
                    editTextFeedback.setSelection(editTextFeedback.getText().toString().trim().length());
                }
                textViewNumber.setText(editTextFeedback.getText().length() + "/200");

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.editText_feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                String contentText = editTextFeedback.getText().toString().trim();
                if (RxDataTool.isNullString(contentText)) {
                    RxToast.showToast("请输入想反馈的内容");
                } else {
                    addFeedBack(contentText);
                }
                break;
            case R.id.editText_feedback:
                break;
        }
    }

    private void addFeedBack(String contentText) {
        OkGo.<String>post(Urls.ADD_FEEDBACK).tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("content", contentText)
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("adviceUser", UserModel.getUserModel().getRealname())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                int status = jsonObjectEx.optInt("status");
                                if (status == 0) {
                                    RxToast.showToast("反馈成功！");
                                    finish();
                                } else {
                                    RxToast.showToast("提交失败，请重试！");
                                }
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
}
