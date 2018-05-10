package com.merrichat.net.activity.merrifunction;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.picture.fragments.MagicTackPhotoStyleFrgment;
import com.merrichat.net.adapter.EffectAndFilterSelectAdapter;
import com.merrichat.net.utils.AnimUtils;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NoDoubleClickListener;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.DurationSelectPopuWindow;
import com.merrichat.net.view.MyCountTimer;
import com.merrichat.net.view.SteppingProgressBar;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/10/25.
 * 相机的基类
 */

public abstract class IRecordVideoUIAty extends AppCompatActivity {
    /**
     * 录制进度条
     */
    @BindView(R.id.step_bar)
    SteppingProgressBar stepBar;
    /**
     * 倒计时
     */
    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    /**
     * 返回按钮
     */
    @BindView(R.id.iv_close)
    ImageView ivClose;
    /**
     * 设置
     */
    @BindView(R.id.iv_camera_setting)
    ImageView ivCameraSetting;
    /**
     * 前后切换
     */
    @BindView(R.id.iv_camera_switch)
    ImageView ivCameraSwitch;
    /**
     * 录制时长秒
     */
    @BindView(R.id.tv_recorded_time)
    TextView tvRecordedTime;
    /**
     * 时长类型切换
     */
    @BindView(R.id.tv_duration_type)
    TextView tvDurationType;

    @BindView(R.id.lay_speed)
    LinearLayout laySpeed;

    /**
     * 极慢
     */
    @BindView(R.id.tv_tab_very_slow)
    TextView tvTabVerySlow;
    /**
     * 慢
     */
    @BindView(R.id.tv_tab_slow)
    TextView tvTabSlow;
    /**
     * 标准速度
     */
    @BindView(R.id.tv_tab_standard_speed)
    TextView tvTabStandardSpeed;
    /**
     * 快
     */
    @BindView(R.id.tv_tab_fast)
    TextView tvTabFast;
    /**
     * 极快
     */
    @BindView(R.id.tv_tab_very_fast)
    TextView tvTabVeryFast;

    /**
     * 美颜
     */
    @BindView(R.id.iv_record_beautify)
    TextView ivRecordBeautify;
    /**
     * 魔拍
     */
    @BindView(R.id.iv_record_magic)
    TextView ivRecordMagic;
    /**
     * 底部按钮布局
     */
    @BindView(R.id.lay_bottom)
    RelativeLayout layBottom;
    /**
     * 回删
     */
    @BindView(R.id.iv_record_dele)
    ImageView ivRecordDele;
    /**
     * 完成
     */
    @BindView(R.id.iv_record_complete)
    ImageView ivRecordComplete;

    @BindView(R.id.tv_touch_beautify)
    TextView tvTouchBeautify;
    @BindView(R.id.tv_touch_magic)
    TextView tvTouchMagic;

    /**************************************************************
     ****************************美颜布局****************************
     **************************************************************/
    /**
     * tab,滤镜，磨皮，美白，瘦脸，大眼
     */
    @BindView(R.id.tv_tab_filter)
    TextView tvTabFilter;
    @BindView(R.id.tv_tab_blur_level)
    TextView tvTabBlurLevel;
    @BindView(R.id.tv_tab_color)
    TextView tvTabColor;
    @BindView(R.id.tv_tab_cheekthin_level)
    TextView tvTabCheekthinLevel;
    @BindView(R.id.tv_tab_enlarge_eye)
    TextView tvTabEnlargeEye;


    /**
     * tab指示器
     */
    @BindView(R.id.v_indicator_beautify)
    View vIndicatorBeautify;
    /**
     * 滤镜列表
     */
    @BindView(R.id.rv_filter)
    RecyclerView rvFilter;

    /**
     * 磨皮
     */
    @BindView(R.id.lay_blur_level)
    LinearLayout layBlurLevel;
    @BindView(R.id.lay_top)
    RelativeLayout layTop;
    /**
     * HOT按钮
     */
    @BindView(R.id.btn_hot)
    Button btnHot;

    private Button[] mBtnBlurLevels;
//    private int[] BLUR_LEVEL_BTN_ID = {R.id.btn_blur_level0, R.id.btn_blur_level1, R.id.btn_blur_level2,
//            R.id.btn_blur_level3, R.id.btn_blur_level4, R.id.btn_blur_level5, R.id.btn_blur_level6};
    private int[] BLUR_LEVEL_BTN_ID = {R.id.btn_blur_level0, R.id.btn_blur_level1, R.id.btn_blur_level2,
            R.id.btn_blur_level3, R.id.btn_blur_level4, R.id.btn_blur_level5};
    /**
     * 美白
     */
    @BindView(R.id.lay_color_level)
    LinearLayout layColorLevel;
    @BindView(R.id.sb_color_level)
    BubbleSeekBar sbColorLevel;
    /**
     * 瘦脸
     */
    @BindView(R.id.lay_cheekthin_level)
    LinearLayout layCheekthinLevel;
    @BindView(R.id.sb_cheekthin_level)
    BubbleSeekBar sbCheekthinLevel;
    /**
     * 大眼
     */
    @BindView(R.id.lay_enlarge_eye_level)
    LinearLayout layEnlargeEyeLevel;
    @BindView(R.id.sb_enlarge_eye_level)
    BubbleSeekBar sbEnlargeEyeLevel;

    /**
     * 总容器
     */
    @BindView(R.id.lay_beautify_control)
    LinearLayout layBeautifyControl;
    /**************************************************************
     ***************************美颜布局****************************
     **************************************************************/

    /**************************************************************
     ***************************魔拍布局****************************
     **************************************************************/
//    @BindView(R.id.tv_tab_new)
//    TextView tvTabNew;
//    @BindView(R.id.tv_tab_hot)
//    TextView tvTabHot;
//    @BindView(R.id.tv_tab_love)
//    TextView tvTabLove;
//    @BindView(R.id.tv_tab_cool)
//    TextView tvTabCool;
//    @BindView(R.id.tv_tab_taste)
//    TextView tvTabTaste;
//    @BindView(R.id.v_indicator_magic)
//    View vIndicatorMagic;
//    @BindView(R.id.rv_new)
//    RecyclerView rvNew;
    @BindView(R.id.lay_magic_control)
    LinearLayout layMagicControl;
    /**************************************************************
     ***************************魔拍布局****************************
     **************************************************************/
    /**
     * 录制按钮
     */
    @BindView(R.id.iv_tp_take)
    ImageView ivTpTake;


    private int windowWidth = 0;
    private static int tabNums = 5;//tab个数
    private int indicatorWidth = 24;//一个字12dp，两个字24dp
    private int paddingLeft = 0;


    /**
     * 延时
     */
    private MyCountTimer myCountTimer;
    public boolean is_timer_down_open;
    public boolean is_flash_light_open;
    //滤镜adapter
    private EffectAndFilterSelectAdapter mFilterRecyclerAdapter;

    private int recordDuration = DurationSelectPopuWindow.duration[0];
    private int recordBiaoZhunDuration = DurationSelectPopuWindow.duration[0];
    private boolean mIsStopped = true;//是不是正在录制 true是停止状态，false是在录制状态
    private int mProgress;//录制进度
    private Context mContext;
    public boolean isHasRecorded = false;//是否还有视频段

    //    public List<Float> speedTypeList = new ArrayList<>();//视频段的速度
    public float currentSpeed = 1.0f;//视频段的速度
    private float lastPartTime = 0.0f;
    private float pausePartTime = 0.0f;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setProgress();
        }
    };
    private CommomDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record_video_faceunity);
        ButterKnife.bind(this);

        //设置拍照界面亮度
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);

        mContext = this;
        initBeautifyUI();
        initMagicUI();
        stepBar.setMax(recordDuration * 1000);
        stepBar.setOnRecordingCallbackListener(new SteppingProgressBar.OnRecordingCallbackListener() {
            @Override
            public void onRecording(float currentRecordedTimes) {
                tvRecordedTime.setText(currentRecordedTimes + "秒");
            }
        });

        MagicTackPhotoStyleFrgment magicTackPhotoStyleFrgment = new MagicTackPhotoStyleFrgment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_magic_control, magicTackPhotoStyleFrgment)
                .commitAllowingStateLoss();

        ivTpTake.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                ivTpTake.setEnabled(false);
                if (mIsStopped) {
                    if (is_timer_down_open) {
                        myCountTimer = new MyCountTimer(4000, 1000, tvCountDown, "");
                        myCountTimer.start();
                        myCountTimer.setOnTimeDownListener(new MyCountTimer.DownTimeWatcher() {
                            @Override
                            public void onTime(int num) {

                            }

                            @Override
                            public void onDownTimeFinish() {
                                laySpeed.setVisibility(View.GONE);
                                tvDurationType.setVisibility(View.GONE);
                                ivRecordDele.setVisibility(View.VISIBLE);
                                ivRecordComplete.setVisibility(View.VISIBLE);
                                layTop.setVisibility(View.GONE);
                                ivTpTake.setImageResource(R.mipmap.icon_record_video_pause);
                                mIsStopped = false;
                                ivTpTake.setEnabled(true);
                                startProgress();
                                isHasRecorded = true;
//                                speedTypeList.add(currentSpeed);
                                onStartRecording();
                            }
                        });
                    } else {
                        laySpeed.setVisibility(View.GONE);
                        tvDurationType.setVisibility(View.GONE);
                        ivRecordDele.setVisibility(View.VISIBLE);
                        ivRecordComplete.setVisibility(View.VISIBLE);
                        layTop.setVisibility(View.GONE);
                        ivTpTake.setImageResource(R.mipmap.icon_record_video_pause);
                        ivTpTake.setEnabled(true);
                        mIsStopped = false;
                        startProgress();
                        isHasRecorded = true;
//                        speedTypeList.add(currentSpeed);
                        onStartRecording();
                    }
                } else {
                    pausePartTime = Float.valueOf(tvRecordedTime.getText().toString().replace("秒", ""));
                    if (pausePartTime - lastPartTime < 3) {
                        RxToast.showToast("视频段至少3秒以上");
                        ivTpTake.setEnabled(true);
                    } else {
                        ivTpTake.setEnabled(true);
//                        laySpeed.setVisibility(View.VISIBLE);
                        ivTpTake.setImageResource(R.mipmap.icon_record_take);
                        mIsStopped = true;
                        lastPartTime = Float.valueOf(tvRecordedTime.getText().toString().replace("秒", ""));
                        layTop.setVisibility(View.VISIBLE);
                        stopProgress();
                        onStopRecording();
                    }
                }
            }
        });
    }

    /**
     * 初始化美颜布局及全部
     */
    private void initBeautifyUI() {
        initIndicator();
        initBlurOnClick();
        initSBChange();
        //滤镜列表
        rvFilter.setLayoutManager(new GridLayoutManager(this, 4));
        mFilterRecyclerAdapter = new EffectAndFilterSelectAdapter(rvFilter, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_FILTER);
        mFilterRecyclerAdapter.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int itemPosition) {
                onFilterSelected(EffectAndFilterSelectAdapter.FILTERS_NAME[itemPosition]);
            }
        });
        rvFilter.setAdapter(mFilterRecyclerAdapter);
        //滤镜列表

        indicatorRunAnim(vIndicatorBeautify, 0);
        setBeautifySelectTabTextColor(tvTabFilter);
        setBeautifySelectLayoutVisible(rvFilter);

    }

    private void initSBChange() {
        sbColorLevel.setProgress(80);
        //美白
        sbColorLevel.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                onColorLevelSelected(progress, 100);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }
        });
        sbCheekthinLevel.setProgress(80);
        //瘦脸
        sbCheekthinLevel.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                onCheekThinSelected(progress, 100);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }
        });
        sbEnlargeEyeLevel.setProgress(80);
        //大眼
        sbEnlargeEyeLevel.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                onEnlargeEyeSelected(progress, 100);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }
        });
    }

    /**
     * 初始化魔拍布局
     */
    private void initMagicUI() {
//        rvNew.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        mEffectRecyclerAdapter = new EffectAndFilterSelectAdapter(rvNew, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT);
//        mEffectRecyclerAdapter.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int itemPosition) {
//                onEffectItemSelected(EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[itemPosition]);
//            }
//        });
//        rvNew.setAdapter(mEffectRecyclerAdapter);
//        indicatorRunAnim(vIndicatorMagic, 0);
//        setMagicSelectTabTextColor(tvTabNew);
//        setMagicSelectLayoutVisible(rvNew);
//
//        ivRecordComplete.setVisibility(View.GONE);
//        ivRecordDele.setVisibility(View.GONE);
    }

    /**
     * 磨皮级别点击
     */
    private void initBlurOnClick() {
        //磨皮
        mBtnBlurLevels = new Button[BLUR_LEVEL_BTN_ID.length];
        for (int i = 0; i < BLUR_LEVEL_BTN_ID.length; i++) {
            final int level = i;
            mBtnBlurLevels[i] = (Button) findViewById(BLUR_LEVEL_BTN_ID[i]);
            mBtnBlurLevels[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectBlurLevelBackground(mBtnBlurLevels[level]);
                    onBlurLevelSelected(level);
                }
            });
        }
        //磨皮
        setSelectBlurLevelBackground(mBtnBlurLevels[3]);
    }

    /**
     * 初始化指示器位置
     */
    private void initIndicator() {
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        indicatorWidth = DensityUtils.dp2px(this, indicatorWidth);
        RelativeLayout.LayoutParams indicatorBeautifyParam = (RelativeLayout.LayoutParams) vIndicatorBeautify.getLayoutParams();
        paddingLeft = (windowWidth / tabNums - indicatorWidth) / 2;
        indicatorBeautifyParam.setMargins(paddingLeft, 0, 0, 0);
        vIndicatorBeautify.setLayoutParams(indicatorBeautifyParam);

//        RelativeLayout.LayoutParams indicatorMagicParam = (RelativeLayout.LayoutParams) vIndicatorMagic.getLayoutParams();
//        paddingLeft = (windowWidth / tabNums - indicatorWidth) / 2;
//        indicatorMagicParam.setMargins(paddingLeft, 0, 0, 0);
//        vIndicatorMagic.setLayoutParams(indicatorMagicParam);
    }


    /**
     * 指示器位移动画
     */
    private void indicatorRunAnim(View view, int s) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", s);
        animator.setDuration(100);
        animator.start();
    }

    /**
     * 美颜tab切换
     *
     * @param selectedTv
     */
    private void setBeautifySelectTabTextColor(TextView selectedTv) {
        tvTabFilter.setTextColor(getResources().getColor(R.color.white));
        tvTabBlurLevel.setTextColor(getResources().getColor(R.color.white));
        tvTabColor.setTextColor(getResources().getColor(R.color.white));
        tvTabCheekthinLevel.setTextColor(getResources().getColor(R.color.white));
        tvTabEnlargeEye.setTextColor(getResources().getColor(R.color.white));
        selectedTv.setTextColor(getResources().getColor(R.color.normal_red));
    }

    /**
     * Magictab切换
     *
     * @param selectedTv
     */
    private void setMagicSelectTabTextColor(TextView selectedTv) {
//        tvTabNew.setTextColor(getResources().getColor(R.color.white));
//        tvTabHot.setTextColor(getResources().getColor(R.color.white));
//        tvTabLove.setTextColor(getResources().getColor(R.color.white));
//        tvTabCool.setTextColor(getResources().getColor(R.color.white));
//        tvTabTaste.setTextColor(getResources().getColor(R.color.white));
//        selectedTv.setTextColor(getResources().getColor(R.color.normal_red));
    }


    /**
     * 美颜tab切换 布局显示隐藏
     *
     * @param v
     */
    private void setBeautifySelectLayoutVisible(View v) {
        rvFilter.setVisibility(View.GONE);
        layBlurLevel.setVisibility(View.GONE);
        layColorLevel.setVisibility(View.GONE);
        layCheekthinLevel.setVisibility(View.GONE);
        layEnlargeEyeLevel.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    /**
     * 速度tab切换 布局显示隐藏
     *
     * @param tv
     */
    private void setSpeedSelectLayoutVisible(TextView tv) {
        tvTabSlow.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTabVerySlow.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTabStandardSpeed.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTabFast.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTabVeryFast.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTabSlow.setTextColor(getResources().getColor(R.color.white));
        tvTabVerySlow.setTextColor(getResources().getColor(R.color.white));
        tvTabStandardSpeed.setTextColor(getResources().getColor(R.color.white));
        tvTabFast.setTextColor(getResources().getColor(R.color.white));
        tvTabVeryFast.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.shape_white);
        tv.setTextColor(getResources().getColor(R.color.c_6b6c73));
    }

    /**
     * Magictab切换 布局显示隐藏
     *
     * @param v
     */
    private void setMagicSelectLayoutVisible(View v) {
//        rvNew.setVisibility(View.GONE);
//        v.setVisibility(View.VISIBLE);
    }


    /**
     * 磨皮级别切换布局颜色
     *
     * @param btn
     */
    private void setSelectBlurLevelBackground(Button btn) {
        mBtnBlurLevels[0].setBackground(getResources().getDrawable(R.drawable.bg_none_beautify_unselected));
        for (int i = 1; i < BLUR_LEVEL_BTN_ID.length; i++) {
            mBtnBlurLevels[i].setBackground(getResources().getDrawable(R.drawable.shape_beautify_circle_unselected));
            mBtnBlurLevels[i].setTextColor(getResources().getColor(R.color.black_new_two));
        }
        if (btn == mBtnBlurLevels[0]) {
            btn.setBackground(getResources().getDrawable(R.drawable.bg_none_beautify_selected));
        } else {
            btn.setBackground(getResources().getDrawable(R.drawable.shape_beautify_circle_selected));
            btn.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 延时取消清空
         */
        ivTpTake.setEnabled(true);
        if (myCountTimer != null) {
            myCountTimer.cancel();
            tvCountDown.setText("");
        }
    }

    /**
     * 点击返回时，ui控制
     *
     * @param isBackKey 是否退出界面
     */
    public void onBackControlUI(boolean isBackKey) {
        if (layBeautifyControl.getVisibility() == View.GONE && layMagicControl.getVisibility() == View.GONE) {
            if (isBackKey) {
                onFinshThisScreen();
            }
        } else {
            if (layBeautifyControl.getVisibility() == View.VISIBLE) {
                AnimUtils.animOutFromScreenBottom(layBeautifyControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layBeautifyControl.setVisibility(View.GONE);
                tvTouchBeautify.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
                if (mIsStopped) {
//                    laySpeed.setVisibility(View.VISIBLE);
                } else {
                    laySpeed.setVisibility(View.GONE);
                }
                if (isHasRecorded) {
                    tvDurationType.setVisibility(View.GONE);
                } else {
                    tvDurationType.setVisibility(View.GONE);
                }
//                AnimUtils.animTranslationYScaleRecovery(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
            }
            if (layMagicControl.getVisibility() == View.VISIBLE) {
                AnimUtils.animOutFromScreenBottom(layMagicControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.GONE);
                tvTouchMagic.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
                if (mIsStopped) {
//                    laySpeed.setVisibility(View.VISIBLE);
                } else {
                    laySpeed.setVisibility(View.GONE);
                }
                if (isHasRecorded) {
                    tvDurationType.setVisibility(View.GONE);
                } else {
                    tvDurationType.setVisibility(View.GONE);
                }
//                AnimUtils.animTranslationYScaleRecovery(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
            }
        }
    }

    //控制返回键
    @Override
    public void onBackPressed() {
        // 完全由自己控制返回键逻辑，系统不在控制，但是有个前提是不要在Activity的onKeyDown或者OnKeyUp中拦截掉返回键

        // 拦截：就是在OnKeyDown或者OnKeyUp中自己处理了返回键（这里处理之后return true.或者return false都会导致onBackPressed不会执行）

        // 不拦截：在OnKeyDown和OnKeyUp中返回super对应的方法（如果两个方法都被覆写就分别都要返回super.onKeyDown,super.onKeyUp）
        onBackControlUI(false);
    }


    @OnClick({R.id.iv_close, R.id.iv_camera_setting, R.id.iv_camera_switch, R.id.iv_record_beautify, R.id.iv_record_magic, R.id.iv_record_dele, R.id.iv_record_complete, R.id.tv_duration_type,
            R.id.tv_tab_very_slow, R.id.tv_tab_slow, R.id.tv_tab_standard_speed, R.id.tv_tab_fast, R.id.tv_tab_very_fast,
            R.id.tv_touch_beautify, R.id.tv_touch_magic, R.id.tv_tab_filter, R.id.tv_tab_blur_level, R.id.tv_tab_color, R.id.tv_tab_cheekthin_level, R.id.tv_tab_enlarge_eye,
            })
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.iv_close://关闭按钮
                onBackControlUI(true);
                break;
            case R.id.iv_camera_setting://设置
                ivCameraSetting.setEnabled(false);
                onCameraSetting(ivCameraSetting);
                break;
            case R.id.iv_camera_switch://前后切换
                onCameraChange();
                break;
            case R.id.iv_record_beautify://美颜
                AnimUtils.animEnterFromScreenBottom(layBeautifyControl);
                AnimUtils.animOutFromScreenBottom(layBottom);
                layBeautifyControl.setVisibility(View.VISIBLE);
                tvTouchBeautify.setVisibility(View.VISIBLE);
                layBottom.setVisibility(View.GONE);
                tvDurationType.setVisibility(View.GONE);
                laySpeed.setVisibility(View.GONE);
//                AnimUtils.animTranslationYScale(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
                break;
            case R.id.iv_record_magic://魔拍
                AnimUtils.animEnterFromScreenBottom(layMagicControl);
                AnimUtils.animOutFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.VISIBLE);
                tvTouchMagic.setVisibility(View.VISIBLE);
                layBottom.setVisibility(View.GONE);
                tvDurationType.setVisibility(View.GONE);
                laySpeed.setVisibility(View.GONE);
//                AnimUtils.animTranslationYScale(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
                break;
            case R.id.iv_record_dele://回删
                if (!mIsStopped) {
                    return;
                }
                showDelePartDialog(mContext);
                break;
            case R.id.tv_touch_beautify://美颜布局区域外的点击事件
                AnimUtils.animOutFromScreenBottom(layBeautifyControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layBeautifyControl.setVisibility(View.GONE);
                tvTouchBeautify.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
                if (mIsStopped) {
//                    laySpeed.setVisibility(View.VISIBLE);
                } else {
                    laySpeed.setVisibility(View.GONE);
                }
                if (isHasRecorded) {
                    tvDurationType.setVisibility(View.GONE);
                } else {
                    tvDurationType.setVisibility(View.GONE);
                }
//                AnimUtils.animTranslationYScaleRecovery(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
                break;
            case R.id.tv_touch_magic://魔拍布局区域外的点击事件
                AnimUtils.animOutFromScreenBottom(layMagicControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.GONE);
                tvTouchMagic.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
                if (mIsStopped) {
//                    laySpeed.setVisibility(View.VISIBLE);
                } else {
                    laySpeed.setVisibility(View.GONE);
                }
                if (isHasRecorded) {
                    tvDurationType.setVisibility(View.GONE);
                } else {
                    tvDurationType.setVisibility(View.GONE);
                }
//                AnimUtils.animTranslationYScaleRecovery(IRecordVideoUIAty.this, ivTpTake, 40, 0.625f);
                break;
            case R.id.iv_record_complete://完成
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                if (mIsStopped) {
                    onCompleted();
                } else {
                    RxToast.showToast("请先停止录制");
                }
                break;
            case R.id.tv_tab_filter://美颜->滤镜
                indicatorRunAnim(vIndicatorBeautify, 0);
                setBeautifySelectTabTextColor(tvTabFilter);
                setBeautifySelectLayoutVisible(rvFilter);
                break;
            case R.id.tv_tab_blur_level://美颜->磨皮
                indicatorRunAnim(vIndicatorBeautify, (paddingLeft * 2 + indicatorWidth) * 1);
                setBeautifySelectTabTextColor(tvTabBlurLevel);
                setBeautifySelectLayoutVisible(layBlurLevel);
                break;
            case R.id.tv_tab_color://美颜->美白
                indicatorRunAnim(vIndicatorBeautify, (paddingLeft * 2 + indicatorWidth) * 2);
                setBeautifySelectTabTextColor(tvTabColor);
                setBeautifySelectLayoutVisible(layColorLevel);
                break;
            case R.id.tv_tab_cheekthin_level://美颜->瘦脸
                indicatorRunAnim(vIndicatorBeautify, (paddingLeft * 2 + indicatorWidth) * 3);
                setBeautifySelectTabTextColor(tvTabCheekthinLevel);
                setBeautifySelectLayoutVisible(layCheekthinLevel);
                break;
            case R.id.tv_tab_enlarge_eye://美颜->大眼
                indicatorRunAnim(vIndicatorBeautify, (paddingLeft * 2 + indicatorWidth) * 4);
                setBeautifySelectTabTextColor(tvTabEnlargeEye);
                setBeautifySelectLayoutVisible(layEnlargeEyeLevel);
                break;
//            case R.id.tv_tab_new://魔拍->最新
//                indicatorRunAnim(vIndicatorMagic, 0);
//                setMagicSelectTabTextColor(tvTabNew);
//                setMagicSelectLayoutVisible(rvNew);
//                break;
//            case R.id.tv_tab_hot://魔拍->最热
//                indicatorRunAnim(vIndicatorMagic, (paddingLeft * 2 + indicatorWidth) * 1);
//                setMagicSelectTabTextColor(tvTabHot);
//                setMagicSelectLayoutVisible(rvNew);
//                break;
//            case R.id.tv_tab_love://魔拍->可爱
//                indicatorRunAnim(vIndicatorMagic, (paddingLeft * 2 + indicatorWidth) * 2);
//                setMagicSelectTabTextColor(tvTabLove);
//                setMagicSelectLayoutVisible(rvNew);
//                break;
//            case R.id.tv_tab_cool://魔拍->潮酷
//                indicatorRunAnim(vIndicatorMagic, (paddingLeft * 2 + indicatorWidth) * 3);
//                setMagicSelectTabTextColor(tvTabCool);
//                setMagicSelectLayoutVisible(rvNew);
//                break;
//            case R.id.tv_tab_taste://魔拍->趣味
//                indicatorRunAnim(vIndicatorMagic, (paddingLeft * 2 + indicatorWidth) * 4);
//                setMagicSelectTabTextColor(tvTabTaste);
//                setMagicSelectLayoutVisible(rvNew);
//                break;
            case R.id.tv_tab_very_slow:
                currentSpeed = 0.25f;
                onRecordSpeed(0.25f);
                setSpeedSelectLayoutVisible(tvTabVerySlow);
                setRecordZhuanHuanDuration(recordBiaoZhunDuration);
                break;
            case R.id.tv_tab_slow:
                currentSpeed = 0.5f;
                onRecordSpeed(0.5f);
                setSpeedSelectLayoutVisible(tvTabSlow);
                setRecordZhuanHuanDuration(recordBiaoZhunDuration);
                break;
            case R.id.tv_tab_standard_speed:
                currentSpeed = 1.0f;
                onRecordSpeed(1.0f);
                setSpeedSelectLayoutVisible(tvTabStandardSpeed);
                setRecordZhuanHuanDuration(recordBiaoZhunDuration);
                break;
            case R.id.tv_tab_fast:
                currentSpeed = 2.0f;
                onRecordSpeed(2.0f);
                setSpeedSelectLayoutVisible(tvTabFast);
                setRecordZhuanHuanDuration(recordBiaoZhunDuration);
                break;
            case R.id.tv_tab_very_fast:
                currentSpeed = 4.0f;
                onRecordSpeed(4.0f);
                setSpeedSelectLayoutVisible(tvTabVeryFast);
                setRecordZhuanHuanDuration(recordBiaoZhunDuration);
                break;
            case R.id.tv_duration_type:
//                DurationSelectPopuWindow durationSelectPopuWindow = new DurationSelectPopuWindow((Activity) mContext, recordDuration, tvDurationType);
//                durationSelectPopuWindow.showUp(tvDurationType);
//                durationSelectPopuWindow.setOnPopuClick(new DurationSelectPopuWindow.OnPopuClick() {
//                    @Override
//                    public void onClick(int second) {
//                        recordBiaoZhunDuration = second;
//                        setRecordZhuanHuanDuration(second);
//                    }
//                });
                break;
        }
    }


    private void setRecordZhuanHuanDuration(int time) {
        if (currentSpeed >= 1) {
            recordDuration = time;
        } else {
            recordDuration = (int) (StringUtil.roundeds(time * currentSpeed, 0));
        }
        tvDurationType.setText(DurationSelectPopuWindow.getCodeSecond(time));
        resetProgress();
        stepBar.setMax(recordDuration * 1000);
    }

    private void setProgress() {
        if (mProgress > recordDuration * 1000) {
            mHandler.removeMessages(0);
            if (!mIsStopped) {
                ivTpTake.setEnabled(true);
                ivTpTake.setImageResource(R.mipmap.icon_record_take);
                mIsStopped = true;
                layTop.setVisibility(View.VISIBLE);
                stopProgress();
                onStopRecording();
                onRecordComplete();
            }
        } else if (!mIsStopped) {
            mProgress += 100;
            stepBar.setProgress(mProgress);
            mHandler.sendEmptyMessageDelayed(0, 100);
        }
    }


    private void stopProgress() {
        stepBar.setTimeStamp(true);
    }

    private void startProgress() {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
    }

    private void resetProgress() {
        stepBar.reset();
        mProgress = 0;
    }

    private void showDelePartDialog(Context context) {
        dialog = new CommomDialog(IRecordVideoUIAty.this, R.style.dialog, "确定要删除吗?", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    stepBar.setmState(SteppingProgressBar.STATE_DELETE_PREPARE);
                    stepBar.deleteLastStepFast(new SteppingProgressBar.OnDeleteCallbackListener() {
                        @Override
                        public void deleteDone(float percent) {
                            mProgress = (int) percent;
                            lastPartTime = percent / 1000;
//                                if (speedTypeList != null && speedTypeList.size() > 0) {
//                                    speedTypeList.remove(speedTypeList.size() - 1);
//                                }
                            onDeletPartVideo();
                            Logger.e(percent + "");
                            if (percent == 0) {
                                isHasRecorded = false;
                                //laySpeed.setVisibility(View.VISIBLE);
                                //tvDurationType.setVisibility(View.VISIBLE);
                                ivRecordComplete.setVisibility(View.GONE);
                                ivRecordDele.setVisibility(View.GONE);
                            }

                        }
                    });
                }
            }
        }).setTitle("温馨提示");
        dialog.show();
    }

//    public static enum VideoSpeedType {
//        VERYSLOW,
//        SLOW,
//        STANDARD,
//        FAST,
//        VERYFAST
//    }

    /**
     * 道具贴纸选择
     *
     * @param effectItemName 道具贴纸文件名
     */
    abstract protected void onEffectItemSelected(String effectItemName);

    /**
     * 滤镜选择
     *
     * @param filterName 滤镜名称
     */
    abstract protected void onFilterSelected(String filterName);

    /**
     * 磨皮选择
     *
     * @param level 磨皮level
     */
    abstract protected void onBlurLevelSelected(int level);

    /**
     * 美白选择
     *
     * @param progress 美白滑动条进度
     * @param max      美白滑动条最大值
     */
    abstract protected void onColorLevelSelected(int progress, int max);

    /**
     * 瘦脸选择
     *
     * @param progress 瘦脸滑动进度
     * @param max      瘦脸滑动条最大值
     */
    abstract protected void onCheekThinSelected(int progress, int max);

    /**
     * 大眼选择
     *
     * @param progress 大眼滑动进度
     * @param max      大眼滑动条最大值
     */
    abstract protected void onEnlargeEyeSelected(int progress, int max);

    /**
     * 相机切换
     */
    abstract protected void onCameraChange();


    /**
     * 开始录制
     */
    abstract protected void onStartRecording();

    /**
     * 停止录制
     */
    abstract protected void onStopRecording();

    /**
     * 关闭界面
     */
    abstract protected void onFinshThisScreen();

    /**
     * 设置相机弹窗
     */
    abstract protected void onCameraSetting(View clickView);

    /**
     * 完成
     */
    abstract protected void onCompleted();

    /**
     * 录制设置速度
     */
    abstract protected void onRecordSpeed(float speedType);

    /**
     * 删除视频片段
     */
    abstract protected void onDeletPartVideo();

    /**
     * 录制完成
     */
    abstract protected void onRecordComplete();
}
