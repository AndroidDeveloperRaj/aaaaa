package com.merrichat.net.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by amssy on 17/7/1.
 * 修改昵称
 */

public class ChangeNickNameActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.editText_nickname_common)
    ClearEditText editText_nickname;
    @BindView(R.id.editText_signature)
    EditText editText_signature;
    @BindView(R.id.textView_number)
    TextView textView;
    @BindView(R.id.lay_edit)
    RelativeLayout lay_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        ButterKnife.bind(this);
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            initTitleBar("昵称", "保存");
            editText_nickname.setHint("请输入昵称");
            editText_nickname.setVisibility(View.VISIBLE);
            editText_nickname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); //最大输入长度
            lay_edit.setVisibility(View.GONE);
        } else if (type == 1) {
            initTitleBar("设置群名称", "保存");
            editText_nickname.setHint("请输入群名称");
            editText_nickname.setVisibility(View.VISIBLE);
            lay_edit.setVisibility(View.GONE);
        } else {
            initTitleBar("个人简介", "保存");
            String jianJieContent = getIntent().getStringExtra("jianJieContent");
            if (jianJieContent != null) {
                editText_signature.setText(jianJieContent);
                editText_signature.setSelection(jianJieContent.length());
            }
            editText_signature.setHint("请输入个人简介");
            editText_nickname.setVisibility(View.GONE);
            lay_edit.setVisibility(View.VISIBLE);
        }

    }

    @OnTextChanged(value = R.id.editText_signature, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @OnTextChanged(value = R.id.editText_signature, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {
        textView.setText(editText_signature.getText().length() + "/100");
    }

    @OnTextChanged(value = R.id.editText_signature, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged() {

    }

    /**
     * 初始化titleBar
     */
    private void initTitleBar(String title, String rtitle) {
        tvTitleText.setText(title);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(rtitle);

    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                toSave();
                break;
        }
    }

    private void toSave() {
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            if (TextUtils.isEmpty(editText_nickname.getText().toString().trim())) {
                RxToast.showToast("请输入昵称");
                return;
            }
        } else if (type == 1) {
            if (TextUtils.isEmpty(editText_nickname.getText().toString().trim())) {
                RxToast.showToast("请输入群名称");
                return;
            }
        } else {
            if (TextUtils.isEmpty(editText_signature.getText().toString().trim())) {
                RxToast.showToast("请输入个人简介");
                return;
            }
        }
        Intent intent = new Intent(ChangeNickNameActivity.this, PersonalInfoActivity.class);
        if (type == 0) {
            intent.putExtra("text", editText_nickname.getText().toString().trim());
        } else if (type == 1) {
            PrefAppStore.setGroupName(this, editText_nickname.getText().toString().trim());
            Log.e("--------->>>", "type = " + type);
        } else {
            intent.putExtra("text", editText_signature.getText().toString().trim());
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
