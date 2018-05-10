package com.merrichat.net.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.merrichat.net.R;
import com.merrichat.net.utils.RxTools.RxToast;

/**
 * Created by amssy on 17/11/7.
 */

public class CameraSettingPopuWindow extends PopupWindow {

    private View conentView;
    private View clickView;
    private OnPopuCameraSettingClick onPopuCameraSettingClick;
    private LinearLayout lay_timer_down, lay_flash_light, lay_zidong;
    private TextView tv_timer_down, tv_flash_light, tv_zidong;
    private ImageView iv_timer_down, iv_flash_light;
    private boolean is_timer_down_open, is_flash_light_open, isTakePhoto, isZidong;
    private SwitchButton switchButton;

    public CameraSettingPopuWindow(final Activity context, boolean is_timer_down_open_, boolean is_flash_light_open_, final int currentCameraType, boolean isTakePhoto_, boolean isZidong_, View clickView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.layout_popu_content_top_arrow, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popwindow_anim_camera_setting);
        this.is_timer_down_open = is_timer_down_open_;
        this.is_flash_light_open = is_flash_light_open_;
        this.isTakePhoto = isTakePhoto_;
        this.isZidong = isZidong_;
        this.clickView = clickView;

        lay_timer_down = (LinearLayout) conentView.findViewById(R.id.lay_timer_down);
        lay_flash_light = (LinearLayout) conentView.findViewById(R.id.lay_flash_light);
        tv_timer_down = (TextView) conentView.findViewById(R.id.tv_timer_down);
        tv_flash_light = (TextView) conentView.findViewById(R.id.tv_flash_light);
        iv_timer_down = (ImageView) conentView.findViewById(R.id.iv_timer_down);
        iv_flash_light = (ImageView) conentView.findViewById(R.id.iv_flash_light);

        tv_zidong = (TextView) conentView.findViewById(R.id.tv_zidong);
        lay_zidong = (LinearLayout) conentView.findViewById(R.id.lay_zidong);
        switchButton = (SwitchButton) conentView.findViewById(R.id.sb_ios);


        initUI(context);
        lay_timer_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateTimerLayoutUI(context);
                is_timer_down_open = !is_timer_down_open;
                if (onPopuCameraSettingClick != null) {
                    onPopuCameraSettingClick.onTimerDownSelet(is_timer_down_open);
                }
            }
        });
        lay_flash_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    RxToast.showToast("前置摄像头无法开启闪光灯");
                    return;
                }
                updateFlashLayoutUI(context);
                is_flash_light_open = !is_flash_light_open;
                if (onPopuCameraSettingClick != null) {
                    onPopuCameraSettingClick.onFlashLight(is_flash_light_open);
                }
            }
        });
//        tv_zidong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (tv_zidong.getText().equals("自动保存/关")) {
//                    tv_zidong.setText("自动保存/开");
//                    onPopuCameraSettingClick.onZidong(true);
//                } else {
//                    tv_zidong.setText("自动保存/关");
//                    onPopuCameraSettingClick.onZidong(false);
//                }
//            }
//        });
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    tv_zidong.setText("自动保存/开");
//                } else {
//                    tv_zidong.setText("自动保存/关");
//                }
                onPopuCameraSettingClick.onZidong(isChecked);
            }
        });
    }


    private void initUI(Activity context) {

        if (isTakePhoto) {
            lay_zidong.setVisibility(View.VISIBLE);
        } else {
            lay_zidong.setVisibility(View.GONE);
        }

        if (!is_timer_down_open) {
            tv_timer_down.setTextColor(context.getResources().getColor(R.color.white));
            iv_timer_down.setImageResource(R.mipmap.icon_timer_down_off);
        } else {
            tv_timer_down.setTextColor(context.getResources().getColor(R.color.normal_red));
            iv_timer_down.setImageResource(R.mipmap.icon_timer_down_open);
        }
        if (!is_flash_light_open) {
            tv_flash_light.setTextColor(context.getResources().getColor(R.color.white));
            iv_flash_light.setImageResource(R.mipmap.icon_flash_off);
        } else {
            tv_flash_light.setTextColor(context.getResources().getColor(R.color.normal_red));
            iv_flash_light.setImageResource(R.mipmap.icon_flash_open);
        }
        switchButton.setChecked(isZidong);
    }

    private void updateTimerLayoutUI(Activity context) {
        if (is_timer_down_open) {
            tv_timer_down.setTextColor(context.getResources().getColor(R.color.white));
            iv_timer_down.setImageResource(R.mipmap.icon_timer_down_off);
        } else {
            tv_timer_down.setTextColor(context.getResources().getColor(R.color.normal_red));
            iv_timer_down.setImageResource(R.mipmap.icon_timer_down_open);
        }
    }

    private void updateFlashLayoutUI(Activity context) {
        if (is_flash_light_open) {
            tv_flash_light.setTextColor(context.getResources().getColor(R.color.white));
            iv_flash_light.setImageResource(R.mipmap.icon_flash_off);
        } else {
            tv_flash_light.setTextColor(context.getResources().getColor(R.color.normal_red));
            iv_flash_light.setImageResource(R.mipmap.icon_flash_open);
        }
    }

    public void setOnPopuCameraSettingClick(OnPopuCameraSettingClick click) {
        this.onPopuCameraSettingClick = click;
    }

    public interface OnPopuCameraSettingClick {
        void onFlashLight(boolean isOpen);

        void onTimerDownSelet(boolean isOpen);

        void onDismiss();

        void onZidong(boolean isOpen);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        clickView.setEnabled(true);
        onPopuCameraSettingClick.onDismiss();
    }
}
