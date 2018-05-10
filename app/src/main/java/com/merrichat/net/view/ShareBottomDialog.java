package com.merrichat.net.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.merrichat.net.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 2018/2/27.
 */

public class ShareBottomDialog extends BottomBaseDialog<ShareBottomDialog> {

    public OnShareClick getOnShareClick() {
        return onShareClick;
    }

    public void setOnShareClick(OnShareClick onShareClick) {
        this.onShareClick = onShareClick;
    }

    private OnShareClick onShareClick;
    @BindView(R.id.ll_wechat_friend_circle)
    LinearLayout mLlWechatFriendCircle;
    @BindView(R.id.ll_wechat_friend)
    LinearLayout mLlWechatFriend;
    @BindView(R.id.ll_weibo)
    LinearLayout mLlWeiBo;
    @BindView(R.id.ll_qq)
    LinearLayout mLlQq;

    public ShareBottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public ShareBottomDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
//        showAnim(new FlipVerticalSwingEnter());
        showAnim(null);
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_share, null);
        ButterKnife.bind(this, inflate);

        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mLlWechatFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlWechatFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onShareClick != null) {
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onShareClick!=null){
                    onShareClick.onShareClick(v);
                }
            }
        });
        mLlWeiBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onShareClick!=null){
                    onShareClick.onShareClick(v);
                }
            }
        });
    }

    public interface OnShareClick {
        void onShareClick(View v);
    }
}
