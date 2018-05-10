package com.merrichat.net.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.merrichat.net.interfaces.LoadFinishCallBack;

/**
 * Created by AMSSY1 on 2017/12/9.
 */


public class ScrollNoLoadImgRecyclerView extends RecyclerView implements LoadFinishCallBack {

    private onLoadMoreListener loadMoreListener;    //加载更多回调
    private boolean isLoadingMore;                  //是否加载更多

    public ScrollNoLoadImgRecyclerView(Context context) {
        this(context, null);
    }

    public ScrollNoLoadImgRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollNoLoadImgRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        isLoadingMore = false;  //默认无需加载更多
        setOnScrollListener(new AutoLoadScrollListener(null, true, true));
    }

    /**
     * 配置显示图片，需要设置这几个参数，快速滑动时，暂停图片加载
     *
     * @param glider        ImageLoader实例对象
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public void setOnPauseListenerParams(RequestManager glider, boolean pauseOnScroll, boolean pauseOnFling) {

        setOnScrollListener(new AutoLoadScrollListener(glider, pauseOnScroll, pauseOnFling));

    }

    public void setLoadMoreListener(onLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public void loadFinish(Object obj) {
        isLoadingMore = false;
    }


    //加载更多的回调接口
    public interface onLoadMoreListener {
        void loadMore();
    }


    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;
        private RequestManager glider;

        public AutoLoadScrollListener(RequestManager glider, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.glider = glider;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = ScrollNoLoadImgRecyclerView.this.getAdapter().getItemCount();

                //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                        2 && dy > 0) {
                    loadMoreListener.loadMore();
                    isLoadingMore = true;
                }
            }
        }

        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；由于用户的操作，屏幕产生惯性滑动时为2
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            //根据newState状态做处理
            if (glider != null) {
                switch (newState) {
                    case 0:
                        glider.resumeRequests();
                        break;

                    case 1:
                        if (pauseOnScroll) {
                            glider.pauseRequests();
                        } else {
                            glider.resumeRequests();
                        }
                        break;

                    case 2:
                        if (pauseOnFling) {
                            glider.pauseRequests();
                        } else {
                            glider.resumeRequests();
                        }
                        break;
                }
            }
        }
    }
}