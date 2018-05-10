package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.merrichat.net.R;

import java.util.List;

/**
 * 图片视频显示适配器
 */
public class VideoSpecialAdapter extends
        RecyclerView.Adapter<VideoSpecialAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<Bitmap> list;
    private Context context;

    public VideoSpecialAdapter(Context context, List<Bitmap> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        View viewLeft;
        View viewRight;
        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.fiv);
            viewLeft = view.findViewById(R.id.view_left);
            viewRight = view.findViewById(R.id.view_right);
        }
    }

    @Override
    public int getItemCount() {
       return list.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_special_video,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (position == 0){
            //获取屏幕的宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.viewLeft.getLayoutParams();
            params.width = width / 2;
            viewHolder.viewLeft.setLayoutParams(params);

            viewHolder.viewLeft.setVisibility(View.VISIBLE);
        }else {
            viewHolder.viewLeft.setVisibility(View.GONE);
        }
        if (position == list.size() - 1){
            //获取屏幕的宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.viewRight.getLayoutParams();
            params.width = width / 2;
            viewHolder.viewRight.setLayoutParams(params);

            viewHolder.viewRight.setVisibility(View.VISIBLE);
        }else {
            viewHolder.viewRight.setVisibility(View.GONE);
        }

        viewHolder.mImg.setImageBitmap(list.get(position));
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
