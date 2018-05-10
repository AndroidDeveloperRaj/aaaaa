package com.merrichat.net.activity.picture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxKeyboardTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/22.
 */

public class PhotoVideoEditTextAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.et_editcontenttext)
    EditText etEditcontenttext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideo_edit);
        ButterKnife.bind(this);
        initTitle();
        initData();
    }

    private void initData() {
        String editContentText = getIntent().getStringExtra("editContentText");
        etEditcontenttext.setText(editContentText);
        if (!RxDataTool.isNullString(etEditcontenttext.getText().toString().trim())) {

            etEditcontenttext.setSelection(editContentText.length());
        }
    }

    private void initTitle() {
        tvTitleText.setText("编辑文字");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        RxKeyboardTool.hideSoftInput(this);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                String contentText = etEditcontenttext.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("editContentText", contentText);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
