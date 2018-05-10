package com.merrichat.net.activity.groupmanage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.ApplyReturnModel;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.OrderJsonModel;
import com.merrichat.net.model.QueryOrderModel;
import com.merrichat.net.model.UploadModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WaiteGroupBuyDetailModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CommomDialog;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wangweiwei on 2018/1/22.
 * <p>
 * 仲裁操作
 */

public class ApplyArbitramentActivity extends MerriActionBarActivity {
    private LinearLayout mGallery;
    private LayoutInflater mInflater;

    private WaiteGroupBuyDetailModel mDetailModel;
    private String orderId;


    private ImageView mSellContentUrl;  //商品的URL 图片
    private TextView mSellContentTitle;//商品名称
    private TextView mSellContentDiscripe;  //商品描述

    private TextView mReceivingPrice;  //商品价钱
    private TextView mGoodsCount;   //善品数量
    private TextView mRealPayPrice; //商品总付款金额

    private EditText mApplyRefundReason;   //仲裁原因

    private TextView mSubmit;  //提交按钮

    private Gson gson = new Gson();

    private ArrayList<String> imgUrlList = new ArrayList<>();
    private StringBuffer buffer;
    private static final int REQUEST_IMAGE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_arbitrament);
        mInflater = LayoutInflater.from(this);
        setLeftBack();
        setTitle("申请仲裁");
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        initView();
        getOrderData(orderId);


    }

    private void initView() {
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);  //退款图片列表

        mSellContentUrl = (ImageView) findViewById(R.id.iv_sell_content_url);
        mSellContentTitle = (TextView) findViewById(R.id.tv_sell_content_title);
        mSellContentDiscripe = (TextView) findViewById(R.id.tv_sell_content_discripe);

        mReceivingPrice = (TextView) findViewById(R.id.receiving_price);
        mGoodsCount = (TextView) findViewById(R.id.goods_count);
        mRealPayPrice = (TextView) findViewById(R.id.real_pay_price);

        mApplyRefundReason = (EditText) findViewById(R.id.et_apply_refund_reason);

        mSubmit = (TextView) findViewById(R.id.tv_submit);

        initClickView();
        loadImages(imgUrlList);
    }

    private void initClickView() {
        for (int i = 0; i < 6; i++) {
            View view = mInflater.inflate(R.layout.item_image, mGallery, false);
            ImageView img = (ImageView) view.findViewById(R.id.item_img);
            final int posion = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("----------->>>", posion + "");
                }
            });
            mGallery.addView(view);
        }

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadGroupOfImg(imgUrlList, orderId, mApplyRefundReason.getText().toString());

            }
        });
    }


    private void initViewData(WaiteGroupBuyDetailModel detailModel) {
        Glide.with(this).load(detailModel.data.orderItemList.get(0).img).centerCrop().override(200, 200).into(mSellContentUrl);
        mSellContentTitle.setText("" + detailModel.data.orderItemList.get(0).productName);
        mSellContentDiscripe.setText("" + detailModel.data.orderItemList.get(0).productPropertySpecValue);

        mReceivingPrice.setText("￥" + detailModel.data.deliveryFee);
        mGoodsCount.setText("" + detailModel.data.orderItemList.get(0).productNum);
        mRealPayPrice.setText(detailModel.data.totalAmount + "");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (data == null) {
                    break;
                }
                ArrayList<String> resultStringList = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                if (resultStringList != null) {
                    imgUrlList.clear();
                    imgUrlList.addAll(resultStringList);
                    loadImages(imgUrlList);
                }
                break;
        }
    }

    /**
     * 根据  orderId 加载订单详情的具体信息
     *
     * @param orderId
     */
    public void getOrderData(String orderId) {
        if (null == orderId || orderId.length() == 0) {
            Toast.makeText(ApplyArbitramentActivity.this, "订单ID为空，检查一下吧～！", Toast.LENGTH_LONG).show();
            return;
        }
        QueryOrderModel.QueryOrderRequestParams requestParams = new QueryOrderModel.QueryOrderRequestParams();
        requestParams.orderId = orderId;
        requestParams.orderStatus = 7+"";

        requestParams.memberId = UserModel.getUserModel().getMemberId();
        final Gson gson = new Gson();

        ApiManager.getApiManager().getService(WebApiService.class).queryOrderDetail(gson.toJson(requestParams))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<WaiteGroupBuyDetailModel>() {
                    @Override
                    public void onNext(WaiteGroupBuyDetailModel buyDetailModel) {
                        if (buyDetailModel.success) {
                            mDetailModel = buyDetailModel;
                            initViewData(mDetailModel);
                        } else {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(ApplyArbitramentActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 申请退款
     *
     * @param orderId        订单id
     * @param arbitrateUrls  申请仲裁的图片
     * @param arbitrateCause 申请仲裁的原因
     */
    private void applyReturn(final String orderId, final String arbitrateUrls, final String arbitrateCause) {
        OrderJsonModel arbitrateInfo =new OrderJsonModel();
        arbitrateInfo.arbitrateUrls = arbitrateUrls;
        arbitrateInfo.arbitrateCause = arbitrateCause;

        ApiManager.getApiManager().getService(WebApiService.class).applyReturn2(orderId,
                "4", // 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
                gson.toJson(arbitrateInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<ApplyReturnModel>() {
                    @Override
                    public void onNext(ApplyReturnModel returnModel) {
                        if (returnModel.success) {
                            Toast.makeText(cnt, "申请仲裁成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("result", -1); //将计算的值回传回去
                            setResult(2, intent);
                            finish();
                        } else {
                            Toast.makeText(cnt, returnModel.message, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });


    }


    /**
     * 上传图片综合接口
     *
     * @param groupImageUrls 图片地址列表
     * @param orderId        订单id
     * @param arbitrateCause
     */
    public void uploadGroupOfImg(final ArrayList<String> groupImageUrls, final String orderId, final String arbitrateCause) {

        if (TextUtils.isEmpty(orderId)) {
            Toast.makeText(cnt, "订单不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(arbitrateCause)) {
            Toast.makeText(cnt, "仲裁原因不能为空", Toast.LENGTH_LONG).show();
            return;
        }


        if (groupImageUrls.size() == 0) {
            applyReturn(orderId, "", arbitrateCause);
            return;
        }else {
            String descriptionString = "This is a description";
            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("fileName", RequestBody.create(null, "returnGoods"));
            map.put("memberId", RequestBody.create(null, UserModel.getUserModel().getMemberId()));

            final LoadingDialog dialog = new LoadingDialog(this);
            dialog.setLoadingText("上传图片中...");
            dialog.show();

            ApiManager.getApiManager().getService(WebApiService.class).upload(description, MerriUtils.filesToMultipartBodyParts(groupImageUrls), map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscribe<UploadModel>() {
                        @Override
                        public void onNext(UploadModel uploadModel) {
                            if (uploadModel.success) {
                                applyReturn(orderId, uploadModel.data, arbitrateCause);
                            } else {
                                Toast.makeText(cnt, "上传图片异常，检查一下吧～！", Toast.LENGTH_LONG).show();
                            }
                            dialog.close();

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.close();
                            e.printStackTrace();
                            Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                            return;
                        }
                    });
        }



    }


    /**
     * 处理图片 listImages 图片列表
     *
     * @param listImages
     */
    private void loadImages(ArrayList<String> listImages) {
        mGallery.removeAllViews();
        buffer = new StringBuffer();
        for (int i = 0; i < listImages.size(); i++) {  //下方选择照片
            File file = new File(listImages.get(i));
            try {
                if (MerriUtils.FormetFileSize(MerriUtils.getFileSize(file)) > 5) {
                    Toast.makeText(cnt, "您有图片过大" + file.getName() + "，请处理后再上传", Toast.LENGTH_LONG).show();
                    mGallery.removeAllViews();
                    imgUrlList.clear();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < listImages.size(); i++) {  //下方选择照片
            View view = mInflater.inflate(R.layout.item_image, mGallery, false);
            SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.item_img);
            Glide.with(this)
                    .load(listImages.get(i))
                    .override(200, 200) // resizes the image to these dimensions (in pixel)
                    .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                    .error(R.mipmap.ic_preloading).into(img);


            final int posion = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CommomDialog dialog = new CommomDialog(ApplyArbitramentActivity.this, R.style.dialog, "是否删除摘片？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                imgUrlList.remove(posion);
                                loadImages(imgUrlList);
                            }
                        }
                    }).setTitle("删除照片");
                    dialog.show();
                }
            });
            mGallery.addView(view);
        }
        if (listImages.size() < 9) {
            View view = mInflater.inflate(R.layout.item_image, mGallery, false);
            ImageView img = (ImageView) view.findViewById(R.id.item_img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMultiImageSelector();
                }
            });
            mGallery.addView(view);
        }
    }

    private void setMultiImageSelector() {
        MultiImageSelector selector = MultiImageSelector.create(ApplyArbitramentActivity.this);
        selector.showCamera(true);
        selector.count(9);
        selector.multi();
        selector.origin(imgUrlList);
        selector.start(ApplyArbitramentActivity.this, ApplyArbitramentActivity.REQUEST_IMAGE);

    }


}