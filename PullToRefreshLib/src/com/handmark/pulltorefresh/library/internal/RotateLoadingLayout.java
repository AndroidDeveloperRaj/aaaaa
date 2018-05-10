/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

public class RotateLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;

	private final Animation mRotateAnimation;
	private final Matrix mHeaderImageMatrix;

	private float mRotationPivotX, mRotationPivotY;

	private final boolean mRotateDrawableWhilePulling;

	private AnimationDrawable drawable;

	private int[] pullingUpAnims = { R.drawable.pulling_up_anim_1,
			R.drawable.pulling_up_anim_2, R.drawable.pulling_up_anim_3,
			R.drawable.pulling_up_anim_4, R.drawable.pulling_up_anim_5 };

	private int[] pullingDownAnims = { R.drawable.rotate_anim_1,
			R.drawable.rotate_anim_2, R.drawable.rotate_anim_3,
			R.drawable.rotate_anim_4, R.drawable.rotate_anim_5,
			R.drawable.rotate_anim_6, R.drawable.rotate_anim_7,
			R.drawable.rotate_anim_8, R.drawable.rotate_anim_9,
			R.drawable.rotate_anim_10, R.drawable.rotate_anim_11,
			R.drawable.rotate_anim_12, R.drawable.rotate_anim_13,
			R.drawable.rotate_anim_14, R.drawable.rotate_anim_15,
			R.drawable.rotate_anim_16, R.drawable.rotate_anim_17,
			R.drawable.rotate_anim_18 };

	public RotateLoadingLayout(Context context, Mode mode,
			Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		mRotateDrawableWhilePulling = attrs.getBoolean(
				R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		mRotateAnimation = new RotateAnimation(0, 720,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	@Override
	public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math
					.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math
					.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		if (mMode == Mode.PULL_FROM_END) {
            mHeaderImage.setBackgroundResource(0);
            mHeaderImage.setImageResource(pullingDownAnims[pullingDownAnims.length - 1]);
		} else {
            float angle;
            if (mRotateDrawableWhilePulling) {
                angle = Math.min(scaleOfLayout * 90f, 180f);
            } else {
                angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
            }

            mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		}
	}

	@Override
	protected void refreshingImpl() {
		if (mMode == Mode.PULL_FROM_END) {
			mHeaderImage.setImageResource(R.drawable.animation_loading_down_res);
		} else {
			mHeaderImage.setImageResource(R.drawable.animation_loading_down_res);
            mHeaderImageMatrix.reset();
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		}
		drawable = (AnimationDrawable) mHeaderImage.getDrawable();
		drawable.start();
	}

	@Override
	protected void resetImpl() {
		// init
		// mHeaderImage.clearAnimation();
		// added by zn.
		if (drawable != null && drawable.isRunning()) {
			// mHeaderImage.setBackgroundResource(0);
			if (mMode == Mode.PULL_FROM_END) {
				mHeaderImage.setImageResource(R.drawable.rotate_anim_18);
			} else {
				mHeaderImage.clearAnimation();
				mHeaderImage.setImageResource(R.drawable.xxb_ptr_rotate);
			}
			drawable.stop();
		}
		resetImageRotation();
	}

	private void resetImageRotation() {
		if (null != mHeaderImageMatrix) {
			mHeaderImageMatrix.reset();
			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		}
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		// init
		// return R.drawable.default_ptr_rotate;
		// added by zn.
		if (mMode == Mode.PULL_FROM_END) {
			return R.drawable.rotate_anim_1;
		} else {
			return R.drawable.xxb_ptr_rotate;
		}
	}

}
