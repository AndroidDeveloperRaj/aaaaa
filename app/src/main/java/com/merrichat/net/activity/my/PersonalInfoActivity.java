package com.merrichat.net.activity.my;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.contact.UpdateGroupNameActivity;
import com.merrichat.net.adapter.MyJingLiAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.JsonBean;
import com.merrichat.net.model.PersonalInfoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.ACache;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.GetJsonDataUtil;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxKeyboardTool;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ConfirmDialog;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/6.
 * <p>
 * 我的主页——编辑个人资料
 */

public class PersonalInfoActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {
    public final static int JIAOYU_TYPE = 0x0002;
    public final static int GONGZUO_TYPE = 0x0003;
    public static final int HEADER_WIDTH = 184;//头像的宽
    public static final int HEADER_HEIGHT = 184;//头像的高
    public static final int MAX_PIC_SIZE = 50 * 1024;//上传图片的最大值
    public static final int MSG_LOAD_DATA = 0x0001;
    private static final String TAG = PersonalInfoActivity.class.getName();
    private static final int RESULTCODE_NICK_NAME = 0;
    private static final int RESULTCODE_SIGNATURE = 1;
    private final static int activityId = MiscUtil.getActivityId();
    private final int XUEXING_POPUP = 2;
    private final int SHENGAO_POPUP = 3;
    private final int TIZHONG_POPUP = 4;
    private final int ZONGJIAO_POPUP = 5;
    private final int XUELI_POPUP = 6;
    private final int ZHIYE_POPUP = 7;
    private final int GANQING_POPUP = 8;
    private final int EPX_REQUEST_CODE = 0x0004;
    private final int JUZHUDI_TYPE = 0x0008;
    private final int GUXIANG_TYPE = 0x0009;
    private final int NICKNAME_REQUESTCODE = 0x0010;
    private final int JIANJIE_REQUESTCODE = 0x0011;
    private final int BACK_TYPE = 0x0006;//返回
    private final int SAVE_TYPE = 0x0007;//完成
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_my_header)
    SimpleDraweeView ivMyHeader;
    @BindView(R.id.perfection_header)
    ImageView perfectionHeader;
    @BindView(R.id.rl_my_header)
    RelativeLayout rlMyHeader;
    @BindView(R.id.tvs)
    TextView tvs;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.iv_row_nick)
    ImageView ivRowNick;
    @BindView(R.id.rl_nick_name)
    RelativeLayout rlNickName;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.perfection_sex)
    ImageView perfectionSex;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tvsd)
    TextView tvsd;
    @BindView(R.id.tv_sheng_ri)
    TextView tvShengRi;
    @BindView(R.id.rl_sheng_ri)
    RelativeLayout rlShengRi;
    @BindView(R.id.tvgrjj)
    TextView tvgrjj;
    @BindView(R.id.tv_grjj)
    TextView tvGrjj;
    @BindView(R.id.rl_gerenjianjie)
    RelativeLayout rlGerenjianjie;
    @BindView(R.id.tv_shengao_text)
    TextView tvShengaoText;
    @BindView(R.id.tv_shengao)
    TextView tvShengao;
    @BindView(R.id.rl_shengao)
    RelativeLayout rlShengao;
    @BindView(R.id.tv_tizhong)
    TextView tvTizhong;
    @BindView(R.id.rl_tizhong)
    RelativeLayout rlTizhong;
    @BindView(R.id.tv_xuexing)
    TextView tvXuexing;
    @BindView(R.id.rl_xuexing)
    RelativeLayout rlXuexing;
    @BindView(R.id.tv_minzu)
    TextView tvMinzu;
    @BindView(R.id.rl_minzu)
    RelativeLayout rlMinzu;
    @BindView(R.id.tv_zongjiao)
    TextView tvZongjiao;
    @BindView(R.id.rl_zongjiao)
    RelativeLayout rlZongjiao;
    @BindView(R.id.tv_xueli)
    TextView tvXueli;
    @BindView(R.id.rl_xueli)
    RelativeLayout rlXueli;
    @BindView(R.id.tvgx)
    TextView tvgx;
    @BindView(R.id.tv_guxiang)
    TextView tvGuxiang;
    @BindView(R.id.ivgx)
    ImageView ivgx;
    @BindView(R.id.rl_guxiang)
    RelativeLayout rlGuxiang;
    @BindView(R.id.tvjzd)
    TextView tvjzd;
    @BindView(R.id.tv_juzhudi)
    TextView tvJuzhudi;
    @BindView(R.id.ivjzd)
    ImageView ivjzd;
    @BindView(R.id.rl_juzhudi)
    RelativeLayout rlJuzhudi;
    @BindView(R.id.tv_zhiye)
    TextView tvZhiye;
    @BindView(R.id.rl_zhiye)
    RelativeLayout rlZhiye;
    @BindView(R.id.tv_danwei)
    EditText tvDanwei;
    @BindView(R.id.tv_ganqing)
    TextView tvGanqing;
    @BindView(R.id.rl_ganqing)
    RelativeLayout rlGanqing;
    @BindView(R.id.tv_tianjia_jiaoyu)
    TextView tvTianjiaJiaoyu;
    @BindView(R.id.rv_receclerView_jiaoyu)
    RecyclerView rvRececlerViewJiaoyu;
    @BindView(R.id.tv_jiaoyu_jingli)
    TextView tvJiaoyuJingli;
    @BindView(R.id.tv_tianjia_gongzuo)
    TextView tvTianjiaGongzuo;
    @BindView(R.id.rv_receclerView_gongzuo)
    RecyclerView rvRececlerViewGongzuo;
    @BindView(R.id.tv_gongzuo_jingli)
    TextView tvGongzuoJingli;
    @BindView(R.id.rl_edu)
    RelativeLayout rlEdu;
    @BindView(R.id.rl_work)
    RelativeLayout rlWork;
    //takephoto相关
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String path_header;//头像图片路径
    private int HEADER_POPUP = 0;
    private int SEX_POPUP = 1;
    private OptionsPickerView pvCustomOptions;
    private List<String> xueXingList;
    private List<String> ganQingList;
    private List<String> shenGaoList = new ArrayList<>();
    private List<String> tiZhongList = new ArrayList<>();
    private List<String> zongJiaoList;
    private List<String> xueLiList;
    private List<String> zhiYeList;
    private TimePickerView pvTime;//时间选择器
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

            }
        }
    };
    private MyJingLiAdapter eduJingLiAdapter;
    private List<PersonalInfoModel.DataBean.InfoBean.EduExperienceBean> eduExperienceList = new ArrayList<>();
    private List<PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean> workExperienceList = new ArrayList<>();
    private MyJingLiAdapter workJingLiAdapter;
    private int editEduPosition = 0;
    private int editWorkPosition = 0;
    private String headerImgUrl = "";
    private ConfirmDialog confirmDialog;
    private ACache mACache;
    private String nickName;
    private String sexName;
    private String shengGao;
    private String tiZhong;
    private String xueXing;
    private String minZu;
    private String zongJiao;
    private String xueLi;
    private String shengRi;
    private String qianMing;
    private String guXiang;
    private String juZhuDi;
    private String zhiYe;
    private String danWei;
    private String ganQing;
    private boolean isToExpAty = false;//是否跳转到工作或教育经历页面的标记
    private int selectPosition = 2;//popup 默认选中的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfection_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        RxKeyboardTool.clickBlankArea2HideSoftInput0();
        initView();
    }

    private void initView() {
        tvTitleText.setText("个人信息");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
        inData();
        LinearLayoutManager jiaoYuLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerViewJiaoyu.setLayoutManager(jiaoYuLayoutManager);
        eduJingLiAdapter = new MyJingLiAdapter(JIAOYU_TYPE, R.layout.item_my_jingli, eduExperienceList);
        rvRececlerViewJiaoyu.setAdapter(eduJingLiAdapter);
        eduJingLiAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                editEduPosition = position;
                Bundle bundle = new Bundle();
                bundle.putSerializable("eduExperienceBean", eduExperienceList.get(position));
                RxActivityTool.skipActivityForResult(PersonalInfoActivity.this, EditEducationInfoAty.class, bundle, EPX_REQUEST_CODE);
            }
        });

        LinearLayoutManager gongZuoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerViewGongzuo.setLayoutManager(gongZuoLayoutManager);
        workJingLiAdapter = new MyJingLiAdapter(GONGZUO_TYPE, R.layout.item_my_jingli, workExperienceList);
        rvRececlerViewGongzuo.setAdapter(workJingLiAdapter);
        workJingLiAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                editWorkPosition = position;

                Bundle bundle = new Bundle();
                bundle.putSerializable("workExperienceBean", workExperienceList.get(position));
                RxActivityTool.skipActivityForResult(PersonalInfoActivity.this, EditWorkInfoAty.class, bundle, EPX_REQUEST_CODE);
            }
        });
    }

    private void inData() {
        String[] xueXingString = {"A", "B", "AB", "O"};
        xueXingList = Arrays.asList(xueXingString);
        for (int i = 100; i <= 250; i++) {
            shenGaoList.add(i + " cm");
        }
        for (int i = 30; i <= 200; i++) {
            tiZhongList.add(i + " kg");
        }
        String[] zongJiaoString = {"佛教", "儒教", "道教", "基督教", "伊斯兰教"};
        zongJiaoList = Arrays.asList(zongJiaoString);
        String[] xueliString = {"初中", "高中", "大专", "本科", "研究生", "硕士", "博士"};
        xueLiList = Arrays.asList(xueliString);
        String[] zhiYeString = {"摄影师", "设计师", "程序员", "老板"};
        zhiYeList = Arrays.asList(zhiYeString);
        String[] ganQingString = {"离异", "未婚", "单身", "恋爱", "已婚"};
        ganQingList = Arrays.asList(ganQingString);

        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        mACache = ACache.get(this);
        //查询个人信息
        oneInfo("");
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_PERSON_INFO) {//刷新数据
            oneInfo("");
        }
        if (myEventBusModel.REFRESH_PERSON_INFO_EPX) {//添加或编辑经历时 刷新
            isToExpAty = true;
            oneInfo("");
        }
    }

    private void oneInfo(final String jsonString) {

        OkGo.<String>get(Urls.ONE_INFO)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("json", jsonString)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {

                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data == null) {
                                    RxToast.showToast("请求失败，请重试！");
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());

                                if (jsonObjectEx.optBoolean("success")) {
                                    if (!RxDataTool.isNullString(jsonString)) {//更改
                                        RxToast.showToast("保存成功！");
//                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
//                                        myEventBusModel.REFRESH_MINE_HOME = true;
//                                        EventBus.getDefault().post(myEventBusModel);

                                        UserModel userModel = UserModel.getUserModel();
                                        userModel.setImgUrl(headerImgUrl);
                                        UserModel.updateUserModel(userModel);
                                        finish();
                                    } else {//查询
                                        PersonalInfoModel personalInfoModel = gson.fromJson(response.body(), PersonalInfoModel.class);
                                        PersonalInfoModel.DataBean.InfoBean personalInfoModelInfo = personalInfoModel.getData().getInfo();
                                        if (!isToExpAty) {

                                            tvNickName.setText(personalInfoModelInfo.getNickName());
                                            String gender = personalInfoModelInfo.getGender();
                                            if (!RxDataTool.isNullString(gender)) {
                                                tvSex.setText(gender.equals("1") ? "男" : "女");
                                            }
                                            tvShengao.setText(personalInfoModelInfo.getHeight() + " cm");
                                            tvTizhong.setText(personalInfoModelInfo.getWeight() + " kg");
                                            tvXuexing.setText(personalInfoModelInfo.getBloodType());
                                            tvXueli.setText(personalInfoModelInfo.getEduBackGround());
                                            tvMinzu.setText(personalInfoModelInfo.getNation());
                                            tvZongjiao.setText(personalInfoModelInfo.getReligion());
                                            tvShengRi.setText(personalInfoModelInfo.getBirthday());
                                            tvGrjj.setText(personalInfoModelInfo.getSignature());
                                            tvGuxiang.setText(personalInfoModelInfo.getHometown());

                                            String currentProvinceString = personalInfoModelInfo.getCurrentProvince();
                                            String currentCityString = personalInfoModelInfo.getCurrentCity();
                                            String currentCountyString = personalInfoModelInfo.getCurrentCounty();
                                            String juZhuDiString = "";
                                            if (!RxDataTool.isNullString(currentProvinceString) && !RxDataTool.isNullString(currentCityString)
                                                    && !RxDataTool.isNullString(currentCountyString)) {
                                                juZhuDiString = currentProvinceString + "-"
                                                        + currentCityString + "-" + currentCountyString;
                                            }
                                            tvJuzhudi.setText(juZhuDiString);
                                            tvZhiye.setText(personalInfoModelInfo.getOccupation());
                                            tvDanwei.setText(personalInfoModelInfo.getCompany());
                                            tvGanqing.setText(personalInfoModelInfo.getEmotionalStatus());
                                            ivMyHeader.setImageURI(personalInfoModelInfo.getImgUrl());
                                            getTextString();
                                            PersonalInfoModel.DataBean.InfoBean infoBean = new PersonalInfoModel.DataBean.InfoBean();
                                            setInfoModel(infoBean);
                                            mACache.put("personalInfoModel", infoBean);
                                        }
                                        isToExpAty = false;
                                        List<PersonalInfoModel.DataBean.InfoBean.EduExperienceBean> eduExperience = personalInfoModelInfo.getEduExperience();
                                        eduExperienceList.clear();
                                        if (eduExperience != null && eduExperience.size() > 0) {
                                            eduExperienceList.addAll(eduExperience);
                                            rlEdu.setVisibility(View.GONE);

                                        } else {
                                            rlEdu.setVisibility(View.VISIBLE);

                                        }
                                        List<PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean> workExperience = personalInfoModelInfo.getWorkExperience();
                                        workExperienceList.clear();
                                        if (workExperience != null && workExperience.size() > 0) {
                                            workExperienceList.addAll(workExperience);
                                            rlWork.setVisibility(View.GONE);
                                        } else {
                                            rlWork.setVisibility(View.VISIBLE);
                                        }
                                        workJingLiAdapter.notifyDataSetChanged();
                                        eduJingLiAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 点击空白处 隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            RxKeyboardTool.clickBlankArea2HideSoftInput1(PersonalInfoActivity.this, v, ev);


        }
        return super.dispatchTouchEvent(ev);
    }

    private void initJsonData() {//解析地址数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onBackPressed() {
        savePersonInfo(BACK_TYPE);
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.iv_my_header, R.id.perfection_header, R.id.rl_my_header, R.id.iv_row_nick, R.id.rl_nick_name,
            R.id.rl_sex, R.id.rl_sheng_ri, R.id.rl_gerenjianjie, R.id.rl_shengao, R.id.rl_tizhong, R.id.rl_xuexing, R.id.rl_minzu, R.id.rl_zongjiao, R.id.rl_xueli, R.id.rl_guxiang,
            R.id.rl_juzhudi, R.id.rl_zhiye, R.id.rl_ganqing, R.id.tv_tianjia_jiaoyu, R.id.tv_tianjia_gongzuo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                savePersonInfo(BACK_TYPE);
                break;
            case R.id.tv_right:
                if (RxDataTool.isNullString(path_header)) {//未选择图像 不上传头像
                    savePersonInfo(SAVE_TYPE);
                } else {
                    upLoadHeaderImg(SAVE_TYPE, "");
                }
                break;
            case R.id.perfection_header:
                break;
            case R.id.rl_my_header://选择头像
                showSelectPicturePop(HEADER_POPUP);

                break;
            case R.id.iv_row_nick:
                break;
            case R.id.rl_nick_name:
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                RxActivityTool.skipActivityForResult(this, UpdateGroupNameActivity.class, bundle, NICKNAME_REQUESTCODE);
                break;
            case R.id.rl_sex://性别
//                showSelectPicturePop(SEX_POPUP);
                break;
            case R.id.rl_sheng_ri:
                initTimePicker();
                pvTime.show();
                break;
            case R.id.rl_gerenjianjie:
                Bundle bundleJianJie = new Bundle();
                bundleJianJie.putInt("type", 2);
                bundleJianJie.putString("jianJieContent", tvGrjj.getText().toString().trim());
                RxActivityTool.skipActivityForResult(this, ChangeNickNameActivity.class, bundleJianJie, JIANJIE_REQUESTCODE);

                break;
            case R.id.rl_shengao:
                initCustomOptionPicker(SHENGAO_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_tizhong:
                initCustomOptionPicker(TIZHONG_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_xuexing:
                initCustomOptionPicker(XUEXING_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_minzu:
                Intent intent = new Intent(this, MinZuActivity.class);
                startActivityForResult(intent, MinZuActivity.activityId);
                break;
            case R.id.rl_zongjiao:
                initCustomOptionPicker(ZONGJIAO_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_xueli:
                initCustomOptionPicker(XUELI_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_guxiang:
                ShowPickerView(GUXIANG_TYPE);
                break;
            case R.id.rl_juzhudi:
                ShowPickerView(JUZHUDI_TYPE);
                break;
            case R.id.rl_zhiye:
                initCustomOptionPicker(ZHIYE_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.rl_ganqing:
                initCustomOptionPicker(GANQING_POPUP);
                pvCustomOptions.show();
                break;
            case R.id.tv_tianjia_jiaoyu://添加教育经历
                RxActivityTool.skipActivityForResult(this, EditEducationInfoAty.class, EPX_REQUEST_CODE);
                break;
            case R.id.tv_tianjia_gongzuo://添加工作经历
                RxActivityTool.skipActivityForResult(this, EditWorkInfoAty.class, EPX_REQUEST_CODE);

                break;
        }
    }

    private void savePersonInfo(final int type) {
        getTextString();
        PersonalInfoModel.DataBean.InfoBean infoBean = new PersonalInfoModel.DataBean.InfoBean();
        setInfoModel(infoBean);
        Gson gson = new Gson();
        final String jsonString = gson.toJson(infoBean);
        if (type == BACK_TYPE) {
            PersonalInfoModel.DataBean.InfoBean personalInfoModel = (PersonalInfoModel.DataBean.InfoBean) mACache.getAsObject("personalInfoModel");
            if (personalInfoModel == null) {
                finish();
            }
            if (RxDataTool.isNullString(path_header) && nickName.equals(personalInfoModel.getNickName()) && sexName.equals(personalInfoModel.getGender()) && shengGao.equals(personalInfoModel.getHeight()) && tiZhong.equals(personalInfoModel.getWeight())
                    && shengGao.equals(personalInfoModel.getHeight()) && xueXing.equals(personalInfoModel.getBloodType()) && minZu.equals(personalInfoModel.getNation()) && zongJiao.equals(personalInfoModel.getReligion())
                    && xueLi.equals(personalInfoModel.getEduBackGround()) && shengRi.equals(personalInfoModel.getBirthday()) && qianMing.equals(personalInfoModel.getSignature()) && juZhuDi.equals(personalInfoModel.getCurrentAddress())
                    && zhiYe.equals(personalInfoModel.getOccupation()) && danWei.equals(personalInfoModel.getCompany()) && ganQing.equals(personalInfoModel.getEmotionalStatus())) {
                finish();
            } else {
                confirmDialog = (ConfirmDialog) ConfirmDialog.newInstance(ConstantsPath.PERSONALINFO_BACK)
                        .setMargin(60)
                        .setOutCancel(false).show(getSupportFragmentManager());
                confirmDialog.setDialogOnClickListener(new ConfirmDialog.DialogOnClickListener() {
                    @Override
                    public void Yes() {
                        confirmDialog.dismiss();
                        if (RxDataTool.isNullString(path_header)) {
                            oneInfo(jsonString);
                        } else {
                            upLoadHeaderImg(SAVE_TYPE, jsonString);
                        }

                    }

                    @Override
                    public void No() {
                        confirmDialog.dismiss();
                        finish();
                    }
                });
            }
        } else {
            oneInfo(jsonString);
        }
    }

    private void setInfoModel(PersonalInfoModel.DataBean.InfoBean infoBean) {
        infoBean.setNickName(nickName);
        if (!RxDataTool.isNullString(sexName)) {
            sexName = sexName.equals("男") ? "1" : "2";
        }
        infoBean.setGender(sexName);
        shengGao = RxDataTool.getNumbers(this.shengGao);
        tiZhong = RxDataTool.getNumbers(this.tiZhong);
        /*if (this.shengGao.length() >= 3) {
            this.shengGao = this.shengGao.substring(0, 3);
        }*/

       /* String[] tiZhongSplit = tiZhong.split(" ");
        if (tiZhongSplit.length > 0) {
            tiZhong = tiZhongSplit[0];
        }*/
        infoBean.setHeight(this.shengGao);
        infoBean.setWeight(tiZhong);
        infoBean.setBloodType(xueXing);
        infoBean.setNation(minZu);
        infoBean.setReligion(zongJiao);
        infoBean.setEduBackGround(xueLi);
        infoBean.setBirthday(shengRi);
        infoBean.setSignature(qianMing);
        infoBean.setHometown(guXiang);
        infoBean.setCurrentAddress(juZhuDi);
        infoBean.setOccupation(zhiYe);
        infoBean.setCompany(danWei);
        infoBean.setEmotionalStatus(ganQing);
        infoBean.setImgUrl(headerImgUrl);
    }

    private void getTextString() {
        nickName = tvNickName.getText().toString().trim();
        sexName = tvSex.getText().toString().trim();
        shengGao = tvShengao.getText().toString().trim();
        tiZhong = tvTizhong.getText().toString().trim();
        xueXing = tvXuexing.getText().toString().trim();
        minZu = tvMinzu.getText().toString().trim();
        zongJiao = tvZongjiao.getText().toString().trim();
        xueLi = tvXueli.getText().toString().trim();
        shengRi = tvShengRi.getText().toString().trim();
        qianMing = tvGrjj.getText().toString().trim();
        guXiang = tvGuxiang.getText().toString().trim();
        juZhuDi = tvJuzhudi.getText().toString().trim();
        zhiYe = tvZhiye.getText().toString().trim();
        danWei = tvDanwei.getText().toString().trim();
        ganQing = tvGanqing.getText().toString().trim();
    }

    /**
     * 地址选择器
     *
     * @param type
     */
    private void ShowPickerView(final int type) {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);
                switch (type) {
                    case GUXIANG_TYPE:
                        tvGuxiang.setText(tx);
                        break;
                    case JUZHUDI_TYPE:
                        tvJuzhudi.setText(tx);
                        break;
                }


            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    /**
     * 时间选择器
     */
    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 23);
        Calendar endDate = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        String date = RxTimeTool.getDate(currentTimeMillis + "", "yyyy-MM-dd");
        if (date != null) {
            String[] splitDate = date.split("-");
            String year = splitDate[0];
            String month = splitDate[1];
            String day = splitDate[2];
            endDate.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        }
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                tvShengRi.setText(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void initCustomOptionPicker(final int popupType) {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        if (popupType == SHENGAO_POPUP) {
            selectPosition = 75;
        } else if (popupType == TIZHONG_POPUP) {
            selectPosition = 29;
        } else {
            selectPosition = 2;
        }
        pvCustomOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                switch (popupType) {
                    case XUEXING_POPUP:
                        String xueXing = xueXingList.get(options1);
                        tvXuexing.setText(xueXing);
                        break;
                    case SHENGAO_POPUP:
                        String shenGao = shenGaoList.get(options1);
                        tvShengao.setText(shenGao);
                        break;
                    case TIZHONG_POPUP:
                        String tiZhong = tiZhongList.get(options1);
                        tvTizhong.setText(tiZhong);
                        break;
                    case ZONGJIAO_POPUP:
                        String zongJiao = zongJiaoList.get(options1);
                        tvZongjiao.setText(zongJiao);
                        break;
                    case XUELI_POPUP:
                        String xueLi = xueLiList.get(options1);
                        tvXueli.setText(xueLi);
                        break;
                    case ZHIYE_POPUP:
                        String zhiYe = zhiYeList.get(options1);
                        tvZhiye.setText(zhiYe);
                        break;
                    case GANQING_POPUP:
                        String ganQing = ganQingList.get(options1);
                        tvGanqing.setText(ganQing);
                        break;
                }
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        TextView tvPopTitle = (TextView) v.findViewById(R.id.title);
                        final TextView tvOk = (TextView) v.findViewById(R.id.ok);
                        TextView tvCancel = (TextView) v.findViewById(R.id.cancel);

                        switch (popupType) {
                            case XUEXING_POPUP:
                                tvPopTitle.setText("血型");
                                break;
                            case SHENGAO_POPUP:
                                tvPopTitle.setText("身高");
                                break;
                            case TIZHONG_POPUP:
                                tvPopTitle.setText("体重");
                                break;
                            case ZONGJIAO_POPUP:
                                tvPopTitle.setText("宗教");
                                break;
                            case XUELI_POPUP:
                                tvPopTitle.setText("学历");
                                break;
                            case ZHIYE_POPUP:
                                tvPopTitle.setText("职业");
                                break;
                            case GANQING_POPUP:
                                tvPopTitle.setText("感情状态");
                                break;

                        }
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });


                    }
                })
                .setSelectOptions(selectPosition)
                .setOutSideCancelable(false)
                .isDialog(true)
                .build();
        switch (popupType) {
            case XUEXING_POPUP:
                pvCustomOptions.setPicker(xueXingList);//添加数据}
                break;
            case SHENGAO_POPUP:
                pvCustomOptions.setPicker(shenGaoList);//添加数据}
                break;
            case TIZHONG_POPUP:
                pvCustomOptions.setPicker(tiZhongList);//添加数据}
                break;
            case ZONGJIAO_POPUP:
                pvCustomOptions.setPicker(zongJiaoList);//添加数据}
                break;
            case XUELI_POPUP:
                pvCustomOptions.setPicker(xueLiList);//添加数据}
                break;
            case ZHIYE_POPUP:
                pvCustomOptions.setPicker(zhiYeList);//添加数据}
                break;
            case GANQING_POPUP:
                pvCustomOptions.setPicker(ganQingList);//添加数据}
                break;
        }

    }

    /**
     * 选择图片、性别的弹窗
     *
     * @param popupType
     */
    private void showSelectPicturePop(final int popupType) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        final Uri imageUri = Uri.fromFile(file);
        NiceDialog.init()
                .setLayoutId(R.layout.popup_normal)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        if (popupType == SEX_POPUP) {
                            holder.setText(R.id.tv_first, "男");
                            holder.setText(R.id.tv_second, "女");
                        }

                        holder.setOnClickListener(R.id.tv_cancle, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.tv_first, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (popupType == SEX_POPUP) {

                                    tvSex.setText("女");
                                } else {

                                    setCompressConfig(HEADER_WIDTH, HEADER_HEIGHT, MAX_PIC_SIZE);
                                    //其他设置
                                    TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                    take_builder.setCorrectImage(true);//纠正拍照的照片旋转角度
                                    takePhoto.setTakePhotoOptions(take_builder.create());
                                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(HEADER_WIDTH, HEADER_WIDTH));
                                }
                            }
                        });
                        holder.setOnClickListener(R.id.tv_second, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (popupType == SEX_POPUP) {
                                    tvSex.setText("男");
                                } else {
                                    setCompressConfig(HEADER_WIDTH, HEADER_HEIGHT, MAX_PIC_SIZE);
                                    //其他设置
                                    TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                    take_builder.setWithOwnGallery(false);//使用TakePhoto自带相册
                                    takePhoto.setTakePhotoOptions(take_builder.create());
                                    takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(HEADER_WIDTH, HEADER_WIDTH));
                                }

                            }
                        });
                    }

                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NICKNAME_REQUESTCODE://昵称
                if (null != PrefAppStore.getGroupName(PersonalInfoActivity.this) && PrefAppStore.getGroupName(PersonalInfoActivity.this).length() > 0) {
                    tvNickName.setText(PrefAppStore.getGroupName(PersonalInfoActivity.this));
                    PrefAppStore.setGroupName(PersonalInfoActivity.this, "");
                }
                break;
            case JIANJIE_REQUESTCODE://个人简介
                if (data != null) {
                    String jianJieText = data.getStringExtra("text");
                    tvGrjj.setText(jianJieText);
                }
                break;
        }

        if (requestCode == MinZuActivity.activityId && data != null) {
            String minZuText = data.getStringExtra("minZuText");
            tvMinzu.setText(minZuText);
        }
    }

    /**
     * 压缩配置
     *
     * @param width
     * @param height
     * @param maxSize 压缩不会超过这个值
     */
    private void setCompressConfig(int width, int height, int maxSize) {
        //压缩配置
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .create();
        config.enableReserveRaw(false);//是否保存原图
        takePhoto.onEnableCompress(config, true);
    }

    /**
     * 裁切配置
     *
     * @param width
     * @param height
     * @return
     */
    private CropOptions getCropOptions(int width, int height) {
        //裁切配置
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(width).setAspectY(height);//比例 184x184
        builder.setWithOwnCrop(false);//裁切工具:false是第三方true是takephoto自带的
        return builder.create();
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "takeSuccess：" + result.getImage().getCompressPath());
        path_header = result.getImage().getCompressPath();
        Uri headerUri = Uri.parse("file://" + path_header);
        ivMyHeader.setImageURI(headerUri);

    }

    /**
     * 上传头像
     */
    private void upLoadHeaderImg(final int type, final String jsonString) {
        OkGo.<String>post(Urls.uploadPicMember)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("myfiles", new File(path_header))
                .params("loadName", "okdiLife")
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.getBoolean("success")) {
                                JSONObject dataJson = jsonObjectEx.optJSONObject("data");
                                headerImgUrl = dataJson.optString("url");
                                if (type == SAVE_TYPE) {
                                    savePersonInfo(SAVE_TYPE);
                                } else if (type == BACK_TYPE) {
                                    oneInfo(jsonString);
                                }

                            } else {
                                RxToast.showToast("头像上传失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, "操作被取消");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

}
