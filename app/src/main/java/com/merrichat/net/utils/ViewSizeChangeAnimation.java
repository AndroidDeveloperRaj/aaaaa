package com.merrichat.net.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by amssy on 18/4/13.
 */

public class ViewSizeChangeAnimation extends Animation {
    int initialHeight;
    int targetHeight;
    int initialWidth;
    int targetWidth;
    View view;

    public ViewSizeChangeAnimation(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        //this.targetWidth = targetWidth;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
        //view.getLayoutParams().width = initialWidth + (int) ((targetWidth - initialWidth) * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        this.initialHeight = height;
        this.initialWidth = width;
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
