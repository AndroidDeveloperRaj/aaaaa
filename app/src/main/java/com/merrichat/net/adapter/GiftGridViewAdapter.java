package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.fragments.Gift;

import java.util.ArrayList;
import java.util.List;

///定影GridView的Adapter
public class GiftGridViewAdapter extends BaseAdapter {
    private int page;

    private List<Gift> gifts;
    private Context context;
    private int selectedPosition = -1;  //默认选择状态为-1
    private int showType;

    private ViewHolder viewHolder;

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
        notifyDataSetChanged();
    }

    /**
     * @param context context
     * @param page    页数
     */
    public GiftGridViewAdapter(Context context, int page, int showType) {
        this.page = page;
        this.showType = showType;
        this.context = context;
    }

    /**
     * 选择位置
     *
     * @param position
     */
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return gifts.size();

    }

    @Override
    public Gift getItem(int position) {
        // TODO Auto-generated method stub
        return gifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Gift catogary = gifts.get(position);

        Glide.with(context)
                .load(catogary.giftUrl)
                .priority(Priority.HIGH)
                .into(viewHolder.grid_fragment_home_item_img);

        viewHolder.grid_fragment_home_item_txt.setText(catogary.giftPrice + "讯美币");
        viewHolder.gift_name.setText(catogary.giftName);


        if (showType == 4) {
            if (selectedPosition == position) {
                viewHolder.grid_fragment_home_item_txt.setTextColor(Color.RED);
                viewHolder.gift_name.setTextColor(Color.RED);
                viewHolder.ll_gift.setBackgroundResource(R.drawable.bg_gift_border);
            } else {
                viewHolder.grid_fragment_home_item_txt.setTextColor(Color.GRAY);
                viewHolder.gift_name.setTextColor(Color.BLACK);
                viewHolder.ll_gift.setBackgroundResource(R.drawable.bg_gift_border_unselect);
            }
        } else {
            if (selectedPosition == position) {
                viewHolder.grid_fragment_home_item_txt.setTextColor(Color.RED);
                viewHolder.gift_name.setTextColor(Color.RED);
                viewHolder.ll_gift.setBackgroundResource(R.drawable.bg_gift_border);
            } else {
                viewHolder.grid_fragment_home_item_txt.setTextColor(Color.WHITE);
                viewHolder.gift_name.setTextColor(Color.WHITE);
                viewHolder.ll_gift.setBackgroundResource(R.drawable.bg_gift_border_unselect);
            }
        }


        return convertView;
    }


    public class ViewHolder {
        public LinearLayout ll_gift;
        public ImageView grid_fragment_home_item_img;
        public TextView gift_name; //礼物名称
        public TextView grid_fragment_home_item_txt;

        public ViewHolder(View convertView) {
            ll_gift = (LinearLayout) convertView.findViewById(R.id.ll_gift);
            grid_fragment_home_item_img = (ImageView) convertView.findViewById(R.id.grid_fragment_home_item_img);
            gift_name = (TextView) convertView.findViewById(R.id.gift_name);
            grid_fragment_home_item_txt = (TextView) convertView.findViewById(R.id.grid_fragment_home_item_txt);
        }
    }

    public void notify(int page) {
        this.notifyDataSetChanged();
    }

}
