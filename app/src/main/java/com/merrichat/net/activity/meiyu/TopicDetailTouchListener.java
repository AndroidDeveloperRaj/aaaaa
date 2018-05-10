package com.merrichat.net.activity.meiyu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.Pools;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.merrichat.net.R;

import java.util.Random;

public class TopicDetailTouchListener implements View.OnTouchListener {
    private final int CHANGETOUCHSTATE = 1;//修改touch状态
    private long down_time = 0;
    private long up_time = 0;

    //handler处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGETOUCHSTATE:
                    break;
            }
        }
    };
    private Context context;

    public RelativeLayout relativeLayout;

    private Pools.SynchronizedPool<ImageView> myPool = new Pools.SynchronizedPool<>(20);
    private ImageView view;
    private int imgWidth;
    private int[] photo = new int[]{ R.drawable.love_06,
            R.drawable.love_07, R.drawable.love_08, R.drawable.love_09, R.drawable.love_10};
    private float[] rotateSum = new float[]{-50f, -40f, -30f, -20f, -10f, 10f, 20f, 30f, 40f, 50f};

    //开启动画
    public void startAnimation(float x, float y) {

        view = myPool.acquire();
        Random random = new Random();
        if (view == null) {
            view = new ImageView(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imgWidth, imgWidth);
            layoutParams.leftMargin = (int) x - imgWidth / 2;
            layoutParams.topMargin = (int) y;
            view.setLayoutParams(layoutParams);
            view.setImageResource(photo[random.nextInt(5)]);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = (int) x - imgWidth / 2;
            layoutParams.topMargin = (int) y ;
            view.setImageResource(photo[random.nextInt(5)]);
        }
        relativeLayout.addView(view);


        //出现动画
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.8f);
        alphaIn.setDuration(500);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 0.8f);
        scaleX.setDuration(1000);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 0.8f);
        scaleY.setDuration(1000);

        //透明度渐变动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 0.8f, 0.0f);
        fadeOut.setDuration(3000);

        //横向变化
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, random.nextInt(200)-50, -random.nextInt(200)+50,0);
        animator.setDuration(6000);

        //退出动画
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        scaleX2.setDuration(300);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        scaleY2.setDuration(300);
        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 0f);
        alphaOut.setDuration(300);

        //旋转动画(随机旋转：-30至30之间)
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 0f);
        rotate.setDuration(0);

        //直线移动动画(距离设置随机，最小为y/2,最大)
//            float path = random.nextInt((int) (y / 2)) + y * 2 / 5;  //图片飘的时间

        float path =  y ;  //图片飘的时间
        ObjectAnimator move = ObjectAnimator.ofFloat(view, "translationY", 0, -path);
        move.setInterpolator(new AccelerateInterpolator());
        move.setDuration(5000);  //图片显示的时间  时间越少向上飘的越快

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(alphaIn).with(scaleX).with(scaleY).with(move);
        animSet.play(animator).with(scaleY).with(move);
        animSet.play(rotate).with(fadeOut).after(alphaIn);
        animSet.play(scaleX2).with(scaleY2).after(move);
        animSet.addListener(new TopicDetailTouchListener.MyListen(view, relativeLayout));
        animSet.start();

    }

    private class MyListen implements Animator.AnimatorListener {
        private ImageView imageView;
        private RelativeLayout rela;

        public MyListen(ImageView view, RelativeLayout rela) {
            this.imageView = view;
            this.rela = rela;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            rela.removeView(imageView);
            myPool.release(imageView);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public TopicDetailTouchListener(RelativeLayout relativeLayout, Context context) {
        imgWidth = DisplayUtil.dip2px(context, 60);
        this.relativeLayout = relativeLayout;
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        int action = event.getAction();
//        if (v.getId() == R.id.touch_surface_view && action == MotionEvent.ACTION_DOWN) {
//            down_time = System.currentTimeMillis();
//            if (handler.hasMessages(CHANGETOUCHSTATE)) {
//                handler.removeMessages(CHANGETOUCHSTATE);
//            }
//            Message msg = new Message();
//            msg.what = CHANGETOUCHSTATE;
//            handler.sendMessageDelayed(msg, 600);
//        }
//        if (v.getId() == R.id.touch_surface_view && action == MotionEvent.ACTION_UP) {
//            if (handler.hasMessages(CHANGETOUCHSTATE)) {
//                handler.removeMessages(CHANGETOUCHSTATE);
//            }
//            Message msg = new Message();
//            msg.what = CHANGETOUCHSTATE;
//            handler.sendMessageDelayed(msg, 600);
//            up_time = System.currentTimeMillis();
//            if (up_time - down_time < 500) {
//                startAnimation(event.getRawX(), event.getRawY());
//            } else {
//                down_time = 0;
//                up_time = 0;
//            }
//        }
        return true;
    }
}