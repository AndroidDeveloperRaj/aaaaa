package com.merrichat.net.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.facebook.common.util.Hex;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDeviceTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private final static Pattern mobilePhonePatten = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(166)|(199)|(17[0,1,3,5,6,7,8]))\\d{8}$");


    //
    // 判断一个字符串是否都为数字
    public static final boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    //
//    // 判断一个字符串是否都为数字
//    public boolean isDigit(String strNum) {
//        Pattern pattern = Pattern.compile("[0-9]{1,}");
//        Matcher matcher = pattern.matcher((CharSequence) strNum);
//        return matcher.matches();
//    }
//
    //截取数字
    public static final String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 截取非数字
    public static final String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
    // 判断一个字符串是否含有数字

    public static final boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;
    }


    /**
     * @param @param  phoneNo
     * @param @return
     * @return boolean
     * @throws
     * @Title: isMobilePhone
     * @Description: TODO验证是否是手机号
     */
    public static boolean isMobilePhone(String phoneNo) {
        return mobilePhonePatten.matcher(phoneNo).matches();
    }

    /**
     * 检测字符串是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * @param @param  str
     * @param @return
     * @return boolean
     * @throws
     * @Title: isPassword
     * @Description: TODO验证密码只能包含数字和字母
     */
    public static boolean isPassword(String str) {
        if (!str.matches("^[0-9a-zA-Z]+$")) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * 获取字符串中的手机号
     ***/
    public static String getTelnum(String sParam) {
        if (sParam.length() <= 0)
            return "";
        //|861
        Pattern pattern = Pattern.compile("(1)(3|5|8|7)\\d{9}$*");
        //Pattern.compile("(1[3|4|5|7|8][0-9]\\d{8})");
        Matcher matcher = pattern.matcher(sParam);
        StringBuffer bf = new StringBuffer();
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

    /**
     * @param @param  str
     * @param @return
     * @return boolean
     * @throws
     * @Title: isPassword
     * @Description: 6~20位的快递单号
     */
    public static boolean isRightExpreDanhao(String str) {
        if (!str.matches("^[A-Za-z0-9*#-]{6,20}$")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param @param  str
     * @param @return
     * @return boolean
     * @throws
     * @Title: isPassword
     * @Description: 6~120位的电话号码
     */
    public static boolean isRightPhone(String str) {
        if (isNumeric(str)) {
            if (str.length() >= 6 && str.length() <= 12) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param @param  addr
     * @param @return
     * @return boolean
     * @throws
     * @Title: checkDetailAddress
     * @Description: TODO匹配详细地址 中文和数字
     */
    public static boolean checkDetailAddress(String addr) {
        if (addr.matches("^[\u4e00-\u9fa50-9]+$")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @param @param  addr
     * @param @return
     * @return boolean
     * @throws
     * @Title: checkDetailAddress
     * @Description: TODO匹配 中文，数字，字母
     */
    public static boolean checkName(String addr) {
        if (addr.matches("^[\u4e00-\u9fa50-9A-Za-z]+$")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @param amount
     * @return
     * @Title: checkAmountValid
     * @Description: 检查输入金额是否有效，小数点前6位，后2位。
     * @return: boolean
     */
    public static boolean checkAmountValid(String amount) {
        Pattern amountPatten = Pattern.compile("^((([1-9]\\d{1,4})?\\d)(\\.\\d{1,2})?)$");
        return amountPatten.matcher(amount).matches();
    }


    /**
     * 四舍五入
     *
     * @param d
     * @param length 小数点保留几位
     * @return
     */
    public static double rounded(double d, int length) {
        java.math.BigDecimal b = new java.math.BigDecimal(d);
        return b.setScale(length, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 进1法
     *
     * @param d
     * @param length 小数点保留几位
     * @return
     */
    public static double roundeds(double d, int length) {
        java.math.BigDecimal b = new java.math.BigDecimal(d);
        return b.setScale(length, java.math.BigDecimal.ROUND_UP).doubleValue();
    }

    /**
     * 金额计算  加法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double moneyAdd(String d1, String d2) {
        BigDecimal bigDecimal = new BigDecimal(d1);
        BigDecimal bigDecimal2 = new BigDecimal(d2);
        BigDecimal bigDecimalAdd = bigDecimal.add(bigDecimal2);
        double add = bigDecimalAdd.doubleValue();
        return add;
    }


    /**
     * 金额计算  减法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double moneySubtract(String d1, String d2) {
        BigDecimal bigDecimal = new BigDecimal(d1);
        BigDecimal bigDecimal2 = new BigDecimal(d2);
        BigDecimal bigDecimalSubtract = bigDecimal.subtract(bigDecimal2);
        double subtract = bigDecimalSubtract.doubleValue();
        return subtract;
    }

    /**
     * 金额计算 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double moneyMultiply(String d1, String d2) {
        BigDecimal bigDecimal = new BigDecimal(d1);
        BigDecimal bigDecimal2 = new BigDecimal(d2);
        BigDecimal bigDecimalMultiply = bigDecimal.multiply(bigDecimal2);
        BigDecimal bigDecimal1 = bigDecimalMultiply.setScale(2, BigDecimal.ROUND_HALF_UP);
        double multiply = bigDecimal1.doubleValue();
        return multiply;
    }

    /**
     * 金额计算 除法
     *
     * @param d1
     * @param d2
     * @param scale 保留几位小数
     * @return BigDecimal.ROUND_HALF_UP  四舍五入
     * BigDecimal.ROUND_UP 进1法
     */
    public static double moneyDivide(String d1, String d2, int scale) {
        BigDecimal bigDecimal = new BigDecimal(d1);
        BigDecimal bigDecimal2 = new BigDecimal(d2);
        BigDecimal bigDecimalDivide = bigDecimal.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP);
        double divide = bigDecimalDivide.doubleValue();
        return divide;
    }


    /**
     * 保留两位小数
     *
     * @param d
     * @return
     */
    public static String rounded(double d) {
        DecimalFormat r = new DecimalFormat();
        r.applyPattern("#0.00");//保留小数位数，不足会补零 
        return r.format(d);
    }

    /**
     * 银行卡卫视进行判断
     *
     * @param d
     * @return
     */
    public static boolean isBankcard(String d) {
        d = d.replace(" ", "");
        if (d.length() > 19) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否全部为中文字符组成
     *
     * @param str 检测的文字
     * @return true：为中文字符串，false:含有非中文字符
     */
    public static boolean isChineseStr(String str) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            Matcher matcher = pattern.matcher(String.valueOf(c[i]));
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 身份证的验证
     *
     * @param idCardNum 检测的文字
     * @return
     */
    public static boolean validatorIdcard(String idCardNum) {
        if (idCardNum.length() != 18) {
            return false;
        }
        boolean result = false;// 余数标志结果
        char[] idcard = idCardNum.toCharArray();
        int total = 0;// 存放数字和系数相乘的结果相加
        int[] seed = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};// 种子数组
        for (int i = 0; i < idcard.length - 1; i++) {
            total += seed[i] * (int) idcard[i];// 累加
        }
        int modresult = total % 11;// 获取余数
        int[] mod = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};// 余数匹配种子
        for (int c : mod) {
            if (c == modresult) {// 如果余数在余数匹配种子里
                result = true;
                break;
            }
        }
        if (result == false) {// 如果余数不在余数匹配种子，身份证号码不对
            return false;// 返回假
        } else {
            char[] lastChar = {'1', '0', 'X', '9', '8', '7', '6', '5', '4',
                    '3', '2'};// 身份证尾数种子
            for (int i = 0; i < lastChar.length; i++) {
                if (lastChar[i] == idcard[idcard.length - 1]) {// 如果身份证号码尾数在身份证尾数种子
                    return true;// 返回真
                }
            }
            return false;// 返回假
        }
    }

    /**
     * 判断银行卡是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isBankCard(String str) {
        str = str.replace(" ", "");
        System.out.println("" + str);
        String REG_DIGIT = "[0-9]*";
        return str.matches(REG_DIGIT);
    }

    /**
     * 截取字符串的最后4位的方法
     *
     * @param str
     * @return
     */
    public static String subString4last(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.substring(str.length() - 4);
        }
        return "";
    }

    /**
     * 判断少于6位
     *
     * @param
     */
    public static boolean isSixLengthPasd(String strPass, int i) {
        String str = strPass.toString().trim();
        if (str.length() < i) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 按字符出现的位置截取字符串，并返回截取后的字符串
     *
     * @param str
     * @param startStr 开始截取的字符（不截取此字符）
     * @param lastStr  结束截取的字符（不截取此字符）
     * @return
     */
    public static String subStringData(String str, String startStr, String lastStr) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(startStr)) {
            Log.v("StringUtils", "参数传入错误");
            return null;
        }
        int index = str.lastIndexOf(lastStr);
        return str.substring((str.indexOf(startStr) + 1), index == -1 ? str.length() : index);
    }

    /**
     * 从指定位置截取字符串
     *
     * @param str
     * @param index 指定开始截取的索引
     * @return
     */
    public static String subStringLast(String str, int index) {
        if (TextUtils.isEmpty(str)) {
            return null;
        } else if (str.length() < index) {
            return null;
        } else {
            return str.substring(index);
        }

    }

    /**
     * 添加下划线
     *
     * @param content 待处理的文本内容
     * @param start   起始位置
     * @param end     终止位置
     * @param flags   传0  暂不清楚
     * @return
     */
    public static SpannableString add_underline(String content, int start, int end, int flags) {
        SpannableString str_result = new SpannableString(content);
        str_result.setSpan(new UnderlineSpan(), start, end, flags);
        return str_result;
    }

    public static String convertString(String url) {
        Log.e("StringUtil", "URL--1:" + url);
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (url.length() <= 5) {
            return "";
        }
        if (url.contains("https") && url.substring(0, 5).equals("https")) {
            url = "http" + url.substring(5, url.length());
        }
        Log.e("StringUtil", "URL--2:" + url);
        return url;
    }

    /**
     * 以一位字母开头 1-4位数字结尾
     *
     * @param addr
     * @return
     */
    public static boolean checkFirstletterForone(String addr) {
        if (addr.matches("^[a-zA-Z]{1}[0-9]{1,4}$")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 以两位字母开头 1-3位数字结尾
     *
     * @param addr
     * @return
     */
    public static boolean checkFirstletterFortwo(String addr) {
        if (addr.matches("^[a-zA-Z]{2}[0-9]{1,3}$")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 1-5位的数字字母
     *
     * @param addr
     * @return
     */
    public static boolean isTrueSeria(String addr) {
        if (addr.matches("^[a-zA-Z0-9]{1,5}$")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 判断是否为字母或数字
     *
     * @param str
     * @return 全部为字母或数字，true， 包含其它，false
     */
    public static boolean isDigitOrLetter(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    /**
     * @param @param  addr
     * @param @return
     * @return boolean
     * @throws
     * @Title: checkGroupingName
     * @Description: TODO匹配详细地址 中文和数字  1-20位
     */
    public static boolean checkGroupingName(String addr) {
        if (addr.matches("^[\u4e00-\u9fa50-9]{1,20}$")) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /*****
     * 避免ScrollView和ListView冲突;条目展示不全的问题
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        listView.setLayoutParams(params);

    }

    /**
     * 判断是否是小数
     *
     * @param str
     * @return
     */
    public static boolean isDecimal(String str) {
        return Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]+)?").matcher(str).matches();

    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);// 参数100表示不压缩

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * <p>
     * 将文件转成base64 字符串
     * </p>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) {
        File file = new File(path);
        FileInputStream inputFile = null;
        String result = "";
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            result = Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                inputFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code, String savePath)
            throws Exception {
        // byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    /**
     * 获取视频第一帧的方法
     *
     * @param @param filePath 视频路径
     * @return Bitmap 返回该视频的第一帧上的图片
     * @Title: createVideoThumbnail
     */
    @SuppressLint("NewApi")
    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
            Log.e("TAG", "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e("TAG", "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e("TAG", "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e("TAG", "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 将图像保存到SD卡中
     *
     * @param file
     * @param mBitmap
     */
    public static void saveMyBitmap(File file, Bitmap mBitmap) {
        if (null == file) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断SD卡是否存在
     *
     * @return
     */
    public static boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /***
     * 查看SD卡的剩余空间
     *
     * @return 单位：MB
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * 查看SD卡总容量
     *
     * @return 单位：MB
     */
    public static long getSDAllSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    //金额验证
    public static boolean isNumber(String str) {
        Pattern amountPatten = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        return amountPatten.matcher(str).matches();
    }

    public static int ScreenHeight(Activity mActivity) {
        //获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return height;
    }

    public static int ScreenWidth(Activity mActivity) {
        //获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return width;
    }

    public static Map<String, String> quJianMa(String parNum) {
        String parNumNumber = parNum.replaceAll("[a-zA-Z]", "");
        String parNumBianHao = parNum.replaceAll("[0-9]", "");
        Map<String, String> map = new HashMap<>();
        map.put("parNumNumber", parNumNumber);
        map.put("parNumBianHao", parNumBianHao);
        return map;
    }


    /**
     * 用来判断一个字串中字符是否全为字母
     *
     * @param str
     * @return
     */
    public static boolean isPhonticName(String str) {
        char[] chars = str.toCharArray();
        boolean isPhontic = false;
        for (int i = 0; i < chars.length; i++) {
            isPhontic = (chars[i] >= 'a' && chars[i] <= 'z') || (chars[i] >= 'A' && chars[i] <= 'Z');
            if (!isPhontic) {
                return false;
            }
        }
        return true;
    }

    /**
     * 隐去输入法的表情
     *
     * @param et
     */
    public static void setProhibitEmoji(EditText et) {
        InputFilter[] filters = {getInputFilterProhibitEmoji()};
        et.setFilters(filters);
    }

    public static InputFilter getInputFilterProhibitEmoji() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsEmoji(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };

        return filter;
    }

    public static boolean getIsEmoji(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
    }


    /**
     * 判断手机是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void setImgTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void setRelHeight(View imageView, Context context, float percent) {
        RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        linearParams1.height = (int) (getHeight(context) * percent);
        imageView.setLayoutParams(linearParams1);
    }

    public static void setLinHeight(View imageView, Context context, float percent) {
        LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        linearParams1.height = (int) (getHeight(context) * percent);
        imageView.setLayoutParams(linearParams1);
    }

    public static void setFraHeight(View imageView, Context context, float percent) {
        FrameLayout.LayoutParams linearParams1 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        linearParams1.height = (int) (getHeight(context) * percent);
        imageView.setLayoutParams(linearParams1);
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWidths(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    // 加密
    public static String getBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解密
    public static String getFromBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 截取View的截图
     *
     * @param view
     * @return
     */
    public static Bitmap viewShot(final View view) {
        if (view == null)
            return null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpec, measureSpec);

        if (view.getMeasuredWidth() <= 0 || view.getMeasuredHeight() <= 0) {

            return null;
        }
        Bitmap bm;
        try {
            bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            System.gc();
            try {
                bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError ee) {

                return null;
            }
        }
        Canvas bigCanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigCanvas.drawBitmap(bm, 0, iHeight, paint);
        view.draw(bigCanvas);
        return bm;
    }

    /**
     * 保存图片到手机相册，并通知图库更新
     *
     * @param context
     * @param bmp     图片bitmap
     * @return 返回图片保存的路径，开发人员可以根据返回的路径在手机里面查看，部分手机发送通知图库并不会更新
     */

    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Igomo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //GetToast.showToast(context,"保存图片到"+ Environment.getExternalStorageDirectory() + "/Igomo/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        String path = Environment.getExternalStorageDirectory() + "/Igomo/" + fileName;
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return Environment.getExternalStorageDirectory() + "/Igomo/" + fileName;
    }

    /**
     * 获取手机唯一标识
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }


    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        String imei = RxDeviceTool.getIMEI(context);
        if (TextUtils.isEmpty(imei)) {
            imei = RxDeviceTool.getAndroidId(context);
        }
        return imei;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static double getStatusBarHeight(Context context) {
        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    public static SpannableString getPrice(String price_sales) {
        final DecimalFormat df = new DecimalFormat("#0.00");
        String string = df.format(Double.parseDouble(price_sales.toString()));
        /**小数点后两位比原字体小*/
        SpannableString spanString = new SpannableString(string);
        RelativeSizeSpan span = new RelativeSizeSpan(0.75f);
        spanString.setSpan(span, string.indexOf(".") + 1, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    /**
     * 是否登录（未登录跳转到登录界面）
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        boolean isLogin;
        if (UserModel.getUserModel().getIsLogin()) {
            isLogin = true;
        } else {
            isLogin = false;
            RxActivityTool.skipActivity(context, LoginActivity.class);
        }
        return isLogin;
    }

    /**
     * 数量输入框中的内容限制（最大：小数点前9位，小数点后8位）
     *
     * @param edt
     */
    public static void judgeNumber(Editable edt, EditText editText, int leftNum, int rightNum) {

        String temp = edt.toString();
        int posDot = temp.indexOf(".");//返回指定字符在此字符串中第一次出现处的索引
        int index = editText.getSelectionStart();//获取光标位置

        if (posDot == 0) {//必须先输入数字后才能输入小数点
            edt.delete(0, temp.length());//删除所有字符
            return;
        }
        if (posDot < 0) {//不包含小数点
            if (temp.length() <= leftNum) {
                return;//小于9位数直接返回
            } else {
                edt.delete(index - 1, index);//删除光标前的字符
                return;
            }
        }
        if (posDot > leftNum) {//小数点前大于9位数就删除光标前一位
            edt.delete(index - 1, index);//删除光标前的字符
            return;
        }
        if (temp.length() - posDot - 1 > rightNum)//如果包含小数点
        {
            edt.delete(index - 1, index);//删除光标前的字符
            return;
        }
    }

    /**
     * 获取系统SDK版本
     *
     * @return
     */
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

    /**
     * 截取屏幕
     */
    public static Bitmap getIerceptionScreen(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b = view.getDrawingCache();

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap bitmap = Bitmap.createBitmap(b, 0, height - StringUtil.dip2px(activity, 240), width, StringUtil.dip2px(activity, 240));
        view.destroyDrawingCache();

        bitmap = FastBlur.fastBlur(bitmap, 20);
        if (bitmap != null) {
            return bitmap;
        } else {
            return null;
        }
    }


    /**
     * 密码+混淆码加密
     */
    public static String generateMd5(String pwd, String salt) {
        if (pwd == null) {
            pwd = "";
        }
        String saltedPass = pwd + salt;
        MessageDigest messageDigest = getMessageDigest();
        byte[] digest;
        try {
            digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        return new String(Hex.encodeHex(digest, false));
    }

    private static final MessageDigest getMessageDigest() {
        String algorithm = "MD5";
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
        }
    }

}
