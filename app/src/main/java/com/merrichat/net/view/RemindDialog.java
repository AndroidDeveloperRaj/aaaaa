package com.merrichat.net.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;


public class RemindDialog extends Dialog implements View.OnClickListener {
    private TextView tv_dialog_title;
    private ImageView title_line;
    private TextView tv_content;
    private TextView tv_why;
    private RelativeLayout rl_left_btn_info;
    private LinearLayout ll_rd_content;//提示内容所在布局
    private LinearLayout ll_btn_layout;//按钮所在布局
    private LinearLayout ll_root;//根布局
    private Button btn_no;
    private Button btn_yes;
    private DialogOnClickListener listener;
    private boolean isSetContentHeight;//是否要改变控件内容的高度。

    public RemindDialog(Context context) {
        super(context, R.style.dialog2);
        inflateView(false);
    }

    public RemindDialog(Context mContext, boolean isSetContentHeight) {
        super(mContext, R.style.dialog2);
        this.isSetContentHeight = isSetContentHeight;
        inflateView(isSetContentHeight);
    }

    private void inflateView(boolean isSetContentHeight) {
        setContentView(R.layout.dialog_remind_view);
        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        title_line = (ImageView) findViewById(R.id.title_line);
        tv_why = (TextView) findViewById(R.id.tv_why);
        rl_left_btn_info = (RelativeLayout) findViewById(R.id.rl_left_btn_info);
        ll_rd_content = (LinearLayout) findViewById(R.id.ll_rd_content);
        ll_btn_layout = (LinearLayout) findViewById(R.id.ll_btn_layout);
        ll_root = (LinearLayout) findViewById(R.id.ll_root);

        if (isSetContentHeight) {
            ll_rd_content.setMinimumHeight(180);
            ll_btn_layout.setMinimumHeight(60);
            ll_root.setMinimumHeight(160);
        }

        btn_no = (Button) findViewById(R.id.btn_no);
        btn_yes = (Button) findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
    }

    public void setDialogOnClickListener(DialogOnClickListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        tv_dialog_title.setText(title);
    }

    public void setTitleVis(boolean exitTitle) {
        if (exitTitle) {
            tv_dialog_title.setVisibility(View.VISIBLE);
            title_line.setVisibility(View.VISIBLE);
        } else {
            tv_dialog_title.setVisibility(View.GONE);
            title_line.setVisibility(View.GONE);
        }

    }

    public void setContent(String str) {
        tv_content.setText(str);
    }

    public void setContent(CharSequence str) {
        tv_content.setText(str);
    }

    public void setWhy(String str) {
        tv_why.setText(str);
    }

    public void setWhyVisibility(int visibility) {
        tv_why.setVisibility(visibility);
    }

    /**
     * 隐藏或显示左边按钮
     *
     * @param flag
     */
    public void hintButtonLeft(int flag) {
        rl_left_btn_info.setVisibility(flag);
    }

    public void setButtonYes(String str) {
        btn_yes.setText(str);
    }

    public void setButtonNo(String str) {
        rl_left_btn_info.setVisibility(View.VISIBLE);
        btn_no.setText(str);
    }

    /**
     * 设置确定按钮的点击事件
     *
     * @param isEnable
     */
    public void setButtonISEnabled(boolean isEnable) {
        btn_yes.setEnabled(isEnable);
    }

    /**
     * 得到确定按钮的点击事件
     *
     * @param
     */
    public boolean getButtonClickable() {
        return btn_yes.isClickable();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_no:
                if (listener != null) {
                    listener.No();
                }
                break;
            case R.id.btn_yes:
                if (listener != null) {
                    listener.Yes();
                }
                break;
        }
    }

}
