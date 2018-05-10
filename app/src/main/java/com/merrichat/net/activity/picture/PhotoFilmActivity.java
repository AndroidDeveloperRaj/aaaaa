package com.merrichat.net.activity.picture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.video.complete.VideoReleaseActivity;
import com.merrichat.net.activity.video.editor.VideoMusicActivity;
import com.merrichat.net.adapter.PhotoFilmCutAdapter;
import com.merrichat.net.adapter.PhotoFilmPicsAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.PhotoFilmPicModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.FaceunityManger;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxImageTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.SpaceItemDecorations;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/10.
 */

public class PhotoFilmActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
    private static final int UPLOAD_SUC = 0x007;
    private static final int UPLOAD_Fail = 0x006;
    private static final int UPLOAD_PROGRESS = 0x005;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.iv_pictureShow)
    ImageView ivPictureShow;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.rl_pics_recyclerview)
    RecyclerView rlPicsRecyclerview;
    @BindView(R.id.rl_edit_recyclerview)
    RecyclerView rlEditRecyclerview;
    @BindView(R.id.bottom_linear)
    LinearLayout bottomLinear;
    @BindView(R.id.tv_photofilm_moban)
    TextView tvPhotofilmMoban;
    @BindView(R.id.ll_photofilmtab_moban)
    LinearLayout llPhotofilmtabMoban;
    @BindView(R.id.ll_photofilmtab_music)
    LinearLayout llPhotofilmtabMusic;
    @BindView(R.id.ll_photofilmtab_lvjing)
    LinearLayout llPhotofilmtabLvjing;
    @BindView(R.id.ll_photofilmtab_cut)
    LinearLayout llPhotofilmtabCut;
    @BindView(R.id.ll_photofilmtab_more)
    LinearLayout llPhotofilmtabMore;
    RelativeLayout llPhotofilmlvjingHeader;
    int checkPosition = 0;//剪切模块中 图片选中的位置
    private SpaceItemDecorations spaceItemDecoration;
    private PhotoFilmPicsAdapter photoFilmPicsAdapter;
    private ArrayList<PhotoFilmPicModel> picsList;
    private int clickType = 0;
    private PhotoFilmCutAdapter photoFilmCutAdapter;
    private ArrayList<PhotoFilmPicModel> cutList;//剪裁
    private ArrayList<PhotoFilmPicModel> filterList;//滤镜集合
    private ArrayList<String> photoList;//照片集合
    private String imgUrl;//正在编辑的图片
    private RelativeLayout llPhotofilmlvjingHeaderBenDi;
    private RelativeLayout llPhotofilmlvjingHeaderSouSuo;
    private List<PhotoFilmPicModel> musicModelList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private String videoPath;
    private String coverPath;
    private ArrayList<String> toVideoList;
    private int REQUEST_CODE_IMG_CHOOSE = 0x001;
    private Bitmap rotateBitmap;//旋转时得到的bitmap
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:
                    Toast.makeText(PhotoFilmActivity.this, "成功！", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("videoPath", videoPath);
                    bundle1.putString("cover", coverPath);
                    RxActivityTool.skipActivity(PhotoFilmActivity.this, VideoReleaseActivity.class, bundle1);
                    handled = true;
                    break;
                case UPLOAD_Fail:
                    Toast.makeText(PhotoFilmActivity.this, "失败！", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    handled = true;
                    break;
                case UPLOAD_PROGRESS:
                    handled = true;
                    break;
            }

            return handled;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photofilm);
        ButterKnife.bind(this);
        File appDir = new File(Environment.getExternalStorageDirectory(), "MerriChat");
        if (!appDir.exists()) {
            appDir.mkdir();
        } else {
            //FilterUtil.deleteDirWihtFile(appDir);
            //appDir.mkdir();
        }
        initView();
    }

    private void initView() {
        initTitle();
        cutList = new ArrayList<>();
        picsList = new ArrayList<>();
        filterList = new ArrayList<>();
        photoList = (ArrayList<String>) getIntent().getSerializableExtra("photoList");
        if (photoList != null && photoList.size() > 0) {
            Glide.with(this).load(photoList.get(0)).into(ivPictureShow);
            coverPath = photoList.get(0);
            toVideoList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < photoList.size(); j++) {
                    if (toVideoList.size() <= 4) {
                        toVideoList.add(photoList.get(j));
                    }
                }
            }
        }
        spaceItemDecoration = new SpaceItemDecorations(20, picsList.size());
        initRecyclerview();
        initDialog();
    }

    private void initRecyclerview() {
        if (photoList != null) {
            for (int i = 0; i < photoList.size(); i++) {
                PhotoFilmPicModel photoFilmPicModel = new PhotoFilmPicModel();
                photoFilmPicModel.setChecked(false);
                photoFilmPicModel.setImgUrl(photoList.get(i));
                picsList.add(photoFilmPicModel);
            }
        }
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_nature, "nature"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_delta, "delta"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_electric, "electric"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_slowlived, "slowlived"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_tokyo, "tokyo"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_warm, "warm"));
        initPicsRecyclerView(PhotoFilmPicModel.music_itemType);
        initCutRecyclerview();


    }

    /**
     * 剪裁
     */
    private void initCutRecyclerview() {
        cutList.add(new PhotoFilmPicModel(R.mipmap.jiancai_chexiao3x, "撤销"));
        cutList.add(new PhotoFilmPicModel(R.mipmap.jiancai_charu3x, "插入"));
        cutList.add(new PhotoFilmPicModel(R.mipmap.jiancai_xuanzhuan3x, "旋转"));
        cutList.add(new PhotoFilmPicModel(R.mipmap.jiancai_kaobei3x, "拷贝"));
        cutList.add(new PhotoFilmPicModel(R.mipmap.jiancai_shanchu3x, "删除"));
        rlEditRecyclerview.removeItemDecoration(spaceItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlEditRecyclerview.setLayoutManager(layoutManager);
        photoFilmCutAdapter = new PhotoFilmCutAdapter(R.layout.item_phtofilm_cut, cutList);
        rlEditRecyclerview.setAdapter(photoFilmCutAdapter);
        rlEditRecyclerview.addItemDecoration(spaceItemDecoration);
        photoFilmCutAdapter.setOnItemClickListener(this);
    }

    /**
     * 照片 “模板 滤镜 剪裁”
     *
     * @param itemType
     */
    private void initPicsRecyclerView(int itemType) {
        rlPicsRecyclerview.removeItemDecoration(spaceItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlPicsRecyclerview.setLayoutManager(layoutManager);
        photoFilmPicsAdapter = new PhotoFilmPicsAdapter(itemType, R.layout.item_phtofilm_pics, picsList);
        rlPicsRecyclerview.setAdapter(photoFilmPicsAdapter);
        if (itemType == PhotoFilmPicModel.lvJing_itemType) {
            photoFilmPicsAdapter.setNewData(filterList);
            photoFilmPicsAdapter.addHeaderView(getHeaderView(), -1, LinearLayout.HORIZONTAL);
            rlPicsRecyclerview.scrollToPosition(0);
        } else if (itemType == PhotoFilmPicModel.music_itemType) {
            photoFilmPicsAdapter.setNewData(musicModelList);
            photoFilmPicsAdapter.addHeaderView(getHeaderView(), -1, LinearLayout.HORIZONTAL);
            llPhotofilmlvjingHeaderBenDi.setVisibility(View.VISIBLE);
            llPhotofilmlvjingHeaderSouSuo.setVisibility(View.VISIBLE);
            rlPicsRecyclerview.scrollToPosition(0);
        } else {
            photoFilmPicsAdapter.removeAllHeaderView();

        }
        rlPicsRecyclerview.addItemDecoration(spaceItemDecoration);
        photoFilmPicsAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 滤镜 头布局
     *
     * @return
     */
    private View.OnClickListener headerViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("this is header!");
                llPhotofilmlvjingHeader.setSelected(true);
                llPhotofilmlvjingHeader.setBackground(getResources().getDrawable(R.drawable.photofilm_item_square_selected));
                for (PhotoFilmPicModel photoFilmPicModel :
                        filterList) {
                    photoFilmPicModel.setChecked(false);
                }
                photoFilmPicsAdapter.notifyDataSetChanged();


            }
        };
    }

    /**
     * 音乐 本地
     *
     * @return
     */
    private View.OnClickListener headerBenDiViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("this is 本地!");


            }
        };
    }

    /**
     * 音乐 搜索
     *
     * @return
     */
    private View.OnClickListener headerSouSuoViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("this is 搜索!");
                RxActivityTool.skipActivity(PhotoFilmActivity.this, VideoMusicActivity.class);

            }
        };
    }

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.item_phtofilm_lvjing, (ViewGroup) rlPicsRecyclerview.getParent(), false);
        llPhotofilmlvjingHeader = (RelativeLayout) view.findViewById(R.id.ll_photofilmlvjing_header);
        llPhotofilmlvjingHeaderBenDi = (RelativeLayout) view.findViewById(R.id.ll_photofilmlvjing_header_bendi);
        llPhotofilmlvjingHeaderSouSuo = (RelativeLayout) view.findViewById(R.id.ll_photofilmlvjing_header_sousuo);
        llPhotofilmlvjingHeader.setSelected(true);
        llPhotofilmlvjingHeader.setBackground(getResources().getDrawable(R.drawable.photofilm_item_square_selected));
        llPhotofilmlvjingHeader.setOnClickListener(headerViewOnClickListener());
        llPhotofilmlvjingHeaderBenDi.setOnClickListener(headerBenDiViewOnClickListener());
        llPhotofilmlvjingHeaderSouSuo.setOnClickListener(headerSouSuoViewOnClickListener());

        return view;
    }

    private void initTitle() {
        tvTitleText.setText("照片电影");
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");
        ivBack.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");
        setTabSelectedState(llPhotofilmtabMusic);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (clickType == PhotoFilmPicModel.jianCai_itemType) {
            imgUrl = picsList.get(position).getImgUrl();
            Glide.with(this).load(imgUrl).into(ivPictureShow);
            setSelectPicsStatu(position);
            rotateBitmap = BitmapFactory.decodeFile(imgUrl);
        }
        if (clickType == PhotoFilmPicModel.moBan_itemType) {
        } else if (clickType == PhotoFilmPicModel.lvJing_itemType) {
            if (RxDataTool.isNullString(imgUrl)) {
                if (!RxDataTool.isNullString(photoList.get(0))) {
                    imgUrl = photoList.get(0);
                } else {
                    RxToast.showToast("请选择图片");
                    return;
                }
            }
            llPhotofilmlvjingHeader.setSelected(false);
            llPhotofilmlvjingHeader.setBackgroundColor(Color.parseColor("#F3F3F3"));
            setSelectLvJingStatu(position);
            //设置图片滤镜
            String filterName = filterList.get(position).getCutName();
            FaceunityManger.loadItems(this, filterName);
            Bitmap src = BitmapFactory.decodeFile(imgUrl);
            FaceunityManger.renderItemsToBitmap(this, filterName, src, new FaceunityManger.Callback() {
                @Override
                public void invoke(final Bitmap bitmap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] bytes = baos.toByteArray();

                            Glide.with(PhotoFilmActivity.this).load(bytes).into(ivPictureShow);

//                            ivPictureShow.setImageBitmap(bitmap);
                            FaceunityManger.destroyItems();

//                            if (null != bitmap && !bitmap.isRecycled()) {
//                                bitmap.recycle();
//                                System.gc();
//                            }
                        }
                    });
                }
            });
        }
    }

    private void setSelectLvJingStatu(int position) {
        for (int i = 0; i < filterList.size(); i++) {
            PhotoFilmPicModel photoFilmPicModel = filterList.get(i);
            if (i == position) {
                photoFilmPicModel.setChecked(true);
            } else {
                photoFilmPicModel.setChecked(false);
            }
        }
        photoFilmPicsAdapter.notifyDataSetChanged();
        RxToast.showToast(position + "");
    }

    @OnClick({R.id.tv_back, R.id.tv_right, R.id.ll_photofilmtab_moban, R.id.ll_photofilmtab_music, R.id.ll_photofilmtab_lvjing, R.id.ll_photofilmtab_cut, R.id.ll_photofilmtab_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:

                videoPath = ConstantsPath.pic2videoPath + System.currentTimeMillis() + ".mp4";
                pic2Video();

                break;
            case R.id.ll_photofilmtab_moban:
                rlEditRecyclerview.setVisibility(View.GONE);
                clickType = PhotoFilmPicModel.moBan_itemType;
                setTabSelectedState(llPhotofilmtabMoban);
                initPicsRecyclerView(PhotoFilmPicModel.moBan_itemType);
                break;
            case R.id.ll_photofilmtab_music:
                rlEditRecyclerview.setVisibility(View.GONE);
                clickType = PhotoFilmPicModel.music_itemType;
                setTabSelectedState(llPhotofilmtabMusic);
                initPicsRecyclerView(PhotoFilmPicModel.music_itemType);
                break;
            case R.id.ll_photofilmtab_lvjing:
                rlEditRecyclerview.setVisibility(View.GONE);
                if (clickType != PhotoFilmPicModel.lvJing_itemType) {
                    clickType = PhotoFilmPicModel.lvJing_itemType;
                    initPicsRecyclerView(PhotoFilmPicModel.lvJing_itemType);
                }
                setTabSelectedState(llPhotofilmtabLvjing);
                break;
            case R.id.ll_photofilmtab_cut:
                rlEditRecyclerview.setVisibility(View.VISIBLE);
                clickType = PhotoFilmPicModel.jianCai_itemType;
                initPicsRecyclerView(PhotoFilmPicModel.jianCai_itemType);
                setTabSelectedState(llPhotofilmtabCut);
                break;
            case R.id.ll_photofilmtab_more:
                Bundle bundle = new Bundle();
                bundle.putSerializable("photoList", photoList);// 真实拍摄图片数据
                bundle.putSerializable("toVideoList", toVideoList);//假数据 用于合成视频
                RxActivityTool.skipActivity(this, PhotoFilmMoreActivity.class, bundle);
                break;
        }
    }

    private void setTabSelectedState(LinearLayout llView) {
        llPhotofilmtabMoban.setSelected(false);
        llPhotofilmtabMusic.setSelected(false);
        llPhotofilmtabLvjing.setSelected(false);
        llPhotofilmtabCut.setSelected(false);
        llView.setSelected(true);
    }

    private void initDialog() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在合成图片，请稍等...");
    }

    /**
     * 图片合成视频
     */
    private void pic2Video() {
        mProgressDialog.show();
        String cmd = executeCMD();
        EpEditor.execCmd(cmd, 2, new OnEditorListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(UPLOAD_SUC);
            }

            @Override
            public void onFailure() {
                handler.sendEmptyMessage(UPLOAD_Fail);
            }

            @Override
            public void onProgress(float v) {
                Message msg = new Message();
                msg.what = UPLOAD_PROGRESS;
                handler.sendMessage(msg);
            }
        });
    }

    private String executeCMD() {
        StringBuilder stringBuilder = new StringBuilder();
        /*for (int i = 0; i < photoList.size(); i++) {
            if (i == 0) {
                stringBuilder.append("-loop 1 -t 5 -i " + photoList.get(i) + " ");
            } else if (i == 1) {
                stringBuilder.append("-loop 1 -t 5 -i " + photoList.get(i) + " ");
            } else {
                stringBuilder.append("-loop 1 -t 9 -i " + photoList.get(i) + " ");

            }
        }*/
        /*stringBuilder.append("-r 5 ");
        for (int i = 0; i < photoList.size(); i++) {
            stringBuilder.append("-i " + photoList.get(i) + " ");
        }*/
        int[] imageWidthHeight = RxImageTool.getImageWidthHeight(photoList.get(0));
        if (photoList.size() == 4) {
            String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    "-filter_complex " +
                    "[0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=50:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v0];" +
//                "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=720x1280[v1];" +
                    "[1:v]fade=t=in:st=0:d=4,fade=t=out:st=4:d=1[v1];" +
//                "[2:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                    "[v1][2:v]overlay='max(W*(7-2*t),0)':(H-h)/2[v2];" +
                    "[v2][3:v]overlay='min(W*(2*t-14),0)':(H-h)/2[v3];" +
                    "[v0][v1][v3]concat=n=3:v=1:a=0,format=yuv420p[v] -map [v] " +
                    videoPath;
        /*String fourImage = "-loop 0 -t 3 -i " + picUrl + "img001.jpg " +
                "-loop 0 -t 3 -i " + picUrl + "img002.jpg " +
                "-loop 0 -t 3 -i " + picUrl + "img003.jpg " +
                "-loop 0 -t 3 -i " + picUrl + "img004.jpg " +
                "-filter_complex [0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=75[v0];" +
                "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=75,fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v1];" +
                "[2:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=75,fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                "[3:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=75,fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v3];" +
                "[v0][v1][v2][v3]concat=n=4:v=1:a=0,format=yuv420p[v]" +
                " -map [v] -preset ultrafast " +
                dstPath;*/
            Log.d("executeCMD: ", fourImage);
            return fourImage;
        } else if (photoList.size() == 5) {
            String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    "-filter_complex " +
                    "[0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v0];" +
//                "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=720x1280[v1];" +
                    "[1:v]fade=t=in:st=0:d=4,fade=t=out:st=4:d=1[v1];" +
                    "[2:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                    "[v1][3:v]overlay='max(W*(7-2*t),0)':(H-h)/2[v3];" +
                    "[v3][4:v]overlay='min(W*(2*t-14),0)':(H-h)/2[v4];" +
                    "[v0][v1][v2][v3][4]concat=n=5:v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast " +
                    videoPath;
            Log.d("executeCMD: ", fourImage);
            return fourImage;
        } else if (photoList.size() == 3) {
            String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    "-filter_complex " +
                    "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=50:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v0];" +
//                "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=720x1280[v1];" +
                    "[2:v]fade=t=in:st=0:d=4,fade=t=out:st=4:d=1[v1];" +
//                    "[2:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                    "[0:v][v1]overlay='max(W*(7-2*t),0)':(H-h)/2[v2];" +
//                    "[v2][3:v]overlay='min(W*(2*t-14),0)':(H-h)/2[v3];" +
                    "[v0][v1][v2]concat=n=3:v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast " +
                    videoPath;
            return fourImage;

        } else if (photoList.size() == 6) {
            for (int i = 0; i < photoList.size(); i++) {
                stringBuilder.append("-loop 1 -t 5 -i " + photoList.get(i) + " ");
            }
            StringBuilder builder = new StringBuilder();
            StringBuilder builder1 = new StringBuilder();
            for (int i = 0; i < photoList.size(); i++) {
                builder.append("[" + i + ":v]").append("fade=t=in:st=0:d=4,fade=t=out:st=4:d=50" + "[v" + i + "];");
                builder1.append("[v" + i + "]");
            }
//            builder.append("fade=t=in:st=0:d=20,fade=t=out:st=30:d=50 ");
            String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    "-filter_complex " + builder.toString() + builder1.toString() + "concat=n=" + photoList.size() + ":v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast " +
//                    "[0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=50:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v0];" +
//                    "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=50:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v1];" +
//                "[1:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=720x1280[v1];" +
//                    "[0:v]fade=t=in:st=0:d=4,fade=t=out:st=4:d=25[v0];" +
//                    "[1:v]fade=t=in:st=0:d=4,fade=t=out:st=4:d=25[v1];" +
//                    "[v0][v1]concat=n=2:v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast " +
                    videoPath;

            /*String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    builder.toString() +
                    "-pix_fmt yuv420p " +
                    videoPath;*/
            Log.d("executeCMD: " + photoList.size(), fourImage);
            return fourImage;
        } else {
            for (int i = 0; i < photoList.size(); i++) {
                if (i == 5 || i == 3||i==1) {
                    stringBuilder.append("-loop 1 -t 5 -i " + photoList.get(i) + " ");

                } else {
                    stringBuilder.append("-loop 0 -t 5 -i " + photoList.get(i) + " ");
                }
            }
            StringBuilder builder = new StringBuilder();
            StringBuilder builder1 = new StringBuilder();
            for (int i = 0; i < photoList.size(); i++) {
                if (i == 1) {
                    builder.append("[" + i + ":v]").append("fade=t=in:st=0:d=4,fade=t=out:st=4:d=50" + "[v" + i + "];");
                }
                else if (i == 3) {
                    builder.append("[" + (i - 1) + ":v]").append("[" + i + ":v]").append("overlay=x='if(gte(t,0),-w+(t)*100,10)':y=0" + "[v" + i + "];");
                }
                else if (i == 5) {
                        builder.append("[" + (i - 1) + ":v]").append("[" + i + ":v]").append("overlay='max(W*(7-2*t),0)':(H-h)/2" + "[v" + i + "];");
                    }
//                else if (i == 7) {
//                    builder.append("[v" + (i - 1) + "]").append("[" + i + ":v]").append("overlay='min(W*(2*t-14),0)':(H-h)/2" + "[v" + i + "];");
//                }
                else {
                    builder.append("[" + i + ":v]")
                            .append("zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=50:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v" + i + "];");
                }

                builder1.append("[v" + i + "]");
            }
            String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                    "-filter_complex " + builder.toString() + builder1.toString() + "concat=n=" + photoList.size() + ":v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast " +
                    videoPath;

            Log.d("executeCMD: " + photoList.size(), fourImage);
            return fourImage;
        }

    }

    /**
     * 剪裁 点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        boolean isCheckPic = false;
        for (int i = 0; i < picsList.size(); i++) {
            if (picsList.get(i).isChecked()) {
                checkPosition = i;
                isCheckPic = true;
            }

        }
        if (position == 1) {//插入
            if (!isCheckPic) {
                RxToast.showToast("请选择插入位置！");
                return;
            } else {
                Matisse.from(PhotoFilmActivity.this)
                        .choose(MimeType.ofImage(), false)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, MerriApp.getFileProviderName(this)))
                        .maxSelectable(9)
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.dp120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .theme(R.style.Matisse_MerriChat)

                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_IMG_CHOOSE);
            }

        } else if (position == 2) {//旋转
            if (!isCheckPic) {
                RxToast.showToast("请选择图片！");
                return;
            } else {

                rotateBitmap = RxImageTool.rotateImage(rotateBitmap, 90);
                ivPictureShow.setImageBitmap(rotateBitmap);
            }
        } else {
            if (position == 3) {//拷贝
                if (!isCheckPic) {
                    RxToast.showToast("请选择图片！");
                    return;
                } else {
                    PhotoFilmPicModel photoFilmPicModel = new PhotoFilmPicModel();
                    photoFilmPicModel.setChecked(false);
                    photoFilmPicModel.setImgUrl(photoList.get(checkPosition));
                    picsList.add(checkPosition + 1, photoFilmPicModel);
                    photoList.add(checkPosition + 1, photoList.get(checkPosition));
                }
            } else if (position == 4) {//删除
                if (!isCheckPic) {
                    RxToast.showToast("请选择图片！");
                    return;
                } else {
                    picsList.remove(checkPosition);
                    picsList.get(checkPosition).setChecked(true);
                    photoList.remove(checkPosition);
                }

            }
        }
        photoFilmPicsAdapter.notifyDataSetChanged();
        RxToast.showToast(cutList.get(position).getCutName());
    }

    private void setSelectPicsStatu(int position) {
        for (int i = 0; i < picsList.size(); i++) {
            PhotoFilmPicModel photoFilmPicModel = picsList.get(i);
            if (i == position) {
                photoFilmPicModel.setChecked(true);
            } else {
                photoFilmPicModel.setChecked(false);
            }
        }
        photoFilmPicsAdapter.notifyDataSetChanged();
        RxToast.showToast(position + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMG_CHOOSE && data != null) {
            if (resultCode == RESULT_OK) {
                List<String> result = Matisse.obtainPathResult(data);
                if (result != null && result.size() > 0) {
                    ArrayList<PhotoFilmPicModel> resultPhotoFilmPicModelList = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        PhotoFilmPicModel photoFilmPicModel = new PhotoFilmPicModel();
                        photoFilmPicModel.setChecked(false);
                        photoFilmPicModel.setImgUrl(result.get(i));
                        resultPhotoFilmPicModelList.add(photoFilmPicModel);
                    }
                    picsList.addAll(checkPosition + 1, resultPhotoFilmPicModelList);
                    photoList.addAll(checkPosition + 1, result);
                    photoFilmPicsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
