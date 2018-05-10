package com.merrichat.net.activity.meiyu.fragments.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.fragments.Gift;

import java.util.ArrayList;
import java.util.List;


/**
 * 礼物
 */
public class GiftItemView extends LinearLayout {

    private TextView giftNumTv ;
    private ImageView giftIv ;
    private TextView name;
    private Gift gift ;

    private List<Gift> giftList = new ArrayList<>();
    private List<Gift> giftsReceiveList = new ArrayList<>();
    private boolean isReceive = false;





    private boolean isShow = false ;
    public GiftItemView(Context context) {
        this(context,null);
    }

    public GiftItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GiftItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setVisibility(INVISIBLE);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gift_message,null,false);
        giftIv = (ImageView) convertView.findViewById(R.id.gift_type);
        giftNumTv = (TextView) convertView.findViewById(R.id.gift_num);
        name = (TextView)convertView.findViewById(R.id.name);
        addView(convertView);
    }

    public void setGift(final Gift gift,final boolean isSendGift) {
        this.gift = gift;
        refreshView(isSendGift);
    }

    /**
     * 设置礼物数量放大和复原的View
     * @param view
     * @param duration
     */
    public void scaleView(View view, long duration){
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 2f, 1f);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleY).with(scaleX);//两个动画同时开始
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimatorListener!=null){
                    onAnimatorListener.onAnimationEnd(gift);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (onAnimatorListener!=null){
                    onAnimatorListener.onAnimationStart(animation);
                }
            }
        });
    }

    /**
     * 刷新view
     */
    public void refreshView(final boolean isSendGift){
        isReceive = isSendGift;
        if (gift==null){
            return;
        }

        if (isSendGift){
            giftNumTv.setText("x"+giftList.size());
            Glide.with(getContext()).load(gift.giftUrl).into(giftIv);
            name.setText("送出礼物");

        }else {
            giftNumTv.setText("x"+giftsReceiveList.size());
            Glide.with(getContext()).load(gift.giftUrl).into(giftIv);
            name.setText("收到礼物");

        }


        scaleView(giftNumTv,200);
    }

    /**
     * 连续点击送礼物的时候数字缩放效果
     * @param num
     */
    public void addNum(int num){

        if (!isReceive){
            if(giftList.size()>0){
                if(giftList.get(0).giftName.equals(gift.giftName)){
                    giftList.add(gift);
                }else {
                    giftList.clear();
                    giftList.add(gift);
                }
            }else {
                giftList.add(gift);
            }
            giftNumTv.setText("x"+giftList.size());
        }else {
            if(giftsReceiveList.size()>0){
                if(giftsReceiveList.get(0).giftName.equals(gift.giftName)){
                    giftsReceiveList.add(gift);
                }else {
                    giftsReceiveList.clear();
                    giftsReceiveList.add(gift);
                }
            }else {
                giftsReceiveList.add(gift);
            }
            giftNumTv.setText("x"+giftsReceiveList.size());
        }



        scaleView(giftNumTv,200);
        handler.removeCallbacks(runnable);
        if (!isShow()){
            show();
        }
        handler.postDelayed(runnable, 3000);
    }
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            isShow = false ;
            setVisibility(INVISIBLE);
        }
    };

    /**
     * 显示view，并开启定时器
     */
    public void show(){
        isShow = true ;
        setVisibility(VISIBLE);
        handler.postDelayed(runnable, 3000);
    }

    public boolean isShow() {
        return isShow;
    }

    private OnAnimatorListener onAnimatorListener ;

    public void setOnAnimatorListener(OnAnimatorListener onAnimatorListener) {
        this.onAnimatorListener = onAnimatorListener;
    }

    public interface OnAnimatorListener{
        public void onAnimationEnd(Gift gift);
        public void onAnimationStart(Animator animation);
    }
}
