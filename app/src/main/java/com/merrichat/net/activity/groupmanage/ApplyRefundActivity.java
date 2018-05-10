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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.ScanAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.ApplyReturnModel;
import com.merrichat.net.model.CompanyData;
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
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by wangweiwei on 2018/1/22.
 * 申请退款
 */

public class ApplyRefundActivity extends MerriActionBarActivity {

    private LinearLayout mGallery;//选择证据布局
    private LayoutInflater mInflater;
    private RelativeLayout mSelectTrans;  //选择快递公司
    private RelativeLayout mTransOrder;  //选择扫描订单
    private static final String TAG = ApplyRefundActivity.class.getName();
    private ArrayList<String> imgUrlList = new ArrayList<>();

    private static final int iSelectCom = 0x10;  //跳转传递选择快递公司界面
    private static final int iScanNum = 0x11;   //扫描订单返回参数
    private static final int REQUEST_IMAGE = 2;

    private ImageView mSellContentUrl;   //商品图片
    private TextView mSellContentTitle;  //销售商品内容标题
    private TextView mSellContentDiscripe; //销售商品介绍

    private TextView mReceivingPrice;  //快递运费
    private TextView mGoodsCount;  //物品数量
    private TextView mRealPayPrice;  //实际支付价钱

    private EditText mApplyRefundReason; //退款原因
    private EditText mTransCo; //快递公司
    private EditText mTransOrderNu;  // 快递单号

    private List<String> mListPhotos;  //图片证据
    private TextView mSubmit; //提交按钮
    private CompanyData companyData = new CompanyData();  //快递公司的model
    private String orderRequestParams;

    private Gson gson = new Gson();
    private StringBuffer buffer;

    private WaiteGroupBuyDetailModel waiteGroupBuyDetailModel = new WaiteGroupBuyDetailModel();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_refund);
        mInflater = LayoutInflater.from(this);
        Intent intent = getIntent();
        orderRequestParams = intent.getStringExtra("queryOrderRequestParams");

        setLeftBack();
        setTitle("申请退款");
        initView();
    }


    private void initView() {
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);
        mSelectTrans = (RelativeLayout) findViewById(R.id.rl_select_trans);
        mTransOrder = (RelativeLayout) findViewById(R.id.rl_trans_order);

        mSellContentUrl = (ImageView) findViewById(R.id.iv_sell_content_url);
        mSellContentTitle = (TextView) findViewById(R.id.tv_sell_content_title);
        mSellContentDiscripe = (TextView) findViewById(R.id.tv_sell_content_discripe);

        mReceivingPrice = (TextView) findViewById(R.id.receiving_price);
        mGoodsCount = (TextView) findViewById(R.id.goods_count);
        mRealPayPrice = (TextView) findViewById(R.id.real_pay_price);

        mApplyRefundReason = (EditText) findViewById(R.id.et_apply_refund_reason);
        mTransCo = (EditText) findViewById(R.id.tv_trans_co);
        mTransOrderNu = (EditText) findViewById(R.id.tv_trans_order);


        mSubmit = (TextView) findViewById(R.id.tv_submit);

        initViewClick();

        loadImages(imgUrlList);
        getOrderData(orderRequestParams);

    }

    private void initViewClick() {

        mSelectTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(cnt, SelectExpressCompanyActivity.class);
                startActivityForResult(intent, iSelectCom);
            }
        });

        mTransOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(cnt, ScanAty.class);
                startActivityForResult(intent, iScanNum);
            }
        });


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                if (mApplyRefundReason.getText().toString().trim().length() == 0) {
                    Toast.makeText(cnt, "请输入退款原因～！", Toast.LENGTH_LONG).show();
                    return;
                }
                uploadGroupOfImg(imgUrlList);
            }
        });


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
            Glide.with(this).load(listImages.get(i)).centerCrop().error(R.mipmap.ic_preloading).override(200, 200).into(img);
            final int posion = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("----------->>>", posion + "");
                }
            });
            img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CommomDialog dialog = new CommomDialog(ApplyRefundActivity.this, R.style.dialog, "是否删除照片？", new CommomDialog.OnCloseListener() {
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
                    return false;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case iSelectCom:
                if (data == null) {
                    break;
                }
                companyData = (CompanyData) data.getSerializableExtra("expressInfoModel");
                mTransCo.setText(companyData.netName);
                break;
            case iScanNum:
                Log.e("------>>>", "扫描快递单号返回");
                if (data == null) {
                    break;
                }
                String code = data.getStringExtra("code");
                if (code != null && code.length() > 0) {
                    mTransOrderNu.setText(code);
                }
                break;

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

    private void setMultiImageSelector() {
        MultiImageSelector selector = MultiImageSelector.create(ApplyRefundActivity.this);
        selector.showCamera(true);
        selector.count(9);
        selector.multi();
        selector.origin(imgUrlList);
        selector.start(ApplyRefundActivity.this, REQUEST_IMAGE);

    }

    /**
     * 申请退款
     *
     * @param orderId        订单id
     * @param flag           1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     * @param orderStatus    订单状态
     * @param returnReason   退款原因
     * @param returnUrls     退货退款图片
     * @param netId          快递公司id
     * @param netName        快递公司名字
     * @param expressCode    快递公司简码zto，yunda
     * @param expressLogo    快递公司logo链接
     * @param waybillNumber  运单号
     * @param arbitrateUrls  申请仲裁的图片
     * @param arbitrateCause 申请仲裁的原因
     */
    private void applyReturn(String orderId, String flag, String orderStatus, final String returnReason,
                             String returnUrls, String netId, String netName, String expressCode, String expressLogo,
                             String waybillNumber, String arbitrateUrls, String arbitrateCause) {
        OrderJsonModel orderJson = new OrderJsonModel();
        orderJson.orderStatus = orderStatus;
        orderJson.returnReason = returnReason;
        orderJson.returnUrls = returnUrls;
        orderJson.netId = netId;
        orderJson.netName = netName;
        orderJson.expressCode = expressCode;
        orderJson.expressLogo = expressLogo;
        orderJson.waybillNumber = waybillNumber;
        orderJson.arbitrateUrls = arbitrateUrls;
        orderJson.arbitrateCause = arbitrateCause;

        ApiManager.getApiManager().getService(WebApiService.class).applyReturn(orderId,
                flag, new Gson().toJson(orderJson))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<ApplyReturnModel>() {
                    @Override
                    public void onNext(ApplyReturnModel returnModel) {
                        if (returnModel.success) {
                            Toast.makeText(cnt, "申请退款成功", Toast.LENGTH_LONG).show();
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
     * 根据  orderId 加载订单详情的具体信息
     *
     * @param orderRequestParams
     */
    public void getOrderData(String orderRequestParams) {
        final Gson gson = new Gson();
        QueryOrderModel.QueryOrderRequestParams requestParams = gson.fromJson(orderRequestParams, QueryOrderModel.QueryOrderRequestParams.class);
        if (null == requestParams.orderId || requestParams.orderId.length() == 0) {
            return;
        }
        requestParams.memberId = UserModel.getUserModel().getMemberId();
        ApiManager.getApiManager().getService(WebApiService.class).queryOrderDetail(gson.toJson(requestParams))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<WaiteGroupBuyDetailModel>() {
                    @Override
                    public void onNext(WaiteGroupBuyDetailModel buyDetailModel) {
                        if (buyDetailModel.success) {
                            waiteGroupBuyDetailModel.data = buyDetailModel.data;
                            setApplyRefund(buyDetailModel.data);
                        } else {
                            Toast.makeText(cnt, buyDetailModel.message, Toast.LENGTH_LONG).show();
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
     * 加载页面的订单详情
     *
     * @param data 数据包
     */
    private void setApplyRefund(WaiteGroupBuyDetailModel.Data data) {
        if (data == null) {
            return;
        }
        Glide.with(this).load(data.orderItemList.get(0).img).centerCrop().override(200, 200).into(mSellContentUrl);
        mSellContentTitle.setText(data.orderItemList.get(0).productName);
        mSellContentDiscripe.setText(data.orderItemList.get(0).productPropertySpecValue);
        mReceivingPrice.setText("￥" + data.deliveryFee);
        mGoodsCount.setText("" + data.orderItemList.get(0).productNum);
        mRealPayPrice.setText(data.totalAmount + "");

    }


    /**
     * 上传图片综合接口
     *
     * @param groupImageUrls 图片地址列表
     */
    public void uploadGroupOfImg(final ArrayList<String> groupImageUrls) {
        Log.e("--->>","uploadGroupOfImg");
        if (TextUtils.isEmpty(mApplyRefundReason.getText().toString())) {
            Toast.makeText(cnt, "请输入退款原因～！", Toast.LENGTH_LONG).show();
            return;
        }

        if (groupImageUrls.size() == 0) {
            if (null == companyData.netName || null == companyData.code ) {
                applyReturn(waiteGroupBuyDetailModel.data.orderId, //订单id
                        "1", // 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
                        "4", // 订单状态
                        mApplyRefundReason.getText().toString(), // 退款原因
                       "", //  图片
                        "",// 快递公司id
                        "", //  快递公司名称
                        "", //  快递公司简称
                        "",//  快递公司logo
                        mTransOrderNu.getText().toString(),//  快递单号
                        "",//
                        "");
            } else {
                applyReturn(waiteGroupBuyDetailModel.data.orderId, //订单id
                        "1", // 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
                        "4", // 订单状态
                        mApplyRefundReason.getText().toString(), // 退款原因
                        "", //  图片
                        companyData.netId + "",// 快递公司id
                        companyData.netName, //  快递公司名称
                        companyData.code, //  快递公司简称
                        companyData.url,//  快递公司logo
                        mTransOrderNu.getText().toString() + "",//  快递单号
                        "",//
                        "");
            }

        } else {
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
                                if (null == companyData) {
                                    applyReturn(waiteGroupBuyDetailModel.data.orderId, //订单id
                                            "1", // 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
                                            "4", // 订单状态
                                            mApplyRefundReason.getText().toString(), // 退款原因
                                            uploadModel.data.toString(), //  图片
                                            "",// 快递公司id
                                            "", //  快递公司名称
                                            "", //  快递公司简称
                                            "",//  快递公司logo
                                            mTransOrderNu.getText().toString(),//  快递单号
                                            "",//
                                            ""
                                    );
                                } else {
                                    applyReturn(waiteGroupBuyDetailModel.data.orderId, //订单id
                                            "1", // 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
                                            "4", // 订单状态
                                            mApplyRefundReason.getText().toString(), // 退款原因
                                            uploadModel.data.toString(), //  图片
                                            companyData.netId + "",// 快递公司id
                                            companyData.netName, //  快递公司名称
                                            companyData.code, //  快递公司简称
                                            companyData.url,//  快递公司logo
                                            mTransOrderNu.getText().toString() + "",//  快递单号
                                            "",//
                                            ""
                                    );
                                }

                            } else {
                                return;
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

}
