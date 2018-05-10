package com.merrichat.net.activity.message.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.message.storage.FolderBean;

import java.util.List;

public class ListImageDirPoppupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;
    private OnDirSelectedListener mListener;
    //被选中的文件夹的文字
    private String mCurrentDir;

    public void setmCurrentDir(String mCurrentDir) {
        this.mCurrentDir = mCurrentDir;
    }

    public void setmListener(OnDirSelectedListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDirSelectedListener {

        void onSelected(FolderBean folderBean);
    }


    public ListImageDirPoppupWindow(Context context, List<FolderBean> mDatas) {

        calWidthAndHeight(context);


        mConvertView = LayoutInflater.from(context).inflate(R.layout.list_dir, null);

        this.mDatas = mDatas;

        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);


        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }

                return false;
            }
        });

        initViews(context);

        initEvent();
    }

    private void initViews(Context context) {
        // TODO Auto-generated method stub
        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context, mDatas));
    }

    private void initEvent() {
        // TODO Auto-generated method stub
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                if (mListener != null) {
                    mListener.onSelected(mDatas.get(position));
                }

            }
        });
    }


    /**
     * 计算popupwindow的宽度和高度
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        // TODO Auto-generated method stub
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.7);


    }

    private class ListDirAdapter extends BaseAdapter {

        private LayoutInflater mInflate;
        private List<FolderBean> mDatas;
        private Context context;

        public ListDirAdapter(Context context, List<FolderBean> mDatas) {

            this.context = context;
            this.mDatas = mDatas;
            mInflate = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflate.inflate(R.layout.list_dir_item, parent, false);
                viewHolder = new ViewHolder();


                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FolderBean bean = mDatas.get(position);
            viewHolder.mImg.setImageResource(R.mipmap.pictures_no);
            ImageLoader.getInstance().loadImage(bean.getFirstImagePath(), viewHolder.mImg);
            String folderName = bean.getName();
            if (folderName.startsWith("/")) {
                folderName = folderName.substring(1, folderName.length());
            }
            viewHolder.mDirName.setText(folderName);
            viewHolder.mDirCount.setText(bean.getCount() + "");

            if (folderName.equals(mCurrentDir)) {
                viewHolder.iv_choose.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_choose.setVisibility(View.GONE);
            }

            return convertView;
        }


        private class ViewHolder {
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
            ImageView iv_choose;

        }
    }
}
