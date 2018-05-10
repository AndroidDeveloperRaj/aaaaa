package com.merrichat.net.activity.message.weidget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.message.SingleChatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.merrichat.net.activity.message.SingleChatActivity.item_single_chat_panel_grid_num;

/**
 * Created by MQ on 2016/11/11.
 */

public class ChatPanelGridViewAdapter extends BaseAdapter {
    private List<SingleChatPanelBean> dataList;
    private OnItemClickListener onItemClickListener;
    public static final int[] SingleChatPanelResId = {
            R.mipmap.icon_chatting_zhaopian, R.mipmap.icon_chatting_paizhao, R.mipmap.icon_chatting_small_video, R.mipmap.icon_chatting_video_call,
            R.mipmap.icon_chatting_red_package, R.mipmap.icon_chatting_liwu, R.mipmap.icon_chatting_send_deal, R.mipmap.icon_chatting_zhuan_zhang,
            R.mipmap.icon_chatting_send_location};
    public static final String[] SingleChatPanelName = {"照片", "拍照", "小视频", "视频通话", "红包", "礼物", "发布交易", "转账", "位置"};
    public static final int[] SingleChatPanelResId_ = {
            R.mipmap.icon_chatting_zhaopian, R.mipmap.icon_chatting_paizhao, R.mipmap.icon_chatting_small_video, R.mipmap.icon_chatting_liwu,
            R.mipmap.icon_chatting_send_deal, R.mipmap.icon_chatting_zhuan_zhang, R.mipmap.icon_chatting_send_location};
    public static final String[] SingleChatPanelName_ = {"照片", "拍照", "小视频", "礼物", "发布交易", "转账", "位置"};

    public ChatPanelGridViewAdapter(List<SingleChatPanelBean> datas, int page) {
        dataList = new ArrayList<>();
        //start end分别代表要显示的数组在总数据List中的开始和结束位置
        int start = page * item_single_chat_panel_grid_num;
        int end = start + item_single_chat_panel_grid_num;
        while ((start < datas.size()) && (start < end)) {
            dataList.add(datas.get(start));
            start++;
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View itemView, ViewGroup viewGroup) {
        ViewHolder mHolder;
        if (itemView == null) {
            mHolder = new ViewHolder();
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_single_chat_panel, viewGroup, false);
            mHolder.iv_img = (ImageView) itemView.findViewById(R.id.iv_single_chat_panel);
            mHolder.tv_text = (TextView) itemView.findViewById(R.id.tv_single_chat_panel);
            itemView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) itemView.getTag();
        }
        SingleChatPanelBean bean = dataList.get(i);
        if (bean != null) {
            mHolder.iv_img.setImageResource(bean.resId);
            mHolder.tv_text.setText(bean.name);
        }
        ((View) mHolder.iv_img.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(dataList.get(i));
                }
            }
        });
        return itemView;
    }

    private class ViewHolder {
        private ImageView iv_img;
        private TextView tv_text;
    }

    public interface OnItemClickListener {
        void onItemClick(SingleChatPanelBean dataBean);
    }

    public ChatPanelGridViewAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(ChatPanelGridViewAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
