package com.merrichat.net.activity.meiyu;

import android.content.Context;

public class DisplayUtil {
    /**
     * dipתpx
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

}
