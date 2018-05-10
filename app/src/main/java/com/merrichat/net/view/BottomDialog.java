package com.merrichat.net.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.merrichat.net.R;
import com.merrichat.net.utils.RxTools.RxDataTool;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 2018/2/27.
 */

public class BottomDialog extends BottomBaseDialog<BottomDialog> {

    @BindView(R.id.tv_first)
    TextView tvFirst;
    @BindView(R.id.ll_first)
    LinearLayout llFirst;
    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.ll_second)
    LinearLayout llSecond;
    @BindView(R.id.ll_third)
    LinearLayout llThird;
    @BindView(R.id.tv_third)
    TextView tv_third;
    @BindView(R.id.tv_four)
    TextView tv_four;
    @BindView(R.id.tv_cancle)
    TextView tv_cancle;
    private String mTv4;
    private String mTv3;
    private OnViewClick onViewClick;
    private String mTv1, mTv2;

    public BottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public BottomDialog(Context context) {
        super(context);
    }

    public BottomDialog(Context context, String tv1, String tv2) {
        super(context);
        this.mTv1 = tv1;
        this.mTv2 = tv2;
    }

    public BottomDialog(Context context, String tv1, String tv2, String tv3, String tv4) {
        super(context);
        this.mTv1 = tv1;
        this.mTv2 = tv2;
        this.mTv3 = tv3;
        this.mTv4 = tv4;
    }

    public OnViewClick getOnViewClick() {
        return onViewClick;
    }

    public void setOnViewClick(OnViewClick onViewClick) {
        this.onViewClick = onViewClick;
    }

    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
        showAnim(null);
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.popup_normal, null);
        ButterKnife.bind(this, inflate);

        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        tvFirst.setText(mTv1);
        tvSecond.setText(mTv2);
        tv_third.setText(mTv3);
        tv_four.setText(mTv4);
        if (!RxDataTool.isNullString(mTv1)) {
            llFirst.setVisibility(View.VISIBLE);
        }else {
            llFirst.setVisibility(View.GONE);
        }
        if (!RxDataTool.isNullString(mTv2)) {
            llSecond.setVisibility(View.VISIBLE);
        }else {
            llSecond.setVisibility(View.GONE);
        }
        if (!RxDataTool.isNullString(mTv3)) {
            llThird.setVisibility(View.VISIBLE);
        }else {
            llThird.setVisibility(View.GONE);
        }
        if (!RxDataTool.isNullString(mTv4)) {
            tv_four.setVisibility(View.VISIBLE);
        }else {
            tv_four.setVisibility(View.GONE);
        }
        tvFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });
        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });
        tv_third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });
        tv_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });

    }

    public interface OnViewClick {
        void onClick(View v);
    }
}
