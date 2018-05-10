package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.FilterBodysMenu;
import com.merrichat.net.activity.meiyu.FilterItem;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.view.MyCheckBox;

import java.util.List;

/**
 * Created by wangweiwei on 2018/3/26.
 */

public class FilterBodysAdapter extends RecyclerView.Adapter<FilterBodysAdapter.TRMViewHolder> {
    private Context mContext;
    private List<FilterItem> menuItemList;
    private boolean showIcon;
    private FilterBodysMenu mTopRightMenu;
    private FilterBodysMenu.OnMenuItemClickListener onMenuItemClickListener;

    private int selectPosition = -1;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public FilterBodysAdapter(Context context, FilterBodysMenu topRightMenu, List<FilterItem> menuItemList, boolean show) {
        this.mContext = context;
        this.mTopRightMenu = topRightMenu;
        this.menuItemList = menuItemList;
        this.showIcon = show;
    }

    public void setData(List<FilterItem> data) {
        menuItemList = data;
        notifyDataSetChanged();
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
        notifyDataSetChanged();
    }

    @Override
    public FilterBodysAdapter.TRMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.meat_bodys_item_menu_list, parent, false);
        return new FilterBodysAdapter.TRMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterBodysAdapter.TRMViewHolder holder, final int position) {
        final FilterItem menuItem = menuItemList.get(position);

        holder.tv_gender_type.setText(menuItem.filterName);
        holder.meat_girls_price.setText(menuItem.price + "美币/分钟");

        if (menuItem.genderType.equals("1")) {
            holder.iv_gender_ic.setBackgroundResource(R.mipmap.shaixuan_nan_2x);
        } else if (menuItem.genderType.equals("2")) {
            holder.iv_gender_ic.setBackgroundResource(R.mipmap.shaixuan_nv_2x);
            if (UserModel.getUserModel().getGender().equals("2")) {  //判断当前状态是否是女生，如果是女生则显示女生免费的提示
                holder.meat_girls_price.setText(menuItem.price + "美币/分钟,女生免费");
            }
        } else if (menuItem.genderType.equals("3")) {
            holder.iv_gender_ic.setBackgroundResource(R.mipmap.meiyu_nanshen);
        } else if (menuItem.genderType.equals("4")) {
            holder.iv_gender_ic.setBackgroundResource(R.mipmap.meiyu_nvshen);
        }

        if (selectPosition == position) {
            holder.mb_girls.setChecked(true);
            holder.meat_girls_price.setTextColor(Color.rgb(255, 61, 111));
        } else {
            holder.mb_girls.setChecked(false);
            holder.meat_girls_price.setTextColor(Color.rgb(136, 136, 136));

        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuItemClickListener != null) {
                    mTopRightMenu.dismiss();
                    onMenuItemClickListener.onMenuItemClick(position, menuItem.genderType, menuItem.price);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return menuItemList == null ? 0 : menuItemList.size();
    }

    class TRMViewHolder extends RecyclerView.ViewHolder {
        ViewGroup container;
        ImageView iv_gender_ic;
        TextView tv_gender_type;
        TextView meat_girls_price;
        MyCheckBox mb_girls;

        TRMViewHolder(View itemView) {
            super(itemView);
            container = (ViewGroup) itemView;
            iv_gender_ic = (ImageView) itemView.findViewById(R.id.iv_gender_ic);
            tv_gender_type = (TextView) itemView.findViewById(R.id.tv_gender_type);
            meat_girls_price = (TextView) itemView.findViewById(R.id.meat_girls_price);
            mb_girls = (MyCheckBox) itemView.findViewById(R.id.mb_girls);
        }
    }

    public void setOnMenuItemClickListener(FilterBodysMenu.OnMenuItemClickListener listener) {
        this.onMenuItemClickListener = listener;
    }

}
