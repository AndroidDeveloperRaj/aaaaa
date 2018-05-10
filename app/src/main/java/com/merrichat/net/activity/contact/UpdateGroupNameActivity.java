package com.merrichat.net.activity.contact;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.RxTools.RxToast;

public class UpdateGroupNameActivity extends MerriActionBarActivity {
    private EditText mEditText; //群名称
    private int type;  //根据type判断当前字符从哪个界面传过来的
    private String saveString = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_name);
        setLeftBack();

        type = getIntent().getIntExtra("type", -1);

        mEditText = (EditText) findViewById(R.id.et_name);


        switch (type) {
            case 0:
                setTitle("昵称");
                saveString = "保存";
                SpannableString nickName = new SpannableString("请输入昵称");//这里输入自己想要的提示文字
                mEditText.setHint(nickName);
                break;
            case 1:
                saveString = "保存";
                setTitle("设置群名称");
                SpannableString s = new SpannableString("请输入群名称");//这里输入自己想要的提示文字
                mEditText.setHint(s);
                break;
        }

        mEditText = (EditText) findViewById(R.id.et_name);


        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = mEditText.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
                //如果是汉字则为两个字符
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，
                    //如果在这个范围内则算一个字符，
                    //如果不在这个范围比如是汉字的话就是两个字符
                    if (charAt >= 32 && charAt <= 122) {
                        mTextMaxlenght++;
                    } else {
                        mTextMaxlenght += 2;
                    }
                    // 当最大字符大于32时，进行字段的截取，并进行提示字段的大小
                    if (mTextMaxlenght > 32) {
                        // 截取最大的字段
                        String newStr = str.substring(0, i);
                        mEditText.setText(newStr);
                        // 得到新字段的长度值
                        editable = mEditText.getText();
                        int newLen = editable.length();
                        if (selEndIndex > newLen) {
                            selEndIndex = editable.length();
                        }
                        // 设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setRightText(saveString, R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSave();
            }
        });

    }

    private void toSave() {

        if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            switch (type) {
                case 0:
                    RxToast.showToast("请输入昵称");
                    break;
                case 1:
                    RxToast.showToast("请输入群名称");
                    break;
            }
            return;
        } else {
            String temp = mEditText.getText().toString().trim();
            if ((temp.contains("讯") && temp.contains("美")) || temp.contains("merrichat") || temp.contains("m e r r i c h a t")) {
                RxToast.showToast("输入的群昵称不合法，请重新输入");
                return;
            }
        }

        PrefAppStore.setGroupName(this, mEditText.getText().toString().trim());
        finish();
    }


}
