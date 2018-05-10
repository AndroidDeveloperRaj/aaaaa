package com.merrichat.net.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.merrichat.net.R;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.view.EffectAndFilterItemView;


/**
 * Created by lirui on 2017/1/20.
 */

public class EffectAndFilterSelectAdapter extends RecyclerView.Adapter<EffectAndFilterSelectAdapter.ItemViewHolder> {

    public static final int[] EFFECT_ITEM_RES_ARRAY = {
            R.mipmap.icon_remove_beautify
    };
    public static final String[] EFFECT_ITEM_FILE_NAME = {"none"};

    public static final int[] FILTER_ITEM_RES_ARRAY = {
            R.mipmap.icon_fiter_nature, R.mipmap.icon_fiter_delta, R.mipmap.icon_fiter_electric, R.mipmap.icon_fiter_slowlived, R.mipmap.icon_fiter_tokyo,
            R.mipmap.icon_fiter_warm, R.mipmap.icon_fiter_white_level, R.mipmap.icon_fiter_polaroid, R.mipmap.icon_fiter_crimson, R.mipmap.icon_fiter_fuji,
            R.mipmap.icon_fiter_dry, R.mipmap.icon_fiter_concrete, R.mipmap.icon_fiter_kodak, R.mipmap.icon_fiter_rollei, R.mipmap.icon_fiter_cyan,
            R.mipmap.icon_fiter_autumn, R.mipmap.icon_fiter_sunshine, R.mipmap.icon_fiter_pearl, R.mipmap.icon_fiter_abao, R.mipmap.icon_fiter_dew,
            R.mipmap.icon_fiter_girly, R.mipmap.icon_fiter_pink, R.mipmap.icon_fiter_hdr, R.mipmap.icon_fiter_blackwhite, R.mipmap.icon_fiter_cold,
            R.mipmap.icon_fiter_red_tea, R.mipmap.icon_fiter_refreshing, R.mipmap.icon_fiter_japanese, R.mipmap.icon_fiter_silver, R.mipmap.icon_fiter_sweet,
            R.mipmap.icon_fiter_forest, R.mipmap.icon_fiter_sakura, R.mipmap.icon_fiter_hongkong, R.mipmap.icon_fiter_cloud, R.mipmap.icon_fiter_boardwalk,
            R.mipmap.icon_fiter_cruz, R.mipmap.icon_fiter_keylime, R.mipmap.icon_fiter_lucky
    };
    public final static String[] FILTERS_NAME = MerriApp.getContext().getResources().getStringArray(R.array.fiter_english);
    public final static String[] FILTERS_CHINESE_NAME = MerriApp.getContext().getResources().getStringArray(R.array.fiter_chinese);

    public static final int RECYCLEVIEW_TYPE_EFFECT = 0;
    public static final int RECYCLEVIEW_TYPE_FILTER = 1;

    private RecyclerView mOwnerRecyclerView;
    private int mOwnerRecyclerViewType;

    private final int EFFECT_DEFAULT_CLICK_POSITION = 0;
    private final int FILTER_DEFAULT_CLICK_POSITION = 0;
    private EffectAndFilterItemView lastClickItemView = null;
    private int lastClickPosition = EFFECT_DEFAULT_CLICK_POSITION;
    private OnItemSelectedListener mOnItemSelectedListener;

    public EffectAndFilterSelectAdapter(RecyclerView recyclerView, int recyclerViewType) {
        mOwnerRecyclerView = recyclerView;
        mOwnerRecyclerViewType = recyclerViewType;
    }

    @Override
    public int getItemCount() {
        return mOwnerRecyclerViewType == RECYCLEVIEW_TYPE_EFFECT ?
                EFFECT_ITEM_RES_ARRAY.length :
                FILTER_ITEM_RES_ARRAY.length;
    }

    String getHintStringByPosition(int position) {
        String res = "";
        switch (EFFECT_ITEM_RES_ARRAY[position]) {
//            case R.mipmap.mood:
//                res = "嘴角向上或嘴角向下";
//                break;
//            case R.mipmap.fu_zh_duzui:
//                res = "嘟嘴";
//                break;
        }
        return res;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == lastClickPosition) {
            holder.mItemView.setSelectedBackground();
            lastClickItemView = holder.mItemView;
        } else holder.mItemView.setUnselectedBackground();

        if (mOwnerRecyclerViewType == RECYCLEVIEW_TYPE_EFFECT) {
            holder.mItemView.setItemIcon(EFFECT_ITEM_RES_ARRAY[adapterPosition % EFFECT_ITEM_RES_ARRAY.length]);
        } else {
            holder.mItemView.setItemIcon(FILTER_ITEM_RES_ARRAY[adapterPosition % FILTER_ITEM_RES_ARRAY.length]);
            holder.mItemView.setItemText(FILTERS_CHINESE_NAME[adapterPosition % FILTER_ITEM_RES_ARRAY.length].toUpperCase());
        }

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickItemView != null) lastClickItemView.setUnselectedBackground();
                lastClickItemView = holder.mItemView;
                lastClickPosition = adapterPosition;
                holder.mItemView.setSelectedBackground();
                setClickPosition(adapterPosition);
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(new EffectAndFilterItemView(parent.getContext(), mOwnerRecyclerViewType));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        EffectAndFilterItemView mItemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mItemView = (EffectAndFilterItemView) itemView;
        }
    }

    private void setClickPosition(int position) {
        if (position < 0) {
            return;
        }
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(position);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int itemPosition);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }


    public int getLastClickPosition() {
        return lastClickPosition;
    }

    public void setLastClickPosition(int lastClickPosition) {
        this.lastClickPosition = lastClickPosition;
    }
}
