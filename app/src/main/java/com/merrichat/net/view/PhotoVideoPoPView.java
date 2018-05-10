package com.merrichat.net.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.merrichat.net.R;
import com.merrichat.net.utils.RxTools.RxDeviceTool;

/**
 * Created by AMSSY1 on 2017/11/21.
 */

public class PhotoVideoPoPView extends PopupWindow {
    // 坐标的位置（x、y）
    private final int[] mLocation = new int[2];
    private final Context mContext;
    // 实例化一个矩形
    private Rect mRect = new Rect();
    // 屏幕的宽度和高度
    private int mScreenWidth, mScreenHeight;
    // 判断是否需要添加或更新列表子类项
    private boolean mIsDirty;
    // 位置不在中心
    private int popupGravity = Gravity.NO_GRAVITY;
    private TextContentOnclickListener textContentOnclickListener;
    private PicContentOnclickListener picContentOnclickListener;

    public PhotoVideoPoPView(Context context, int width, int height, int layout) {
        this.mContext = context;

        // 设置可以获得焦点
        setFocusable(true);
        // 设置弹窗内可点击
        setTouchable(true);
        // 设置弹窗外可点击
        setOutsideTouchable(true);

        // 获得屏幕的宽度和高度
        mScreenWidth = RxDeviceTool.getScreenWidth(mContext);
        mScreenHeight = RxDeviceTool.getScreenHeight(mContext);

        // 设置弹窗的宽度和高度
        setWidth(width);
        setHeight(height);

        setBackgroundDrawable(new BitmapDrawable());

        // 设置弹窗的布局界面
        View contentView = LayoutInflater.from(mContext).inflate(
                layout, null);
        setContentView(contentView);
        initUI(contentView);
    }

    private void initUI(View contentView) {
        final View picContent = contentView.findViewById(R.id.iv_add_piccontent);
        View textContent = contentView.findViewById(R.id.iv_add_textcontent);
        picContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (picContentOnclickListener != null) {
                    picContentOnclickListener.picContentOnclickListener();
                }

            }
        });
        textContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (textContentOnclickListener != null) {
                    textContentOnclickListener.textContentOnclickListener();
                }

            }
        });

    }

    /**
     * 显示弹窗列表界面
     */
    public void show(View view, int dex) {
        // 获得点击屏幕的位置坐标
        view.getLocationOnScreen(mLocation);

        // 设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0],
                mLocation[1]);

        // 显示弹窗的位置
        showAtLocation(view, popupGravity, (mLocation[0] + (mScreenWidth / 2 - mLocation[0])) - dex, mRect.top);
    }


    public void setPicContentOnclickListener(
            PicContentOnclickListener picContentOnclickListener) {
        this.picContentOnclickListener = picContentOnclickListener;
    }

    public void setTextContentOnclickListener(
            TextContentOnclickListener textContentOnclickListener) {
        this.textContentOnclickListener = textContentOnclickListener;
    }

    public interface PicContentOnclickListener {
        void picContentOnclickListener();
    }

    public interface TextContentOnclickListener {
        void textContentOnclickListener();
    }
}
