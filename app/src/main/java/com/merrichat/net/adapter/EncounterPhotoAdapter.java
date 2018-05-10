package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merrichat.net.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/15.
 */

public class EncounterPhotoAdapter extends RecyclerView.Adapter<EncounterPhotoAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;

    private static List<String> data = new ArrayList<>();

    static {
        for (int i = 1; i <= 70; i++) {
            data.add(i + "");
        }
    }


    public EncounterPhotoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encounte_photo, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
