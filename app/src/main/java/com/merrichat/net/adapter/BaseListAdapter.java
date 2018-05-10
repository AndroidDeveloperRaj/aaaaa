package com.merrichat.net.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author : 桥下一粒砂 chenyoca@gmail.com
 * date    : 2012-9-13
 * 抽象Adapter类
 */

/**
 * @ClassName: BaseListAdapter
 * @Description: listView base adapter
 * @author: xxywy
 * @date: 2013-9-26 下午1:45:51
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> data;
    protected Context mContext;
    protected LayoutInflater inflater;

    public BaseListAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    /**
     * 更新Adapter的数据集。
     * 
     * @param data
     *            数据集
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * 清空Adapter的数据集
     */
    public void clear() {
        if (data != null) {
            data.clear();
        }
    }

    /**
     * 提交更新
     */
    public void postChange() {
        notifyDataSetChanged();
    }

    /**
     * 刷新
     */
    public void postInvalidate() {
        notifyDataSetInvalidated();
    }

    /**
     * 添加数据集，向数据缓存中添加多个元素。
     * 
     * @param set
     *            元素集
     */
    public void add(List<T> set) {
        if (null == data) {
            throw new NullPointerException("DataSet is NULL, call 'update' first !");
        }
        data.addAll(set);
    }

    /**
     * 删除数据集中指定位置的数据。
     * 
     * @param position
     *            要删除的数据在数据集中的位置
     */
    public void remove(int position) {
        if (data == null) return;
        data.remove(position);
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return null == data ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateListView(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
