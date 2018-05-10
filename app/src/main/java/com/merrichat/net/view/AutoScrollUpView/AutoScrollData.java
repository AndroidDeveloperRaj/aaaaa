package com.merrichat.net.view.AutoScrollUpView;

/**
 * 自动滚动View的数据
 */
public interface AutoScrollData<T> {

    /**
     * * 获取图片url
     *
     * @param data
     * @return
     */
    public String getIvUrl(T data);

    /**
     * 获取内容
     *
     * @param data
     * @return
     */
    public String getTextInfo(T data);

}
