package com.merrichat.net.adapter.abslistview.base;


import com.merrichat.net.adapter.abslistview.ViewHolder;

/**
 * Created by chenjingjing on 17/7/29.
 */

public interface ItemViewDelegate<T> {

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public abstract void convert(ViewHolder holder, T t, int position);


}
