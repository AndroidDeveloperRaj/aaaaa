package com.merrichat.net.activity.video;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;

public class MerriActionBarActivity extends BaseActivity {
    private FrameLayout fl_content;
    protected TextView tv_center;
    protected Button bt_right;
    protected Button bt_left;
    protected ImageView iv_left;
    protected ImageView iv_right;


    public RelativeLayout rl_actionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_merri_actionbar);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        bt_right = (Button) findViewById(R.id.bt_right);
        bt_left = (Button) findViewById(R.id.bt_left);
        rl_actionbar = (RelativeLayout) findViewById(R.id.rl_actionbar);
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, fl_content);
    }

    /**
     * 用来设置页面标题
     */
    public void setTitle(String title) {
        tv_center.setText(title);
        tv_center.setVisibility(View.VISIBLE);
    }

    public void setTitle(CharSequence text, TextView.BufferType type){
        tv_center.setText(text,type);
        tv_center.setVisibility(View.VISIBLE);
    }

    /**
     * 适用于文字透明度不一样
     * @param btnText
     * @param
     */
    public void setRightText_(String btnText, int color) {
        bt_right.setText(btnText);
        bt_right.setTextColor(color);
        //bt_right.setOnClickListener(onClickListener);
        bt_right.setVisibility(View.VISIBLE);
        iv_right.setVisibility(View.GONE);
    }

    /**
     * 用于设置左侧图片按钮，如果是正常返回键使用setLeftBack()
     * @param imgRes imgRes
     * @param onClickListener onClickListener
     */
    public void setLeftImage(int imgRes, View.OnClickListener onClickListener) {
        iv_left.setImageResource(imgRes);
        iv_left.setOnClickListener(onClickListener);
        iv_left.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.GONE);
    }

    /**
     * 设置左侧为文本按钮
     * @param btnText btnText
     * @param onClickListener onClickListener
     */
    public void setLeftText(String btnText, View.OnClickListener onClickListener) {
        bt_left.setText(btnText);
        bt_left.setOnClickListener(onClickListener);
        bt_left.setVisibility(View.VISIBLE);
        iv_left.setVisibility(View.GONE);
    }

    /**
     * 设置左侧为正常返回键，常用
     */
    public void setLeftBack() {
        setLeftImage(R.mipmap.fanhui_button_tongyong2x, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 设置右侧为图片按钮
     * @param imgRes imgRes
     * @param onClickListener onClickListener
     */
    public void setRightImage(int imgRes, View.OnClickListener onClickListener) {
        if (imgRes <= 0 && onClickListener == null) {  // 隐藏显示
            iv_right.setImageBitmap(null);
            iv_right.setOnClickListener(null);
            iv_right.setVisibility(View.GONE);
        } else {
            iv_right.setImageResource(imgRes);
            iv_right.setOnClickListener(onClickListener);
            iv_right.setVisibility(View.VISIBLE);
        }
        bt_right.setVisibility(View.GONE);
    }

    /**
     * 设置右侧为文本按钮
     * @param btnText btnText
     * @param onClickListener onClickListener
     */
    public void setRightText(String btnText, int color,View.OnClickListener onClickListener) {
        bt_right.setText(btnText);
        bt_right.setTextColor(cnt.getResources().getColor(color));
        bt_right.setOnClickListener(onClickListener);
        bt_right.setVisibility(View.VISIBLE);
        iv_right.setVisibility(View.GONE);
    }

    public void setBtnRightListener(View.OnClickListener onClickListener){
        bt_right.setOnClickListener(onClickListener);
    }

    public void showRightText(String text){
        bt_right.setText(text);
        bt_right.setVisibility(View.VISIBLE);
    }

    public void hideRightText(){
        bt_right.setVisibility(View.GONE);
    }

    /**
     * 设置标题栏隐藏
     */
    public void setTitleGone(){
        rl_actionbar.setVisibility(View.GONE);
    }


    /**
     * 设置分割线隐藏
     */
    public void setLineGone(){
//        iv_line.setVisibility(View.GONE);
    }


}
