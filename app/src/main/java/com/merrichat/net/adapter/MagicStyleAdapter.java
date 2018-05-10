package com.merrichat.net.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.QueryMagicListModel;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.view.CircularProgressView;

import org.akita.ui.adapter.AkBaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangweiwei on 2018/3/31.
 */

public class MagicStyleAdapter extends AkBaseAdapter<QueryMagicListModel.MagicType> {

    ViewHolder holder = null;
    private Fragment mFragment;
    private Context context;
    private int mSelectPosition = -1;

    public MagicStyleAdapter(Context context, Fragment fragment) {
        super(fragment.getActivity());
        this.context = context;
        mFragment = fragment;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int mSelectPosition) {
        this.mSelectPosition = mSelectPosition;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = (mFragment.getActivity().getLayoutInflater()).inflate(
                    R.layout.item_magic_style, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QueryMagicListModel.MagicType data = mData.get(position);

        if (mSelectPosition == position) {
            holder.simpleCoverBg.setVisibility(View.VISIBLE);

            if (data != null && data.thumbnailUrl != null) {
                File file = new File(data.imgUrl);
                if (isFileExit(pathListFile(FileUtils.photoMagicPath), file.getName())) {
                    holder.simpleDownload.setVisibility(View.GONE);
                    holder.progressRate.setVisibility(View.GONE);
                } else {
                    holder.simpleDownload.setVisibility(View.VISIBLE);
                    holder.progressRate.setVisibility(View.VISIBLE);
                }
            } else {
                holder.progressRate.setVisibility(View.GONE);

            }

        } else {
            holder.progressRate.setVisibility(View.GONE);
            holder.simpleCoverBg.setVisibility(View.GONE);
        }

        if (data != null && data.thumbnailUrl != null) {  //判断当前的文件是否为空
            Glide.with(context).load(data.thumbnailUrl).centerCrop().error(R.mipmap.mobai_wu).into(holder.simpleCover);

            File file = new File(data.imgUrl);
            if (isFileExit(pathListFile(FileUtils.photoMagicPath), file.getName())) {
                holder.simpleDownload.setVisibility(View.GONE);
            } else {
                holder.simpleDownload.setVisibility(View.VISIBLE);
            }
        } else {
            Glide.with(context).load(data.thumbnailUrl).centerCrop().error(R.mipmap.mobai_wu).into(holder.simpleCover);
            holder.simpleDownload.setVisibility(View.GONE);
        }

        return convertView;
    }

    public List<String> pathListFile(String filePath) {
        List<String> filePathUrl = new ArrayList<>();
        File[] files = new File(filePath).listFiles();
        for (int i = 0; i < files.length; i++) {
            filePathUrl.add(files[i].getName());
        }
        return filePathUrl;
    }

    public boolean isFileExit(List<String> fileList, String fileName) {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    class ViewHolder {  //加载viewHolder
        private ImageView simpleCoverBg;
        private ImageView simpleCover;   //整个item 的view
        private ImageView simpleDownload;  //下载按钮 根据本地文件是否显示
        private CircularProgressView progressRate;

        public ViewHolder(View convertView) {
            simpleCoverBg = (ImageView) convertView.findViewById(R.id.simple_cover_bg);
            simpleCover = (ImageView) convertView.findViewById(R.id.simple_cover);
            simpleDownload = (ImageView) convertView.findViewById(R.id.simple_download);
            progressRate = (CircularProgressView) convertView.findViewById(R.id.progressBar);
        }
    }

}
