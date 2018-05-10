package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.PhotoFilmPicModel;

import java.util.ArrayList;

/**
 * 视频编辑适配器
 */
public class VideoEditorFilterAdapter extends
        RecyclerView.Adapter<VideoEditorFilterAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context context;
    private int layoutPosition = 0;//当前点击的位置
    private ArrayList<PhotoFilmPicModel> filterList;//滤镜集合

    public VideoEditorFilterAdapter(Context context, ArrayList<PhotoFilmPicModel> filterList) {
        this.context = context;
        this.filterList = filterList;
        mInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_bg;
        SimpleDraweeView imageViewFilter;
        Button btn_filter;
        public ViewHolder(View view) {
            super(view);
            rel_bg = (RelativeLayout) view.findViewById(R.id.rel_bg);
            imageViewFilter = (SimpleDraweeView) view.findViewById(R.id.image_filter);
            btn_filter = (Button) view.findViewById(R.id.filter_name);
        }
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_filter,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        PhotoFilmPicModel filterModel = filterList.get(position);
        Uri uri = Uri.parse("res://"+context.getPackageName()+"/" + filterModel.getCutIconId());
        viewHolder.imageViewFilter.setImageURI(uri);
        viewHolder.btn_filter.setText(filterModel.getCutName());

        viewHolder.rel_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前点击的位置
                layoutPosition = viewHolder.getLayoutPosition();
                notifyDataSetChanged();
                mItemClickListener.onFilterItemClick(position, viewHolder.rel_bg);
            }
        });

        //单选通过这个方法实现
        if (position == layoutPosition) {
            viewHolder.rel_bg.setBackground(context.getResources().getDrawable(R.drawable.photofilm_item_square_selected));
            viewHolder.btn_filter.setTextColor(context.getResources().getColor(R.color.normal_red));
        } else {
            viewHolder.rel_bg.setBackgroundColor(Color.parseColor("#00000000"));
            viewHolder.btn_filter.setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    protected OnFilterItemClickListener mItemClickListener;

    public interface OnFilterItemClickListener {
        void onFilterItemClick(int position, View v);
    }

    public void setOnFilterItemClickListener(OnFilterItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
