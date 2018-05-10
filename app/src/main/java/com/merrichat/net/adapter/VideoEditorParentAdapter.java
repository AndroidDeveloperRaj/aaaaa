package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.videoutil.ItemToucHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * 视频编辑适配器
 */
public class VideoEditorParentAdapter extends
        RecyclerView.Adapter<VideoEditorParentAdapter.ViewHolder> implements ItemToucHelperAdapter{
    private LayoutInflater mInflater;
    private List<List<Bitmap>> listChild;
    private Context context;
    private int layoutPosition = 0;//当前点击的位置

    public void addData(List<Bitmap> list,int position){
        listChild.add(position,list);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void removeData(int position){
        listChild.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public VideoEditorParentAdapter(Context context, List<List<Bitmap>> list) {
        this.context = context;
        this.listChild = list;
        mInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linParent;
        View viewLeft;
        View viewRight;
        View viewTop;
        View viewBottom;

        public ViewHolder(View view) {
            super(view);
            linParent = (LinearLayout) view.findViewById(R.id.lin_parent);
            viewLeft = view.findViewById(R.id.view_left);
            viewRight = view.findViewById(R.id.view_right);
            viewTop = view.findViewById(R.id.view_top);
            viewBottom = view.findViewById(R.id.view_bottom);
        }
    }

    @Override
    public int getItemCount() {
        return listChild.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_video_editor,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        List<Bitmap> list = listChild.get(position);

        viewHolder.linParent.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_video_editor_child, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView_child);

            imageView.setImageBitmap(list.get(i));

            viewHolder.linParent.addView(view);
        }

        viewHolder.linParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前点击的位置
                //layoutPosition = viewHolder.getLayoutPosition();
                //notifyDataSetChanged();
                //mItemClickListener.onItemClick(position, viewHolder.linParent);
            }
        });

        //单选通过这个方法实现
        /*if (position == layoutPosition) {
            viewHolder.viewLeft.setVisibility(View.VISIBLE);
            viewHolder.viewRight.setVisibility(View.VISIBLE);
            viewHolder.viewTop.setVisibility(View.VISIBLE);
            viewHolder.viewBottom.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewLeft.setVisibility(View.GONE);
            viewHolder.viewRight.setVisibility(View.GONE);
            viewHolder.viewTop.setVisibility(View.GONE);
            viewHolder.viewBottom.setVisibility(View.GONE);
        }*/

    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 下面四个方法选用
     * @param source
     * @param target
     */
    @Override
    public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < listChild.size() && toPosition < listChild.size()) {
            //交换数据位置
            Collections.swap(listChild, fromPosition, toPosition);
            //刷新位置交换
            notifyItemMoved(fromPosition, toPosition);
        }
        //移动过程中移除view的放大效果
        onItemClear(source);
    }

    @Override
    public void onItemDissmiss(RecyclerView.ViewHolder source) {
        /*int position = source.getAdapterPosition();
        listChild.remove(position); //移除数据
        notifyItemRemoved(position);//刷新数据移除*/
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder source) {
        //当拖拽选中时放大选中的view
        source.itemView.setScaleX(1.2f);
        source.itemView.setScaleY(1.2f);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder source) {
        //拖拽结束后恢复view的状态
        source.itemView.setScaleX(1.0f);
        source.itemView.setScaleY(1.0f);
    }
}
