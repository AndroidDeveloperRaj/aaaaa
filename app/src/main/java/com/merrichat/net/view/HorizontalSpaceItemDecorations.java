package com.merrichat.net.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by AMSSY1 on 2017/11/16.
 */

public class HorizontalSpaceItemDecorations extends RecyclerView.ItemDecoration {
    private int flag = 0;
    private int mSpace;
    private int mBottom;
    private int mLeft;
    private int mTop;
    private int mRight;

    public HorizontalSpaceItemDecorations(int space) {
        flag = 1;
        this.mSpace = space;
    }

    public HorizontalSpaceItemDecorations(int left, int top, int right, int bottom) {
        flag = 2;
        this.mBottom = bottom;
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (flag == 1) {
            outRect.left = mSpace;
        }
        if (flag == 2) {
            outRect.left =mLeft;
            outRect.top =mTop;
            outRect.right =mRight;
            outRect.bottom =mBottom;
        }

    }
}
