package com.merrichat.net.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 2018/1/22.
 */

public class MapAdapter extends BaseQuickAdapter<PoiInfo, BaseViewHolder> {

    private int mIndexTag = 0;
    private PoiInfo mUserPoiInfo;
    protected List<PoiInfo> mDatas;//一般数据

    public MapAdapter(int layoutResId, List<PoiInfo> data) {
        super(layoutResId, data);
        this.mDatas = data;
    }

    public int getmIndexTag() {
        return mIndexTag;
    }

    public void setmUserPoiInfo(PoiInfo userPoiInfo) {
        this.mUserPoiInfo = userPoiInfo;
    }

    public void setmIndexTag(int mIndexTag) {
        this.mIndexTag = mIndexTag;
        notifyDataSetChanged();
    }


    @Override
    protected void convert(BaseViewHolder helper, PoiInfo item) {


        TextView tvTitle = helper.getView(R.id.tv_title);

        TextView tvContent = helper.getView(R.id.tv_content);
        ImageView iv_tip = helper.getView(R.id.iv_tip);

        tvTitle.setText(item.name);

        tvContent.setText(item.address);

        if (mIndexTag == helper.getAdapterPosition()) {
            iv_tip.setVisibility(View.VISIBLE);
        } else {
            iv_tip.setVisibility(View.GONE);
        }
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     *
     * @param datas     数据
     * @param isRefresh 是否刷新
     */
    public void setDatas(List<PoiInfo> datas, boolean isRefresh) {
        if (mUserPoiInfo != null && datas != null) {
            datas.add(0, mUserPoiInfo);
        }
        if (datas == null)
            datas = new ArrayList<>();
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
        this.mDatas.addAll(datas);
        if (isRefresh)
            notifyDataSetChanged();
        mIndexTag = 0;
    }

    /**
     * @param datas 数据
     */
    public void setDatas(List<PoiInfo> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
        mIndexTag = -1;
    }

    /**
     * 加载更多
     *
     * @param datas 数据
     */
    public void addDatas(List<PoiInfo> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        if (mDatas != null) {
            this.mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas != null) {
            this.mDatas.clear();
            notifyDataSetChanged();
        }
    }
}
