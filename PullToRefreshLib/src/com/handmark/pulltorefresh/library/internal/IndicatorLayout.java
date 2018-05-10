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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;

@SuppressLint("ViewConstructor")
public class IndicatorLayout extends FrameLayout implements AnimationListener {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private Animation mInAnim, mOutAnim;
	public static final int INDICATOR_TOP = 1;
	public static final int INDICATOR_CAL = 2;

	private RelativeLayout mFilterLayout;
	private TextView mFilterTextView;
	private ImageView mFilterimaImageView;

	public IndicatorLayout(Context context, PullToRefreshBase.Mode mode,
			final int type) {
		super(context);

		int inAnimResId, outAnimResId;
		switch (mode) {
		case PULL_FROM_END:
			inAnimResId = R.anim.slide_in_from_bottom;
			outAnimResId = R.anim.slide_out_to_bottom;
			break;
		default:
		case PULL_FROM_START:
			inAnimResId = R.anim.slide_in_from_top;
			outAnimResId = R.anim.slide_out_to_top;
			break;
		}

		mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
		mInAnim.setAnimationListener(this);

		mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
		mOutAnim.setAnimationListener(this);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (INDICATOR_TOP == type) {
			mFilterLayout = (RelativeLayout) inflater.inflate(
					R.layout.pull_to_refresh_filter, null);
			setFilterTextView((TextView) mFilterLayout
					.findViewById(R.id.question_num));
			setFilterImageView((ImageView) mFilterLayout
					.findViewById(R.id.filter_iv));
		} else if (INDICATOR_CAL == type) {
			mFilterLayout = (RelativeLayout) inflater.inflate(
					R.layout.pull_to_refresh_calendar, null);
			setFilterTextView((TextView) mFilterLayout
					.findViewById(R.id.question_num));
		}
		this.addView(mFilterLayout);

	}

	public final boolean isVisible() {
		Animation currentAnim = getAnimation();
		if (null != currentAnim) {
			return mInAnim == currentAnim;
		}
		return getVisibility() == View.VISIBLE;
	}

	public void hide() {
		startAnimation(mOutAnim);
	}

	public void show() {
		// mArrowImageView.clearAnimation();
		startAnimation(mInAnim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == mOutAnim) {
			// mArrowImageView.clearAnimation();
			setVisibility(View.GONE);
		} else if (animation == mInAnim) {
			setVisibility(View.VISIBLE);
		}

		clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// NO-OP
	}

	@Override
	public void onAnimationStart(Animation animation) {
		setVisibility(View.VISIBLE);
	}

	public void releaseToRefresh() {
	}

	public void pullToRefresh() {
	}

	public TextView getFilterTextView() {
		return mFilterTextView;
	}

	public void setFilterTextView(TextView mFilterTextView) {
		this.mFilterTextView = mFilterTextView;
	}

	public ImageView getFilterImageView() {
		return mFilterimaImageView;
	}

	public void setFilterImageView(ImageView mFilterimaImageView) {
		this.mFilterimaImageView = mFilterimaImageView;
	}

}
