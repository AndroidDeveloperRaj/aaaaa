package com.merrichat.net.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.player.gsyvideo.EmptyControlVideo;
import com.merrichat.net.model.CircleVideoModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.StringUtil;

import java.util.List;

/**
 * Created by amssy on 18/4/12.
 */

public class CircleVideoAdapter extends BaseQuickAdapter<CircleVideoModel.DataBean.ListBean, BaseViewHolder> {

    private OnVideoPlayerListener onItemClickListener;

    public CircleVideoAdapter(int layoutResId, @Nullable List<CircleVideoModel.DataBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CircleVideoModel.DataBean.ListBean item) {
        Logger.e("video adapter position：" + helper.getAdapterPosition());
        if (helper.getAdapterPosition() == 0) {
            onItemClickListener.onVideoPlayer((EmptyControlVideo) helper.getView(R.id.ijkVideoView), (ProgressBar) helper.getView(R.id.pb_mini));
        }
        PhotoVideoModel model = JSON.parseObject(item.beautyLog1.cover, PhotoVideoModel.class);
        String coverImage = model.getUrl();
        //封面图
        ((SimpleDraweeView) helper.getView(R.id.anchor_img)).setImageURI(coverImage);
        helper.getView(R.id.anchor_img).setVisibility(View.VISIBLE);
        //进度条隐藏
        helper.getView(R.id.pb_mini).setVisibility(View.GONE);
        //标题
        ((TextView) helper.getView(R.id.tv_video_title)).setText(item.beautyLog1.title);
        //内容
        if (TextUtils.isEmpty(item.beautyLog1.describe)) {
            (helper.getView(R.id.tv_video_content)).setVisibility(View.GONE);
        } else {
            (helper.getView(R.id.tv_video_content)).setVisibility(View.VISIBLE);
            ((TextView) helper.getView(R.id.tv_video_content)).setText(item.beautyLog1.describe);
        }
        //评论
        if (item.beautyLog1.commentCounts > 9999) {
            ((TextView) helper.getView(R.id.tv_comment)).setText(StringUtil.rounded(item.beautyLog1.commentCounts / 10000, 1) + "w");
        } else {
            ((TextView) helper.getView(R.id.tv_comment)).setText("" + item.beautyLog1.commentCounts);
        }
        //点赞
        ((CheckBox) helper.getView(R.id.shb_like)).setChecked(item.likes);

        if (item.beautyLog1.likeCounts > 9999) {
            ((TextView) helper.getView(R.id.tv_like)).setText(StringUtil.rounded(item.beautyLog1.likeCounts / 10000, 1) + "w");
        } else {
            ((TextView) helper.getView(R.id.tv_like)).setText("" + item.beautyLog1.likeCounts);
        }
        //收入
        if (!TextUtils.isEmpty(item.inCome)) {
            //赚钱
            if (Double.valueOf(item.inCome) >= 10000) {
                double incomes = Double.valueOf(item.inCome);
                ((TextView) helper.getView(R.id.tv_money)).setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                ((TextView) helper.getView(R.id.tv_money1)).setText(StringUtil.rounded(incomes / 10000, 1) + "w");
            } else {
                ((TextView) helper.getView(R.id.tv_money)).setText(item.inCome);
                ((TextView) helper.getView(R.id.tv_money1)).setText(item.inCome);
            }
        }
        //头像
        ((SimpleDraweeView) helper.getView(R.id.sv_header)).setImageURI(item.beautyLog1.memberImage);
        //关注按钮
        if (item.queryIsAttentionRelation) {
            helper.getView(R.id.iv_collect).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.iv_collect).setVisibility(View.VISIBLE);
            if (TextUtils.equals(UserModel.getUserModel().getMemberId(), String.valueOf(item.beautyLog1.memberId))) {
                helper.getView(R.id.iv_collect).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.iv_collect).setVisibility(View.VISIBLE);
            }
        }

        //设置textView可滑动
        ((TextView) helper.getView(R.id.tv_video_content)).setMovementMethod(ScrollingMovementMethod.getInstance());
        //评论
        helper.addOnClickListener(R.id.iv_comment);
        //item
        helper.addOnClickListener(R.id.room_view);
        //头像
        helper.addOnClickListener(R.id.sv_header);
        //关注
        helper.addOnClickListener(R.id.iv_collect);
        //点赞
        helper.addOnClickListener(R.id.shb_like);
        //收入公分
        helper.addOnClickListener(R.id.iv_money);
        //打赏
        helper.addOnClickListener(R.id.iv_dash);
        //写评论
        helper.addOnClickListener(R.id.tv_write_comment);
        //分享
        helper.addOnClickListener(R.id.iv_share);

        //final GestureDetector mGestureDetector = new GestureDetector(mContext, new OnDoubleClick());

//        helper.getView(R.id.ijkVideoView).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mGestureDetector.onTouchEvent(event);
//            }
//        });
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setVideoPlayerListener(OnVideoPlayerListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnVideoPlayerListener {
        void onVideoPlayer(EmptyControlVideo mVideoView, ProgressBar mProgressBar);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

//    /**
//     * 双击事件
//     */
//    public onDoubleClicklister onDoubleClicks;
//
//    public void setOnDoubleClicklister(onDoubleClicklister onDoubleClick) {
//        this.onDoubleClicks = onDoubleClick;
//    }
//
//    public interface onDoubleClicklister {
//        void onDoubleClicks(MotionEvent event);
//
//        void onViewClicks();
//    }
//
//    public class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            //双击
//            onDoubleClicks.onDoubleClicks(e);
//            return false;
//        }
//
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            //单击
//            onDoubleClicks.onViewClicks();
//            return false;
//        }
//
//    }

}
