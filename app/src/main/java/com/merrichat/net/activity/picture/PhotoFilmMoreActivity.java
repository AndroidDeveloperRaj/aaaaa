package com.merrichat.net.activity.picture;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.picture.operate.ImageObject;
import com.merrichat.net.activity.picture.operate.OperateUtils;
import com.merrichat.net.activity.picture.operate.OperateView;
import com.merrichat.net.activity.picture.operate.TextObject;
import com.merrichat.net.activity.video.complete.VideoReleaseActivity;
import com.merrichat.net.adapter.PhotoFilmMorePicsAdapter;
import com.merrichat.net.adapter.PhotoFilmMoveAdapter;
import com.merrichat.net.model.PhotoFilmPicModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxImageTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.SpaceItemDecorations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/13.
 * <p>
 * 图片编辑--更多
 */

public class PhotoFilmMoreActivity extends BaseActivity {
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
    @BindView(R.id.bottom_linear)
    LinearLayout bottomLinear;
    /**
     * 封面图片
     */
    @BindView(R.id.iv_photofilmmore_pic)
    ImageView ivPhotofilmmorePic;
    /**
     * 封面 布局
     */
    @BindView(R.id.rl_photofilm_pic)
    RelativeLayout rlPhotofilmPic;

    private SpaceItemDecorations spaceItemDecoration;
    private ArrayList<PhotoFilmPicModel> cutList;
    private ArrayList<PhotoFilmPicModel> picsList;
    private PhotoFilmMorePicsAdapter photoFilmMorePicsAdapter;
    private String coverImgUrl = "";//选中编辑图片的路径
    private int itemType = 0;
    private PhotoFilmMoveAdapter photoFilmMoveAdapter;
    private ArrayList<PhotoFilmPicModel> moveList;
    private OperateUtils operateUtils;
    private OperateView operateView;
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mainLayout.getWidth() != 0) {
                    Log.i("LinearLayoutW", mainLayout.getWidth() + "");
                    Log.i("LinearLayoutH", mainLayout.getHeight() + "");
                    // 取消定时器
                    fillContent();
                }
            }
        }
    };
    private List<PhotoFilmPicModel> watermarkList = new ArrayList<>();//贴纸
    private List<String> photoList;
    private String videoPath;
    private ProgressDialog mProgressDialog;
    private List<String> toVideoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photofilm_more);
        ButterKnife.bind(this);
        initData();
        initView();
        initDialog();
        operateUtils = new OperateUtils(this);

    }

    private void initData() {
        watermarkList.add(new PhotoFilmPicModel(R.drawable.watermark_chunvzuo));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.comment));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.gouda));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.guaishushu));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.haoxingzuop));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.wanhuaile));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.xiangsi));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.xingzuokong));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.xinnian));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.zaoan));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.zuile));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.zuo));
        watermarkList.add(new PhotoFilmPicModel(R.drawable.zui));
        photoList = (List<String>) getIntent().getSerializableExtra("photoList");
        toVideoList = (List<String>) getIntent().getSerializableExtra("toVideoList");
        if (photoList != null && photoList.size() > 0) {
            Glide.with(this).load(photoList.get(0)).into(ivPictureShow);
            coverImgUrl = photoList.get(0);
//            myHandler.sendEmptyMessageDelayed(1, 10);
        }

    }

    private void fillContent() {
        if (RxDataTool.isNullString(coverImgUrl)) {
            RxToast.showToast("请选择图片！");
            return;
        }
        mainLayout.removeAllViews();
        Bitmap resizeBmp = BitmapFactory.decodeFile(coverImgUrl);
        Bitmap bitmap = RxImageTool.clip(resizeBmp, 60, 120, 567, 567);
        operateView = new OperateView(PhotoFilmMoreActivity.this, bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                bitmap.getWidth(), bitmap.getHeight());
        operateView.setLayoutParams(layoutParams);
        mainLayout.addView(operateView);
        operateView.setMultiAdd(true); // 设置此参数，可以添加多个图片

    }

    private void addpic(int position) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), position);
        // ImageObject imgObject = operateUtils.getImageObject(bmp);
        ImageObject imgObject = operateUtils.getImageObject(bmp, operateView,
                5, 150, 100);
        operateView.addItem(imgObject);
    }

    private void initView() {
        initTitle();
        cutList = new ArrayList<>();
        moveList = new ArrayList<>();
        picsList = new ArrayList<>();
        spaceItemDecoration = new SpaceItemDecorations(20, picsList.size());
        initRecyclerview();
    }

    private void initRecyclerview() {

//        for (int i = 0; i < 10; i++) {
//            picsList.add(new PhotoFilmPicModel(2, "无", "http://pic23.photophoto.cn/20120530/0020033092420808_b.jpg", false));
//            picsList.add(new PhotoFilmPicModel(2, "美白", "http://img1.3lian.com/2015/a1/84/d/95.jpg", false));
//            picsList.add(new PhotoFilmPicModel(2, "可爱", "http://pic7.nipic.com/20100520/559805_115200039131_2.jpg", false));
//        }
        for (int i = 0; i < photoList.size(); i++) {
            String imgUrl = photoList.get(i);
            PhotoFilmPicModel photoFilmPicModel = new PhotoFilmPicModel();
            photoFilmPicModel.setImgUrl(imgUrl);
            picsList.add(photoFilmPicModel);
        }
        moveList.add(new PhotoFilmPicModel(R.mipmap.jiancai_chexiao3x, "无"));
        moveList.add(new PhotoFilmPicModel("淡入淡出"));
        moveList.add(new PhotoFilmPicModel("左淡入"));
        moveList.add(new PhotoFilmPicModel("右淡入"));
        cutList.addAll(picsList);
       /* initPicsRecyclerView(PhotoFilmPicModel.moBan_itemType);
        initMoveRecyclerview(PhotoFilmPicModel.tiezhi_itemType);
*/

    }

    /**
     * 动作
     *
     * @param itemType
     */
    private void initMoveRecyclerview(final int itemType) {
        rlEditRecyclerview.setVisibility(View.VISIBLE);
        rlEditRecyclerview.removeItemDecoration(spaceItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlEditRecyclerview.setLayoutManager(layoutManager);
        photoFilmMoveAdapter = new PhotoFilmMoveAdapter(itemType, R.layout.item_phtofilm_pics, cutList);
        if (itemType == PhotoFilmPicModel.move_itemType) {//动作
            photoFilmMoveAdapter = new PhotoFilmMoveAdapter(itemType, R.layout.item_phtofilm_cut, cutList);

        } else if (itemType == PhotoFilmPicModel.tiezhi_itemType) {//贴纸
            photoFilmMoveAdapter.setNewData(watermarkList);
        }
        rlEditRecyclerview.setAdapter(photoFilmMoveAdapter);
        rlEditRecyclerview.addItemDecoration(spaceItemDecoration);
        photoFilmMoveAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (itemType == PhotoFilmPicModel.moBan_itemType) {
                }
                if (itemType == PhotoFilmPicModel.move_itemType) {
                    setSelectMoveStatu(position);
                } else if (itemType == PhotoFilmPicModel.tiezhi_itemType) {//添加贴纸
                    // 延迟每次延迟10 毫秒 隔1秒执行一次
                    addpic(watermarkList.get(position).getDrawId());
                } else if (itemType == PhotoFilmPicModel.zimu_itemType) {
                    addfont();
                }


            }
        });
    }

    private void addfont() {
        final EditText editText = new EditText(PhotoFilmMoreActivity.this);
        new AlertDialog.Builder(PhotoFilmMoreActivity.this).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        TextObject textObj = operateUtils.getTextObject(string,
                                operateView, 5, 150, 100);
                        if (textObj != null) {
                            operateView.addItem(textObj);
                            operateView.setOnListener(new OperateView.MyListener() {
                                public void onClick(TextObject tObject) {
                                    alert(tObject);
                                }
                            });
                        }
                    }
                }).show();
    }

    private void alert(final TextObject tObject) {

        final EditText editText = new EditText(PhotoFilmMoreActivity.this);
        new AlertDialog.Builder(PhotoFilmMoreActivity.this).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        tObject.setText(string);
                        tObject.setColor(R.color.colorAccent);
                        tObject.commit();
                    }
                }).show();
    }

    /**
     * 照片 “模板 滤镜 剪裁”
     *
     * @param itemType
     */
    private void initPicsRecyclerView(final int itemType) {
        rlPicsRecyclerview.setVisibility(View.VISIBLE);
        rlPicsRecyclerview.removeItemDecoration(spaceItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlPicsRecyclerview.setLayoutManager(layoutManager);
        photoFilmMorePicsAdapter = new PhotoFilmMorePicsAdapter(itemType, R.layout.item_phtofilm_pics, picsList);
        rlPicsRecyclerview.setAdapter(photoFilmMorePicsAdapter);
        rlPicsRecyclerview.addItemDecoration(spaceItemDecoration);
        photoFilmMorePicsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                coverImgUrl = picsList.get(position).getImgUrl();

                if (itemType == PhotoFilmPicModel.cover_itemType) {
                    rlPhotofilmPic.setVisibility(View.VISIBLE);
//                    Glide.with(PhotoFilmMoreActivity.this).load(coverImgUrl).into(ivPictureShow);
                    Glide.with(PhotoFilmMoreActivity.this).load(coverImgUrl).into(ivPhotofilmmorePic);

                }
                    Glide.with(PhotoFilmMoreActivity.this).load(coverImgUrl).into(ivPictureShow);

//                myHandler.sendEmptyMessageDelayed(1, 10);
                setSelectPicsStatu(position);

            }
        });
    }

    private void setSelectMoveStatu(int position) {
        for (int i = 0; i < cutList.size(); i++) {
            PhotoFilmPicModel photoFilmPicModel = cutList.get(i);
            if (i == position) {
                photoFilmPicModel.setChecked(true);
            } else {
                photoFilmPicModel.setChecked(false);
            }
        }
        photoFilmMoveAdapter.notifyDataSetChanged();
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
        photoFilmMorePicsAdapter.notifyDataSetChanged();
        RxToast.showToast(position + "");
    }

    private void initTitle() {
        tvTitleText.setText("更多");
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");
        ivBack.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");
        setTabSelectedState(llPhotofilmtabMoban);
    }

    private void setTabSelectedState(LinearLayout llView) {
        llPhotofilmtabMoban.setSelected(false);
        llPhotofilmtabLvjing.setSelected(false);
        llPhotofilmtabCut.setSelected(false);
        llPhotofilmtabMusic.setSelected(false);
        llPhotofilmtabMore.setSelected(false);
        llView.setSelected(true);
    }

    @OnClick({R.id.tv_back, R.id.tv_right, R.id.ll_photofilmtab_moban, R.id.ll_photofilmtab_music, R.id.ll_photofilmtab_lvjing, R.id.ll_photofilmtab_cut, R.id.ll_photofilmtab_more, R.id.rl_photofilm_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
//                btnSave();
                videoPath = ConstantsPath.pic2videoPath + System.currentTimeMillis() + ".mp4";
                pic2Video();

                break;
            case R.id.ll_photofilmtab_moban://贴纸
                cutList.clear();
                cutList.addAll(picsList);
                if (itemType != PhotoFilmPicModel.tiezhi_itemType) {
                    itemType = PhotoFilmPicModel.tiezhi_itemType;
                    initMoveRecyclerview(itemType);
                }
                setTabSelectedState(llPhotofilmtabMoban);
                break;
            case R.id.ll_photofilmtab_music://字幕
                if (itemType != PhotoFilmPicModel.zimu_itemType) {
                    itemType = PhotoFilmPicModel.zimu_itemType;
                    initMoveRecyclerview(itemType);
                }
                setTabSelectedState(llPhotofilmtabMusic);

                break;
            case R.id.ll_photofilmtab_lvjing://音量
                rlEditRecyclerview.setVisibility(View.GONE);
                rlPicsRecyclerview.setVisibility(View.GONE);
                if (itemType != PhotoFilmPicModel.volume_itemType) {
                    itemType = PhotoFilmPicModel.volume_itemType;
//                    initMoveRecyclerview(itemType);
                }
                setTabSelectedState(llPhotofilmtabLvjing);

                break;
            case R.id.ll_photofilmtab_cut://动作
                cutList.clear();
                cutList.addAll(moveList);
                if (itemType != PhotoFilmPicModel.move_itemType) {
                    itemType = PhotoFilmPicModel.move_itemType;
                    initMoveRecyclerview(itemType);
                    initPicsRecyclerView(itemType);
                }
                setTabSelectedState(llPhotofilmtabCut);

                break;
            case R.id.ll_photofilmtab_more://封面
                itemType = PhotoFilmPicModel.cover_itemType;
                rlEditRecyclerview.setVisibility(View.GONE);
                initPicsRecyclerView(itemType);
                setTabSelectedState(llPhotofilmtabMore);
                break;
            case R.id.rl_photofilm_pic://封面图片
                Glide.with(PhotoFilmMoreActivity.this).load(coverImgUrl).into(ivPictureShow);
                break;
        }
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
                Toast.makeText(PhotoFilmMoreActivity.this, "成功！", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                Bundle bundle1 = new Bundle();
                bundle1.putString("videoPath", videoPath);
                bundle1.putString("cover", coverImgUrl);
                RxActivityTool.skipActivity(PhotoFilmMoreActivity.this, VideoReleaseActivity.class, bundle1);

            }

            @Override
            public void onFailure() {
                Toast.makeText(PhotoFilmMoreActivity.this, "失败！", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }

            @Override
            public void onProgress(float v) {
                mProgressDialog.setProgress((int) (v * 100));
            }
        });
    }

    private String executeCMD() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < toVideoList.size(); i++) {
            if (i == 0) {
                stringBuilder.append("-loop 0 -t 9 -i " + toVideoList.get(i) + " ");
            } else if (i == 1) {
                stringBuilder.append("-loop 1 -t 3 -i " + toVideoList.get(i) + " ");
            } else {
                stringBuilder.append("-loop 1 -t 9 -i " + toVideoList.get(i) + " ");

            }
        }
        int[] imageWidthHeight = RxImageTool.getImageWidthHeight(toVideoList.get(0));
        String fourImage = stringBuilder.toString() + "-c:v libx264 " +
                "-filter_complex " +
                "[0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125:s=" + imageWidthHeight[0] + "x" + imageWidthHeight[1] + "[v0];" +
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
    }

    private void initDialog() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在合成图片,请稍等...");
    }

    private void btnSave() {
        operateView.save();
        Bitmap bmp = getBitmapByView(operateView);
        if (bmp != null) {
            String mPath = saveBitmap(bmp, "saveTemp");
            Intent okData = new Intent();
            okData.putExtra("camera_path", mPath);
            setResult(RESULT_OK, okData);
            RxToast.showToast("保存成功！");
        }
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    // 将生成的图片保存到内存中
    public String saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(ConstantsPath.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(ConstantsPath.filePath + name + ".jpg");
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
