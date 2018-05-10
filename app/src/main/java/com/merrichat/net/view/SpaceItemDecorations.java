package com.merrichat.net.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by AMSSY1 on 2017/11/9.
 */

public class SpaceItemDecorations extends RecyclerView.ItemDecoration {

    int mSpace;
    private int spanNum = 0;

    public SpaceItemDecorations(int space, int spanNum) {
        this.mSpace = space;
        this.spanNum = spanNum;
    }

    public SpaceItemDecorations(int space) {
        this.mSpace = space;
    }

    /**
     * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
     * the number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     * <p>
     * <p>
     * If this ItemDecoration does not affect the positioning of item views, it should set
     * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
     * before returning.
     * <p>
     * <p>
     * If you need to access Adapter for additional data, you can call
     * {@link RecyclerView#getChildAdapterPosition(View)} to get the adapter position of the
     * View.
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        MyStaggeredGridLayoutManager.LayoutParams params =
                (MyStaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        //outRect.top = mSpace / 2;
        outRect.bottom = mSpace;
        if (params.getSpanIndex() % 2 == 0) {
            outRect.left = 0;
            outRect.right = mSpace / 2;
        } else {
            outRect.left = mSpace / 2;
            outRect.right = 0;
        }
    }
}
