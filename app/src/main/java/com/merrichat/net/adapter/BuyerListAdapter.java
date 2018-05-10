package com.merrichat.net.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.model.SellOrderModel;
import com.merrichat.net.model.WaiteGroupBuyModel;

import java.util.List;

/**
 * Created by amssy on 18/1/23.
 * 群订单管理---待收货列表--买家列表
 */

public class BuyerListAdapter extends BaseAdapter {
    private Context context;

    private List<SellOrderModel.DataBean.SerialMemberBean> list;

    public BuyerListAdapter(Context context, List<SellOrderModel.DataBean.SerialMemberBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_buyer_list, parent, false);
            holder = new ViewHolder();
            holder.tv_buy_name = (TextView) convertView.findViewById(R.id.tv_buy_name);
            holder.rl_all = (RelativeLayout) convertView.findViewById(R.id.rl_all);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_buy_name.setText("买家昵称: "+list.get(position).getMemberName());
        holder.rl_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_buy_name;
        RelativeLayout rl_all;
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
