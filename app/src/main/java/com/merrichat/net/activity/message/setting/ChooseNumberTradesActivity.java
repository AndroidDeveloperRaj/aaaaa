package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.utils.MiscUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/27.
 * 设置/修改群规模
 */

public class ChooseNumberTradesActivity extends MerriActionBarActivity {

    public static final int activityId = MiscUtil.getActivityId();

    /**
     * 五百
     */
    @BindView(R.id.tv_five_hundred)
    TextView tvFiveHundred;
    @BindView(R.id.ll_five_hundred)
    LinearLayout llFiveHundred;


    /**
     * 一千
     */
    @BindView(R.id.tv_one_thousand)
    TextView tvOneThousand;
    @BindView(R.id.ll_one_thousand)
    LinearLayout llOneThousand;

    /**
     * 两千
     */
    @BindView(R.id.tv_two_thousand)
    TextView tvTwoThousand;
    @BindView(R.id.ll_two_thousand)
    LinearLayout llTwoThousand;


    /**
     * 五千
     */
    @BindView(R.id.tv_five_thousand)
    TextView tvFiveThousand;
    @BindView(R.id.ll_five_thousand)
    LinearLayout llFiveThousand;


    /**
     * 一万
     */
    @BindView(R.id.tv_ten_thousand)
    TextView tvTenThousand;
    @BindView(R.id.ll_ten_thousand)
    LinearLayout llTenThousand;

    /**
     * 两万
     */
    @BindView(R.id.tv_twenty_thousand)
    TextView tvTwentyThousand;
    @BindView(R.id.ll_twenty_thousand)
    LinearLayout llTwentyThousand;

    private int NumberPeopleFlag = 6;

    private final int ONE_THOUSAND = 1;
    private final int TWO_THOUSAND = 2;
    private final int FIVE_THOUSAND = 3;
    private final int TEN_THOUSAND = 4;
    private final int TWENTY_THOUSAND = 5;

    private final int WU_BAI = 6;


    /**
     * 选择交易人数
     */
    private int memberNum;


    /**
     * 群类型0：交流群，1：微商群（BTC） 2：集市群(CTC)
     */
    private int type;

    private boolean isWubai = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number_trades);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("设置/修改群规模");
        setLeftBack();
        setRightText("完成", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                if (type == 0) {
                    if (NumberPeopleFlag == WU_BAI) {
                        intent.putExtra("number", "500");
                        intent.putExtra("freezeCurrency", "0");
                    } else if (NumberPeopleFlag == ONE_THOUSAND) {
                        intent.putExtra("number", "1000");
                        intent.putExtra("freezeCurrency", "1000");
                    } else if (NumberPeopleFlag == TWO_THOUSAND) {
                        intent.putExtra("number", "2000");
                        intent.putExtra("freezeCurrency", "2000");
                    } else if (NumberPeopleFlag == FIVE_THOUSAND) {
                        intent.putExtra("number", "5000");
                        intent.putExtra("freezeCurrency", "5000");
                    } else if (NumberPeopleFlag == TEN_THOUSAND) {
                        intent.putExtra("number", "10000");
                        intent.putExtra("freezeCurrency", "10000");
                    } else if (NumberPeopleFlag == TWENTY_THOUSAND) {
                        intent.putExtra("number", "20000");
                        intent.putExtra("freezeCurrency", "20000");
                    }
                } else {
                    if (NumberPeopleFlag == WU_BAI) {
                        intent.putExtra("number", "500");
                        intent.putExtra("freezeCurrency", "0");
                    } else if (NumberPeopleFlag == ONE_THOUSAND) {
                        int freezeCurrency;
                        if (isWubai) {
                            freezeCurrency = 1000 - memberNum + 500;
                        } else {
                            freezeCurrency = 1000 - memberNum;
                        }
                        intent.putExtra("number", "1000");
                        intent.putExtra("freezeCurrency", freezeCurrency+"");
                    } else if (NumberPeopleFlag == TWO_THOUSAND) {
                        int freezeCurrency;
                        if (isWubai) {
                            freezeCurrency = 2000 - memberNum + 500;
                        } else {
                            freezeCurrency = 2000 - memberNum;
                        }
                        intent.putExtra("number", "2000");
                        intent.putExtra("freezeCurrency", freezeCurrency+"");
                    } else if (NumberPeopleFlag == FIVE_THOUSAND) {
                        int freezeCurrency;
                        if (isWubai) {
                            freezeCurrency = 5000 - memberNum + 500;
                        } else {
                            freezeCurrency = 5000 - memberNum;
                        }
                        intent.putExtra("number", "5000");
                        intent.putExtra("freezeCurrency", freezeCurrency+"");
                    } else if (NumberPeopleFlag == TEN_THOUSAND) {
                        int freezeCurrency;
                        if (isWubai) {
                            freezeCurrency = 10000 - memberNum + 500;
                        } else {
                            freezeCurrency = 10000 - memberNum;
                        }
                        intent.putExtra("number", "10000");
                        intent.putExtra("freezeCurrency", freezeCurrency+"");
                    } else if (NumberPeopleFlag == TWENTY_THOUSAND) {
                        int freezeCurrency;
                        if (isWubai) {
                            freezeCurrency = 20000 - memberNum + 500;
                        } else {
                            freezeCurrency = 20000 - memberNum;
                        }
                        intent.putExtra("number", "20000");
                        intent.putExtra("freezeCurrency", freezeCurrency+"");
                    }
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initView() {
        memberNum = getIntent().getIntExtra("memberNum", 0);
        type = getIntent().getIntExtra("type", 0);
        if (memberNum == 500) {
            isWubai = true;
            NumberPeopleFlag = WU_BAI;
            tvFiveHundred.setText("500人(冻结讯美币0)");
            tvOneThousand.setText("1000人(冻结讯美币1000)");
            tvTwoThousand.setText("2000人(冻结讯美币2000)");
            tvFiveThousand.setText("5000人(冻结讯美币5000)");
            tvTenThousand.setText("10000人(冻结讯美币10000)");
            tvTwentyThousand.setText("20000人(冻结讯美币20000)");

            setTextColor();
        }
        if (memberNum == 1000) {
            NumberPeopleFlag = ONE_THOUSAND;
            tvFiveHundred.setText("500人");
            tvOneThousand.setText("1000人(冻结讯美币1000)");
            tvTwoThousand.setText("2000人(补交讯美币1000)");
            tvFiveThousand.setText("5000人(补交讯美币4000)");
            tvTenThousand.setText("10000人(补交讯美币9000)");
            tvTwentyThousand.setText("20000人(补交讯美币19000)");

            setTextColor();
        } else if (memberNum == 2000) {
            NumberPeopleFlag = TWO_THOUSAND;
            tvFiveHundred.setText("500人");
            tvOneThousand.setText("1000人");
            tvTwoThousand.setText("2000人(冻结讯美币2000)");
            tvFiveThousand.setText("5000人(补交讯美币3000)");
            tvTenThousand.setText("10000人(补交讯美币8000)");
            tvTwentyThousand.setText("20000人(补交讯美币18000)");
            setTextColor();
        } else if (memberNum == 5000) {
            NumberPeopleFlag = FIVE_THOUSAND;
            tvFiveHundred.setText("500人");
            tvOneThousand.setText("1000人");
            tvTwoThousand.setText("2000人");
            tvFiveThousand.setText("5000人(冻结讯美币5000)");
            tvTenThousand.setText("10000人(补交讯美币5000)");
            tvTwentyThousand.setText("20000人(补交讯美币15000)");
            setTextColor();
        } else if (memberNum == 10000) {
            NumberPeopleFlag = TEN_THOUSAND;
            tvFiveHundred.setText("500人");
            tvOneThousand.setText("1000人");
            tvTwoThousand.setText("2000人");
            tvFiveThousand.setText("5000人");
            tvTenThousand.setText("10000人(冻结讯美币10000)");
            tvTwentyThousand.setText("20000人(补交讯美币10000)");
            setTextColor();
        } else if (memberNum == 20000) {
            NumberPeopleFlag = TWENTY_THOUSAND;
            tvFiveHundred.setText("500人");
            tvOneThousand.setText("1000人");
            tvTwoThousand.setText("2000人");
            tvFiveThousand.setText("5000人");
            tvTenThousand.setText("10000人");
            tvTwentyThousand.setText("20000人(冻结讯美币20000)");
            setTextColor();
        }
    }

    @OnClick({R.id.ll_five_hundred, R.id.ll_one_thousand, R.id.ll_two_thousand, R.id.ll_five_thousand, R.id.ll_ten_thousand, R.id.ll_twenty_thousand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_five_hundred://五百
                if (memberNum <= 500) {
                    if (NumberPeopleFlag == WU_BAI) {
                        return;
                    }
                    NumberPeopleFlag = WU_BAI;
                    setTextColor();
                }
                break;
            case R.id.ll_one_thousand://一千
                if (memberNum <= 1000) {
                    if (NumberPeopleFlag == ONE_THOUSAND) {
                        return;
                    }
                    NumberPeopleFlag = ONE_THOUSAND;
                    setTextColor();
                }
                break;
            case R.id.ll_two_thousand://两千
                if (memberNum <= 2000) {
                    if (NumberPeopleFlag == TWO_THOUSAND) {
                        return;
                    }
                    NumberPeopleFlag = TWO_THOUSAND;
                    setTextColor();
                }
                break;
            case R.id.ll_five_thousand://五千
                if (memberNum <= 5000) {
                    if (NumberPeopleFlag == FIVE_THOUSAND) {
                        return;
                    }
                    NumberPeopleFlag = FIVE_THOUSAND;
                    setTextColor();
                }
                break;
            case R.id.ll_ten_thousand://一万
                if (memberNum <= 10000) {
                    if (NumberPeopleFlag == TEN_THOUSAND) {
                        return;
                    }
                    NumberPeopleFlag = TEN_THOUSAND;
                    setTextColor();
                }
                break;
            case R.id.ll_twenty_thousand://两万
                if (memberNum <= 20000) {
                    if (NumberPeopleFlag == TWENTY_THOUSAND) {
                        return;
                    }
                    NumberPeopleFlag = TWENTY_THOUSAND;
                    setTextColor();
                }
                break;
        }
    }


    /**
     * 设置文字颜色
     */
    private void setTextColor() {
        switch (NumberPeopleFlag) {
            case WU_BAI:
                tvFiveHundred.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvOneThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTenThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                break;
            case ONE_THOUSAND:
                tvOneThousand.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTenThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveHundred.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case TWO_THOUSAND:
                tvOneThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTenThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveHundred.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case FIVE_THOUSAND:
                tvOneThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvTenThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveHundred.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case TEN_THOUSAND:
                tvOneThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTenThousand.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveHundred.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case TWENTY_THOUSAND:
                tvOneThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwoThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvFiveThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTenThousand.setTextColor(getResources().getColor(R.color.black_new_two));
                tvTwentyThousand.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                tvFiveHundred.setTextColor(getResources().getColor(R.color.black_new_two));
                break;
        }
    }
}
