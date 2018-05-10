package com.merrichat.net.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by chenjingjing on 17/7/29.
 */

public class DensityUtils {

    private DensityUtils()

    {

          /* cannot be instantiated */

        throw new UnsupportedOperationException("cannot be instantiated");

    }


    /**
     * dp转px
     *
     * @param context
     * @param
     * @return
     */

    public static int dp2px(Context context, float dpVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,

                dpVal, context.getResources().getDisplayMetrics());

    }

    /**
     * dp转px
     *
     * @param context
     * @param
     * @return
     */

    public static int dp2px(Context context, int dpVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,

                dpVal, context.getResources().getDisplayMetrics());

    }


    /**
     * sp转px
     *
     * @param context
     * @param
     * @return
     */

    public static int sp2px(Context context, float spVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,

                spVal, context.getResources().getDisplayMetrics());

    }


    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */

    public static float px2dp(Context context, float pxVal)

    {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (pxVal / scale);

    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */

    public static int px2dp(Context context, int pxVal)

    {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxVal / scale);

    }


    /**
     * px转sp
     *
     * @param
     * @param pxVal
     * @return
     */

    public static float px2sp(Context context, float pxVal)

    {

        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);

    }


    public static float rounded(float number) {
        java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.0");
        float str = Float.parseFloat(myformat.format(number));
        return str;
    }
}
