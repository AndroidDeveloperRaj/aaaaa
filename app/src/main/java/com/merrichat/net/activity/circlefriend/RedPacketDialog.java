package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.EditInputFilter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by amssy on 17/8/30.
 * 秀吧详情发红包Dialog
 */

public class RedPacketDialog extends DialogFragment {
    @BindView(R.id.tv_price_packet)
    EditText tvPricePacket;
    @BindView(R.id.tv_change_price)
    TextView tvChangePrice;
    @BindView(R.id.btn_packet)
    Button btnPacket;
    @BindView(R.id.simple_header_packet)
    SimpleDraweeView simpleHeaderPacket;
    @BindView(R.id.rel_packet)
    RelativeLayout relPacket;
    @BindView(R.id.imageView_close)
    RelativeLayout imageViewClose;
    @BindView(R.id.rel_child)
    RelativeLayout relChild;
    @BindView(R.id.tv_name)
    TextView tvName;
    private String contentId;
    private String toMemberId;
    private int type = 4;//1:视频分成,2:语音,3:礼物,4:打赏
    private String title = "打赏帖子";//标题 （打赏 ，赠送礼物，视频）
    private String relName;
    private MyRedPacketActivity_Listener myRedPacketActivity_Listener;
    private Context mContext;
    private String flag = "0";

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_red_packet);
        ButterKnife.bind(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
    }*/

    public static RedPacketDialog getInstance(Context mContext, FragmentManager fm) {
        String tag = RedPacketDialog.class.getName();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            RedPacketDialog dialogFragment = (RedPacketDialog) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            dialogFragment.setCancelable(false);//外围点击 dismiss
            return dialogFragment;
        } else {
            return (RedPacketDialog) fragment;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        try {
            myRedPacketActivity_Listener = (MyRedPacketActivity_Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implementon MyDialogFragment_Listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //将对话框内部的背景设为透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);//居中显示
        //将对话框外部的背景设为透明
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
        // 去掉对话框默认标题栏
        View view = inflater.inflate(R.layout.activity_dialog_red_packet, container, false);
        ButterKnife.bind(this, view);
        simpleHeaderPacket.setImageURI(UserModel.getUserModel().getImgUrl());
        tvName.setText(UserModel.getUserModel().getRealname());

        InputFilter[] filters = {new EditInputFilter(888)};
        tvPricePacket.setFilters(filters);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contentId = arguments.getString("contentId");
            toMemberId = arguments.getString("toMemberId");
            relName = arguments.getString("relName");
            flag = arguments.getString("flag");
        }
        return view;
    }

    @OnClick({R.id.imageView_close, R.id.rel_child, R.id.btn_packet, R.id.tv_change_price})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_close:
//                finish();
                dismiss();
                break;
            case R.id.rel_child:
                break;
            case R.id.btn_packet:
                if (TextUtils.isEmpty(tvPricePacket.getText().toString().trim())) {
                    RxToast.showToast("请输入打赏金额");
                    return;
                }
                if (Double.parseDouble(tvPricePacket.getText().toString()) >= 1) {
                    if (Double.parseDouble(tvPricePacket.getText().toString()) <= 888) {
                        dashOrderPay();
                    } else {
                        RxToast.showToast("红包金额应不大于888讯美币");
                    }
                } else {
                    RxToast.showToast("红包金额应不少于1讯美币");
                }
                break;
            case R.id.tv_change_price:
                UserModel.getUserModel().getAccountId();
                if (TextUtils.equals(tvChangePrice.getText().toString(), "修改数量")) {
                    tvChangePrice.setText("取消");
                    tvPricePacket.setEnabled(true);
                    tvPricePacket.setText("");
                    //打开软键盘必须设置
                    tvPricePacket.setFocusable(true);
                    tvPricePacket.setFocusableInTouchMode(true);
                    tvPricePacket.requestFocus();
                    //打开软键盘
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    tvPricePacket.setText("1.00");
                    tvChangePrice.setText("修改数量");
                    tvPricePacket.setEnabled(false);
                    //tvPricePacket.setFocusable(false);
                }
                break;
        }
    }

    /**
     * 打赏
     */
    private void dashOrderPay() {
        OkGo.<String>get(Urls.CLIP_ORDER_PAY)
                .tag(this)
                .params("inMemberId", toMemberId)
                .params("outMemberId", UserModel.getUserModel().getMemberId())
                .params("amount", tvPricePacket.getText().toString())
                .params("title", title)
                .params("type", type)
                .params("remark", "打赏(" + relName + ")的帖子")
                .params("tieId", contentId)
                .execute(new StringDialogCallback(mContext, "正在支付中,请稍候...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //RxToast.showToast("打赏成功");
                                    if (!TextUtils.isEmpty(flag) && TextUtils.equals(flag, "1")) {
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.VIDEO_DASH_SUCCESS = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                    } else {
                                        if (myRedPacketActivity_Listener != null) {
                                            myRedPacketActivity_Listener.getDataFrom_DialogFragment(true);
                                        }
                                    }
                                    dismiss();
                                    /*setResult(RESULT_OK);
                                    finish();*/
                                } else {
                                    RxToast.showToast(data.optString("error_msg"));
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
     * 判断弹窗是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    /**
     * 显示DialogFragment(注此方法会衍生出状态值问题(本人在正常使用时并未出现过))
     *
     * @param manager
     * @param tag
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void show(FragmentManager manager, String tag, boolean isResume) {
        if (!isShowing()) {
            if (isResume) {
                //正常显示
                if (!isAdded()) {
                    show(manager, tag);
                } else {
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.show(this);
                    ft.commit();
                }
            } else {
                //注 此方法会衍生出一些状态问题,慎用（在原代码中 需要设置  mDismissed = false 和 mShownByMe = true 并未在此引用到,如果需要用到相关判断值,此方法不可用）
                FragmentTransaction ft = manager.beginTransaction();
                if (!isAdded()) {
                    ft.add(this, tag);
                } else {
                    ft.show(this);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    /**
     * 关闭DialogFragment
     *
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void dismiss(boolean isResume) {
        if (isResume) {
            dismiss();
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }

    /*
        private void initView() {
            simpleHeaderPacket.setImageURI(UserModel.getUserModel().getImgUrl());
            tvName.setText(UserModel.getUserModel().getRealname());
            Intent intent = getIntent();
            if (intent != null) {
                contentId = intent.getStringExtra("contentId");
                toMemberId = intent.getStringExtra("toMemberId");
                relName = intent.getStringExtra("relName");
            }
        }
    */
// 回调接口，用于传递数据给Activity -------
    public interface MyRedPacketActivity_Listener {
        void getDataFrom_DialogFragment(boolean isSuccess);
    }
}
