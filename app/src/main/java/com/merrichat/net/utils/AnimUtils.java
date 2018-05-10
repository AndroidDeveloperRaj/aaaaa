package com.merrichat.net.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by amssy on 17/11/10.
 */

public class AnimUtils {

    /**
     * 上下平移并缩放动画
     */
    public static final void animTranslationYScale(Context context, View view, float y, float scale) {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), view.getTranslationY() + DensityUtils.dp2px(context, y));
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "scaleY", 1f, scale);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(view, "scaleX", 1f, scale);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(rotate).with(fadeInOut).with(moveIn);
        animSet.setDuration(500);
        animSet.start();
    }

    /**
     * 上下平移并缩放动画
     */
    public static final void animTranslationYScaleRecovery(Context context, View view, float y, float scale) {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), view.getTranslationY() - DensityUtils.dp2px(context, y));
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "scaleY", scale, 1f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(view, "scaleX", scale, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(rotate).with(fadeInOut).with(moveIn);
        animSet.setDuration(500);
        animSet.start();
    }

    /**
     * 从view顶部上弹
     */
    public static final void animEnterFromScreenBottom(View view) {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        view.startAnimation(mShowAction);
    }

    /**
     * 从view底部弹出
     */
    public static final void animOutFromScreenBottom(View view) {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mShowAction.setDuration(300);
        view.startAnimation(mShowAction);
    }

    /**
     * 从view顶部下弹
     */
    public static final void animEnterFromScreenTop(View view) {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        view.startAnimation(mShowAction);
    }
}
