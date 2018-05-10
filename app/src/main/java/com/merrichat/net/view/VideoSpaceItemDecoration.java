package com.merrichat.net.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.merrichat.net.utils.StringUtil;

/**
 * 适配器间距
 * Created by amssy on 17/11/10.
 */

public class VideoSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private Context context;

    /**
     * @param context
     * @param space   item间距
     */
    public VideoSpaceItemDecoration(Context context, int space) {
        this.space = space;
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) {//第一个设置距离
            outRect.left = StringUtil.getWidths(context) / 2;
        } else {
            outRect.left = space;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //最后一项需要right
        if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {//最后一个设置距离
            outRect.right = StringUtil.getWidths(context) / 2;
        }
    }
}
