package com.merrichat.net.view.PopWindow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.merrichat.net.R;
import com.merrichat.net.utils.FastBlur;


/**
 * Created by HMY on 2016/9/10.
 */
public class PopAlertDialog extends Dialog implements PopWindowInterface, DialogInterface.OnShowListener, View.OnClickListener,
        PopWindowInterface.OnStartShowListener, PopWindowInterface.OnStartDismissListener {

    private FrameLayout mRootLayout;
    private FrameLayout mContainLayout;
    private PopAlertView mPopAlertDialog;
    private LinearLayout mContentLayout;
    private LinearLayout layout_f;
    private PopWindow mPopWindow;

    private Animation mAlphaOpenAnimation;
    private Animation mAlphaCloseAnimation;
    private Animation mPopOpenAnimation;
    private Animation mPopCloseAnimation;

    private PopItemAction mCancelPopItemAction;
    private boolean mIsDismissed = true;

    private View mCustomView;
    private boolean mIsBlur;
    private int mRadius;

    public PopAlertDialog(Activity activity, int titleResId, int messageResId, PopWindow popWindow, int radius, boolean isBlur) {
        this(activity, titleResId == 0 ? null : activity.getString(titleResId), messageResId == 0 ? null : activity.getString(messageResId), popWindow, radius, isBlur);
    }

    public PopAlertDialog(Activity activity, CharSequence title, CharSequence message, PopWindow popWindow, int radius, boolean isBlur) {
        super(activity, R.style.PopWindowStyle);
        setContentView(R.layout.pop_alert_dialog);
        mIsBlur = isBlur;
        mRadius = radius;
        getWindow().setWindowAnimations(R.style.PopWindowAnimation);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, getScreenHeight(activity) - getStatusBarHeight(activity));
        setOnShowListener(this);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mPopWindow = popWindow;

        initRootView();
        initContentView(title, message, activity);
        initAnim();
    }

    private void initRootView() {
        mRootLayout = (FrameLayout) findViewById(R.id.layout_root);
        mRootLayout.setOnClickListener(this);
    }

    private void initContentView(CharSequence title, CharSequence message, Activity activity) {
        mPopAlertDialog = (PopAlertView) findViewById(R.id.popAlertView);
        mPopAlertDialog.setPopWindow(mPopWindow);
        mPopAlertDialog.setTitleAndMessage(title, message);

        mContentLayout = (LinearLayout) findViewById(R.id.layout_center);
        layout_f = (LinearLayout) findViewById(R.id.layout_f);
        mContainLayout = (FrameLayout) findViewById(R.id.layout_contain);
        if (mIsBlur) {
            layout_f.setBackground(new BitmapDrawable(getIerceptionScreen(activity)));
        }
    }

    /*
   截取屏幕
   * */
    private Bitmap getIerceptionScreen(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap bitmap = Bitmap.createBitmap(b, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        bitmap = FastBlur.fastBlur(bitmap, mRadius);
        if (bitmap != null) {
            return bitmap;
        } else {
            return null;
        }
    }


    private void initAnim() {
        mPopOpenAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_alert_enter);
        mPopCloseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_alert_exit);
        mPopCloseAnimation.setAnimationListener(new PopSimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mCustomView != null) {
                    mContentLayout.post(mDismissRunnable);
                } else
                    mPopAlertDialog.post(mDismissRunnable);
            }
        });

        mAlphaOpenAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_alpha_enter);
        mAlphaCloseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_alpha_exit);
        mAlphaCloseAnimation.setAnimationListener(new PopSimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                onStartDismiss(PopAlertDialog.this);
                if (mCustomView != null) {
                    mContentLayout.startAnimation(mPopCloseAnimation);
                } else
                    mPopAlertDialog.startAnimation(mPopCloseAnimation);
            }
        });
    }

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            PopAlertDialog.super.dismiss();
        }
    };


    @Override
    public void onShow(DialogInterface dialog) {
        if (mIsDismissed) {
            mIsDismissed = false;
            onStartShow(PopAlertDialog.this);
            mRootLayout.startAnimation(mAlphaOpenAnimation);
            if (mCustomView != null) {
                mContentLayout.startAnimation(mPopOpenAnimation);
            } else if (mPopAlertDialog.showAble()) {
                mPopAlertDialog.refreshBackground();
                mPopAlertDialog.startAnimation(mPopOpenAnimation);
            } else {
                throw new RuntimeException("必须至少添加一个PopItemView");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_root) {
            onBackPressed();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mCancelPopItemAction != null) {
            mCancelPopItemAction.onClick();
        }
    }

    @Override
    public void dismiss() {
        executeExitAnim();
    }

    private void executeExitAnim() {
        if (!mIsDismissed) {
            mIsDismissed = true;
            mRootLayout.startAnimation(mAlphaCloseAnimation);
        }
    }

    @Override
    public void setView(View view) {
        view.setClickable(true);
        mCustomView = view;
        mContentLayout.setVisibility(View.VISIBLE);
        mPopAlertDialog.setVisibility(View.GONE);
        mContentLayout.addView(mCustomView);
    }

    @Override
    public void addContentView(View view) {
        view.setClickable(true);
        mPopAlertDialog.addContentView(view);
    }

    @Override
    public void addItemAction(PopItemAction popItemAction) {
        if (mCustomView != null) {
            return;
        }
        mContentLayout.setVisibility(View.GONE);
        mPopAlertDialog.setVisibility(View.VISIBLE);
        mPopAlertDialog.addItemAction(popItemAction);
        if (popItemAction.getStyle() == PopItemAction.PopItemStyle.Cancel) {
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            mCancelPopItemAction = popItemAction;
        }
    }

    @Override
    public void setIsShowLine(boolean isShowLine) {
        mPopAlertDialog.setIsShowLine(isShowLine);
    }

    @Override
    public void setIsShowCircleBackground(boolean isShow) {
        mPopAlertDialog.setIsShowCircleBackground(isShow);
        if (!isShow) {
            mPopAlertDialog.setBackgroundColor(getContext().getResources().getColor(R.color.dialog_bg_content));
        }
    }

    @Override
    public void setPopWindowMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContainLayout.getLayoutParams();
        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        mContainLayout.setLayoutParams(layoutParams);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    @Override
    public void onStartDismiss(PopWindowInterface popWindowInterface) {
        mPopWindow.onStartDismiss(popWindowInterface);
    }

    @Override
    public void onStartShow(PopWindowInterface popWindowInterface) {
        mPopWindow.onStartShow(popWindowInterface);
    }
}
