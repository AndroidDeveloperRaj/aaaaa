package com.merrichat.net.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.merrichat.net.adapter.TradeListAdapter;

/***
 * @ClassName: TradeListView
 * @Description: 用来展示 全部的账单：含有月份标题头，可以上拉刷新，下拉加载；
 * @date 2015-12-11 下午1:24:24
 */
public class TradeListView extends ListView implements OnScrollListener,
        Pullable {

    private TradeListAdapter mAdapter;
    private View mCurrentHeader;
    private int mCurrentHeaderViewType = 0;
    private float mHeaderOffset;
    private boolean mShouldPin = true;
    private int mCurrentSection = 0;
    private int mWidthMode;
    private int mHeightMode;
    private OnScrollListener mOnScrollListener;

    private int mLastVisibleItem = -1;
    private View footerView; // 脚布局对象
    private int footerViewHeight; // 脚布局的高度

    private final int UP_PULL = 0; // 脚布局状态: 上拉加载更多
    private int CURRENTSTATE = UP_PULL; // 脚布局当前的状态, 默认为: 上拉加载更多
    private boolean isLoadingMore = false; // 是否正在加载更多中, 默认为: 没有正在加载
    /*****
     * 是否可以上拉加载标记
     ********/
    private boolean flag = true;
    /*****
     * 是否可以下拉刷新标记
     ********/
    private boolean refreshFlag = true;
    // 回滚速度
    public float MOVE_SPEED = 8;

    public TradeListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public TradeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    public TradeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    // 做过刷新后去调用的方法
    public void finish() {
        // 加载完成后的业务逻辑
        if (isLoadingMore) {
            // 真正的做过加载操作
            isLoadingMore = false;
            // 隐藏脚底
            footerView.setPadding(0, -footerViewHeight, 0, 0);
        }
    }

    /**
     * 设置是否需要分割的头条目
     *
     * @param shouldPin
     */
    public void setPinHeaders(boolean shouldPin) {
        mShouldPin = shouldPin;
    }

    /**
     * 设置自定义ListView的适配器 将当前头条目对应的View对象置空
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        mAdapter = (TradeListAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
        if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin
                || (firstVisibleItem < getHeaderViewsCount())) {
            mCurrentHeader = null;
            mHeaderOffset = 0.0f;
            for (int i = firstVisibleItem; i < firstVisibleItem
                    + visibleItemCount; i++) {
                View header = getChildAt(i);
                if (header != null) {
                    header.setVisibility(VISIBLE);
                }
            }

            return;
        }

        firstVisibleItem -= getHeaderViewsCount();

        int section = mAdapter.getSectionForPosition(firstVisibleItem);
        int viewType = mAdapter.getSectionHeaderViewType(section);
        mCurrentHeader = getSectionHeaderView(section,
                mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
        ensurePinnedHeaderLayout(mCurrentHeader);
        mCurrentHeaderViewType = viewType;

        mHeaderOffset = 0.0f;

        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (mAdapter.isSectionHeader(i)) {
                View header = getChildAt(i - firstVisibleItem);
                float headerTop = header.getTop();
                float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
                header.setVisibility(VISIBLE);
                if (pinnedHeaderHeight >= headerTop && headerTop > 0) {
                    mHeaderOffset = headerTop - header.getHeight();
                } else if (headerTop <= 0) {
                    header.setVisibility(INVISIBLE);
                }
            }
        }
        mLastVisibleItem = visibleItemCount;
        invalidate();
    }

    /**
     * 设置分割标题头的数据源
     */
    public static interface PinnedSectionedHeaderAdapter {
        /**
         * 判断当前条目是否为标题头
         *
         * @param position
         * @return boolean
         */
        public boolean isSectionHeader(int position);

        public int getSectionForPosition(int position);

        /**
         * 获取对应位置的头布局
         *
         * @param section
         * @param convertView
         * @param parent
         * @return
         */
        public View getSectionHeaderView(int section, View convertView,
                                         ViewGroup parent);

        /**
         * 获取对应分割头条目的类型
         *
         * @param section
         * @return
         */
        public int getSectionHeaderViewType(int section);

        /**
         * 获取总数
         *
         * @return
         */
        public int getCount();
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = section != mCurrentSection || oldView == null;

        View view = mAdapter.getSectionHeaderView(section, oldView, this);
        if (shouldLayout) {
            // a new section, thus a new header. We should lay it out again
            ensurePinnedHeaderLayout(view);
            mCurrentSection = section;
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header) {
        if (header.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height,
                        MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED);
            }
            header.measure(widthSpec, heightSpec);
            header.layout(0, 0, header.getMeasuredWidth(),
                    header.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAdapter == null || !mShouldPin || mCurrentHeader == null)
            return;
        int saveCount = canvas.save();
        canvas.translate(0, mHeaderOffset);
        canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight());
        mCurrentHeader.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec);
    }

    public void setOnItemClickListener(
            OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int rawPosition, long id) {
            TradeListAdapter adapter;
            if (adapterView.getAdapter().getClass()
                    .equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView
                        .getAdapter();
                adapter = (TradeListAdapter) wrapperAdapter.getWrappedAdapter();
            } else {
                adapter = (TradeListAdapter) adapterView.getAdapter();
            }
            int section = adapter.getSectionForPosition(rawPosition);
            int position = adapter.getPositionInSectionForPosition(rawPosition);

            if (position == -1) {
                onSectionClick(adapterView, view, section, id);
            } else {
                onItemClick(adapterView, view, section, position, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view,
                                         int section, int position, long id);

        public abstract void onSectionClick(AdapterView<?> adapterView,
                                            View view, int section, long id);

    }

    /**
     * 获取当前控件加载状态的方法
     */
    public int getFootViewStatus() {
        return CURRENTSTATE;
    }

    @Override
    public boolean canPullDown() {
        if (!refreshFlag) {
            return false;
        }
        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (getFirstVisiblePosition() == 0
                && getChildAt(0).getTop() >= 0) {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }

    /**
     * 设置不可以下拉刷新
     */
    public void setDisAblePullDown() {
        refreshFlag = false;

    }

    /**
     * 设置可以下拉刷新
     */
    public void setAblePullDown() {
        refreshFlag = true;
    }

    /**
     * 设置不可以上拉加载
     */
    public void setDisAblePullUp() {
        flag = false;

    }

    /**
     * 设置可以上拉加载
     */
    public void setAblePullUp() {
        flag = true;
    }

    @Override
    public boolean canPullUp() {
        if (!flag) {
            return false;
        }
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}
