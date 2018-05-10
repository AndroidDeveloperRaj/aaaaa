package com.merrichat.net.view.AutoScrollUpView;

import android.content.Context;
import android.util.AttributeSet;

import com.merrichat.net.model.GroupMarketModel;

/**
 * Created by amssy on 2018/2/27.
 */

/**
 * 制作主页的向上广告滚动条
 * AdvertisementObject是主页的数据源，假如通过GSON或FastJson获取的实体类
 * Created by amssy on 2018/2/27.
 */
public class MainScrollUpAdvertisementView extends BaseAutoScrollUpView<GroupMarketModel.DataBean> {

    public MainScrollUpAdvertisementView(Context context, AttributeSet attrs,
                                         int defStyle) {
        super(context, attrs, defStyle);
    }

    public MainScrollUpAdvertisementView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainScrollUpAdvertisementView(Context context) {
        super(context);
    }

    @Override
    public String getIvUrl(GroupMarketModel.DataBean data) {
        return data.getMemberUrl();
    }

    @Override
    public String getTextInfo(GroupMarketModel.DataBean data) {
        return data.getMemberNick() + "发布了" + data.getProductName();
    }

    /**
     * 这里面的高度应该和你的xml里设置的高度一致
     */
    @Override
    protected int getAdertisementHeight() {
        return 55;
    }

}
