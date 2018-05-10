package com.merrichat.net.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.merrichat.net.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Allen Cheng on 2018/5/4.
 */
public class RegisterRedPakageShareBottomDialog extends BottomBaseDialog<ShareBottomDialog> {

    @BindView(R.id.ll_wechat_friend_circle)
    LinearLayout mLlWechatFriendCircle;
    @BindView(R.id.ll_wechat_friend)
    LinearLayout mLlWechatFriend;
    @BindView(R.id.ll_weibo)
    LinearLayout mLlWeiBo;
    @BindView(R.id.ll_qq)
    LinearLayout mLlQq;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private RegisterRedPakageShareBottomDialog.OnShareClick onShareClick;

    public RegisterRedPakageShareBottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public RegisterRedPakageShareBottomDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }

    public RegisterRedPakageShareBottomDialog.OnShareClick getOnShareClick() {
        return onShareClick;
    }

    public void setOnShareClick(RegisterRedPakageShareBottomDialog.OnShareClick onShareClick) {
        this.onShareClick = onShareClick;
    }

    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
        showAnim(null);
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_share_register, null);
        ButterKnife.bind(this, inflate);

        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mLlWechatFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlWechatFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlWeiBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
    }

    public interface OnShareClick {
        void onShareClick(View v);
    }
}
