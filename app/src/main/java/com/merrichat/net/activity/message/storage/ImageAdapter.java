package com.merrichat.net.activity.message.storage;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.message.util.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {

    // 存储选中图片
    public static Set<String> mselectedImg = new HashSet<String>();
    private final Context context;
    private String mDirPath;
    // 所有图片路径
    private List<String> mImgPaths;
    private LayoutInflater mInflater;
    private int mScreenWidth;
    private int photoCountNum;
    private int flagNum = 0;

    public ImageAdapter(Context context, List<String> mDatas, String dirPath, int photoCountNum) {
        this.context = context;
        this.mDirPath = dirPath;
        this.mImgPaths = mDatas;
        this.photoCountNum = photoCountNum;

        mInflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mImg = (ImageView) convertView
                    .findViewById(R.id.id_item_image);
            viewHolder.mSelect = (ImageButton) convertView
                    .findViewById(R.id.id_item_select);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 重置状态
        viewHolder.mImg.setImageResource(R.mipmap.pictures_no);
        viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
        viewHolder.mImg.setColorFilter(null);

        viewHolder.mImg.setMaxWidth(mScreenWidth / 3);
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(
                mDirPath + "/" + mImgPaths.get(position), viewHolder.mImg);

        final String filePath = mDirPath + "/" + mImgPaths.get(position);

        viewHolder.mImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // 已经被选择
                if (new PhotoCount().getPhotosPaths().contains(filePath)) {
                    flagNum--;
                    // mselectedImg.remove(filePath);
                    new PhotoCount().getPhotosPaths().remove(filePath);
                    viewHolder.mImg.setColorFilter(null);
                    viewHolder.mSelect
                            .setImageResource(R.mipmap.picture_unselected);

                } else {// 图片未被选择
                    // mselectedImg.add(filePath);

//                    int size = new PhotoCount().getPhotosPaths().size();
//                    if (size >= photoCountNum) {
//                        if (photoCountNum == 4) {
//                            Toast.makeText(context, "超过四张了", Toast.LENGTH_SHORT)
//                                    .show();
//                        } else {//
//                            Toast.makeText(context, "超过" + photoCountNum + "张了", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    } else {
//                        new PhotoCount().getPhotosPaths().add(filePath);
//                        viewHolder.mImg.setColorFilter(Color
//                                .parseColor("#77000000"));
//                        viewHolder.mSelect
//                                .setImageResource(R.mipmap.pictures_selected);
//                    }
                    if (flagNum < photoCountNum) {
                        flagNum++;
                        new PhotoCount().getPhotosPaths().add(filePath);
                        viewHolder.mImg.setColorFilter(Color
                                .parseColor("#77000000"));
                        viewHolder.mSelect
                                .setImageResource(R.mipmap.pictures_selected);
                    } else {
                        Toast.makeText(context, "超过" + photoCountNum + "张了", Toast.LENGTH_SHORT).show();
                    }
                }

                // notifyDataSetChanged();
            }
        });

		/*
         * if (mselectedImg.contains(filePath)) {
		 * viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
		 * viewHolder.mSelect.setImageResource(R.drawable.pictures_selected); }
		 */
        if (new PhotoCount().getPhotosPaths().contains(filePath)) {
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.mipmap.pictures_selected);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView mImg;
        private ImageButton mSelect;

    }
}

