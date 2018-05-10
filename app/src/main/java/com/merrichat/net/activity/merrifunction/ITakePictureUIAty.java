package com.merrichat.net.activity.merrifunction;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.picture.fragments.MagicTackPhotoStyleFrgment;
import com.merrichat.net.adapter.EffectAndFilterSelectAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.AnimUtils;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.view.MyCountTimer;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/10/25.
 * 相机的基类
 */

public abstract class ITakePictureUIAty extends AppCompatActivity {

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
     * 拍照
     */
    @BindView(R.id.iv_tp_take)
    ImageView ivTpTake;
    /**
     * 完成
     */
    @BindView(R.id.iv_tp_complete)
    ImageView ivTpComplete;
    /**
     * 底部按钮布局
     */
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;

    @BindView(R.id.tv_touch_beautify)
    TextView tvTouchBeautify;
    @BindView(R.id.tv_touch_magic)
    TextView tvTouchMagic;


    /**
     * 延时
     */
    private MyCountTimer myCountTimer;

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

    /**
     * HOT按钮
     */
    @BindView(R.id.btn_hot)
    Button btnHot;
    /**************************************************************
     ***************************魔拍布局****************************
     **************************************************************/


    private int windowWidth = 0;
    private static int tabNums = 5;//tab个数
    private int indicatorWidth = 24;//一个字12dp，两个字24dp
    private int paddingLeft = 0;

    public boolean is_timer_down_open;
    public boolean is_flash_light_open;
    //滤镜adapter
    private EffectAndFilterSelectAdapter mFilterRecyclerAdapter;

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
        setContentView(R.layout.activity_take_picture);
        ButterKnife.bind(this);
        //设置拍照界面亮度
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);

        initBeautifyUI();
        initMagicUI();
        //查询魔拍是否是HOT状态
        queryIsHotStatus(UserModel.getUserModel().getMemberId());

        MagicTackPhotoStyleFrgment magicTackPhotoStyleFrgment = new MagicTackPhotoStyleFrgment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_magic_control, magicTackPhotoStyleFrgment)
                .commitAllowingStateLoss();
    }


    @OnClick({R.id.iv_close, R.id.iv_camera_setting, R.id.iv_camera_switch, R.id.iv_record_beautify, R.id.iv_record_magic, R.id.iv_tp_take, R.id.iv_tp_complete, R.id.rv_image,
            R.id.tv_tab_filter, R.id.tv_tab_blur_level, R.id.tv_tab_color, R.id.tv_tab_cheekthin_level, R.id.tv_tab_enlarge_eye,
//            R.id.tv_tab_new, R.id.tv_tab_hot, R.id.tv_tab_love, R.id.tv_tab_cool, R.id.tv_tab_taste,
            R.id.tv_touch_beautify, R.id.tv_touch_magic})
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
//                FlashLightUtils.getInstance().switchFlash();
                onCameraChange();
                break;
            case R.id.iv_record_beautify://美颜
                AnimUtils.animEnterFromScreenBottom(layBeautifyControl);
                AnimUtils.animOutFromScreenBottom(layBottom);
                layBeautifyControl.setVisibility(View.VISIBLE);
                tvTouchBeautify.setVisibility(View.VISIBLE);
                layBottom.setVisibility(View.GONE);
//                AnimUtils.animTranslationYScale(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
                break;
            case R.id.iv_record_magic://魔拍
                AnimUtils.animEnterFromScreenBottom(layMagicControl);
                AnimUtils.animOutFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.VISIBLE);
                tvTouchMagic.setVisibility(View.VISIBLE);
                layBottom.setVisibility(View.GONE);
//                AnimUtils.animTranslationYScale(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
                break;
            case R.id.tv_touch_beautify://美颜布局区域外的点击事件
                AnimUtils.animOutFromScreenBottom(layBeautifyControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layBeautifyControl.setVisibility(View.GONE);
                tvTouchBeautify.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
//                AnimUtils.animTranslationYScaleRecovery(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
                break;
            case R.id.tv_touch_magic://魔拍布局区域外的点击事件
                AnimUtils.animOutFromScreenBottom(layMagicControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.GONE);
                tvTouchMagic.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
//                AnimUtils.animTranslationYScaleRecovery(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
                break;
            case R.id.iv_tp_take://拍照
//                Animation animation = AnimationUtils.loadAnimation(ITakePictureUIAty.this, R.anim.set_scale);
//                ivTpTake.startAnimation(animation);
                ivTpTake.setEnabled(false);
                onBackControlUI(false);
                if (is_timer_down_open) {
                    myCountTimer = new MyCountTimer(4000, 1000, tvCountDown, "");
                    myCountTimer.start();
                    myCountTimer.setOnTimeDownListener(new MyCountTimer.DownTimeWatcher() {
                        @Override
                        public void onTime(int num) {
                        }

                        @Override
                        public void onDownTimeFinish() {
                            onCameraTakePicture();
                        }
                    });
                } else {
                    onCameraTakePicture();

                }
                break;
            case R.id.iv_tp_complete://完成
                onCompleted();
                break;
            case R.id.rv_image://拍照图片列表
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


        }
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
//        ivTpComplete.setVisibility(View.GONE);
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
    protected void onPause() {
        super.onPause();
        /**
         * 延时取消清空
         */
        ivTpTake.setEnabled(true);
        if (myCountTimer != null) {
            myCountTimer.cancel();
            ivTpComplete.setEnabled(true);
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
//                AnimUtils.animTranslationYScaleRecovery(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
            }
            if (layMagicControl.getVisibility() == View.VISIBLE) {
                AnimUtils.animOutFromScreenBottom(layMagicControl);
                AnimUtils.animEnterFromScreenBottom(layBottom);
                layMagicControl.setVisibility(View.GONE);
                tvTouchMagic.setVisibility(View.GONE);
                layBottom.setVisibility(View.VISIBLE);
//                AnimUtils.animTranslationYScaleRecovery(ITakePictureUIAty.this, ivTpTake, 80, 0.625f);
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
     * 相机拍照
     */
    abstract protected void onCameraTakePicture();

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

//    /**
//     * 脸型选择
//     */
//    abstract protected void onFaceShapeSelected(int faceShape);
//
//    /**
//     * 美型程度选择
//     */
//    abstract protected void onFaceShapeLevelSelected(int progress, int max);
//
//    /**
//     * 美白程度选择
//     */
//    abstract protected void onRedLevelSelected(int progress, int max);

    /**
     * 查询魔拍是否是HOT状态
     *
     * @param memberId
     */
    private void queryIsHotStatus(String memberId) {
        OkGo.<String>get(Urls.queryIsHotStatus)
                .tag(this)
                .params("memberId", memberId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    String isHot = data.optJSONObject("data").optString("isHot");
                                    if (TextUtils.equals(isHot, "1")) {
                                        btnHot.setVisibility(View.VISIBLE);
                                    } else {
                                        btnHot.setVisibility(View.GONE);
                                    }
                                } else {
                                    //RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }
}
