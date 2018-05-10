package com.merrichat.net.activity.meiyu.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.NewMeetNiceActivity;
import com.merrichat.net.activity.meiyu.fragments.view.MyViewPager;
import com.merrichat.net.activity.my.mywallet.RechargeMoneyActivity;
import com.merrichat.net.adapter.GiftGridViewAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.GiftListsMode;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.RxTools.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FragmentGiftDialog extends DialogFragment {
    private Dialog dialog;
    private MyViewPager vp;
    private List<View> gridViews;
    private LayoutInflater layoutInflater;
    private ArrayList<Gift> catogarys;

    private TextView coinCount;     //我的讯美币数量
    private TextView giftSubmit;        // 赠送按钮
    private int giftShowNum;  //每页多少个
    private TextView tvDialogTitle;  //标题
    private RadioGroup radio_group;  //指示器
    private TextView tv_chongzhi;  //充值
    private LinearLayout gif_ll;
    private int selectPosition = -1;
    private View vlLine1;
    private View vlLine2;
    private String cashBalance = "0.0";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.common_gift_dialog_layout, null, false);
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        initDialogStyle(rootView);
        initView(rootView);
        return dialog;

    }

    private void initView(View rootView) {
        Bundle args = getArguments();
        if (args == null)
            return;
        layoutInflater = getActivity().getLayoutInflater();
        vp = (MyViewPager) rootView.findViewById(R.id.view_pager);
        coinCount = (TextView) rootView.findViewById(R.id.coin_count);
        giftSubmit = (TextView) rootView.findViewById(R.id.submit);
        tv_chongzhi = (TextView) rootView.findViewById(R.id.tv_chongzhi);
        gif_ll = (LinearLayout) rootView.findViewById(R.id.gif_ll);
        tvDialogTitle = (TextView) rootView.findViewById(R.id.tv_dialog_title);
        radio_group = (RadioGroup) rootView.findViewById(R.id.radio_group);

        vlLine1 = (View) rootView.findViewById(R.id.vl_line1);
        vlLine2 = (View) rootView.findViewById(R.id.vl_line2);


        if (giftShowNum == 4) { //判断如果页面的礼物数量是4则设置成背景白色
            vp.heightLevel = 1;
            gif_ll.setBackgroundColor(Color.WHITE);
            tvDialogTitle.setTextColor(Color.BLACK);
            coinCount.setTextColor(Color.BLACK);
            vlLine1.setBackgroundResource(R.color.background);
            vlLine2.setBackgroundResource(R.color.background);

        } else {
            vp.heightLevel = 2;

        }
        cashBalance = PrefAppStore.getCashBalance(getActivity());
        coinCount.setText("讯美币：" + cashBalance);

        catogarys = new ArrayList<Gift>();

        ApiManager.getApiManager().getService(WebApiService.class).queryWalletInfo(UserModel.getUserModel().getAccountId(), "0", UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryWalletInfoModel>() {
                    @Override
                    public void onNext(QueryWalletInfoModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            coinCount.setText("讯美币：" + queryWalletInfoModel.data.giftBalance);
                            PrefAppStore.setCashBalance(getActivity(),queryWalletInfoModel.data.giftBalance);
                            getGifts();
                        } else {
                            Toast.makeText(getActivity(), queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });


       // getGifts();

        tv_chongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                startActivity(new Intent(getActivity(), RechargeMoneyActivity.class));

            }
        });
    }

    public void initViewPager(final ArrayList<Gift> gifts) {
        gridViews = new ArrayList<View>();
        ///定义第一个GridView
        for (int i = 0; i < (gifts.size() / giftShowNum); i++) {
            GridView gridView =
                    (GridView) layoutInflater.inflate(R.layout.grid_fragment_home, null);
            final GiftGridViewAdapter giftGridViewAdapter = new GiftGridViewAdapter(getActivity(), i, giftShowNum);

            List<Gift> gifts1 = new ArrayList<>();
            for (int j = 0; j < giftShowNum; j++) {
                gifts1.add(gifts.get(i * giftShowNum + j));
            }
            giftGridViewAdapter.setGifts(gifts1);
            gridView.setAdapter(giftGridViewAdapter);
            final int page = i;
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectPosition = page * giftShowNum + position;
                    giftGridViewAdapter.setSelectedPosition(position);
                    giftGridViewAdapter.notify(page);
                }
            });
            gridViews.add(gridView);

            RadioButton rb = new RadioButton(getActivity());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = DensityUtils.dp2px(getActivity(), 5);
            params.width = DensityUtils.dp2px(getActivity(), 5);
            params.setMargins(DensityUtils.dp2px(getActivity(), 3), 0, DensityUtils.dp2px(getActivity(), 3), 0);
            rb.setLayoutParams(params);
            rb.setButtonDrawable(null);
            radio_group.addView(rb);
            ((RadioButton) radio_group.getChildAt(0)).setChecked(true);
        }


        giftSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPosition == -1) {
                    RxToast.showToast("请选择一个礼物");
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    onGridViewClickListener.click(catogarys.get(selectPosition));
                }
            }
        });
        ///定义viewpager的PagerAdapter
        vp.setAdapter(new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;

            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return gridViews.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(gridViews.get(position));
                //super.destroyItem(container, position, object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(gridViews.get(position));
                return gridViews.get(position);
            }
        });
        ///注册viewPager页选择变化时的响应事件
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton radioButton = (RadioButton) radio_group.getChildAt(position);
                radioButton.setChecked(true);
                if (giftShowNum != 0 && selectPosition / giftShowNum == position) {

                } else {
                    ((GiftGridViewAdapter) ((GridView) gridViews.get(position)).getAdapter()).setSelectedPosition(-1);
                    ((GiftGridViewAdapter) ((GridView) gridViews.get(position)).getAdapter()).notifyDataSetChanged();
                }

            }
        });
    }

    public static final FragmentGiftDialog newInstance() {
        FragmentGiftDialog fragment = new FragmentGiftDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public OnGridViewClickListener onGridViewClickListener;

    /**
     * @param giftSize                每页显示的
     * @param onGridViewClickListener 回调点击事件
     * @return
     */
    public FragmentGiftDialog setOnGridViewClickListener(int giftSize, OnGridViewClickListener onGridViewClickListener) {
        this.giftShowNum = giftSize;
        this.onGridViewClickListener = onGridViewClickListener;
        return this;
    }

    public interface OnGridViewClickListener {
        void click(Gift gift);
    }

    private void initDialogStyle(View view) {
        dialog = new Dialog(getActivity(), R.style.CustomGiftDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
    }

    public void getGifts() {
        ApiManager.apiService(WebApiService.class).findGift()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<GiftListsMode>() {
                    @Override
                    public void onNext(GiftListsMode giftListsMode) {
                        if (giftListsMode.success) {
                            if (giftListsMode.data.size() > 0) {
                                catogarys = giftListsMode.data;
                                initViewPager(catogarys);
                            }
                        } else {
                            Toast.makeText(getContext(), "获取礼物列表失败！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "没有网络了，检查一下吧！", Toast.LENGTH_LONG).show();

                    }
                });
    }
}
