# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/AMSSY1/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#支付宝支付混淆
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

#友盟分享
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.merrichat.net.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature


# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


 -dontwarn org.apache.log4j.**
 -dontwarn org.apache.mina.**
 #不混淆mina包下内容
 -keep class org.apache.mina.**{
     *;
 }

 -dontwarn net.soureceforge.pinyin4j.**
 -dontwarn demo.**
 -keep class net.sourceforge.pinyin4j.** { *;}


 #butterknife
 -keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }

 -keepclasseswithmembernames class * {
     @butterknife.* <fields>;
 }

 -keepclasseswithmembernames class * {
     @butterknife.* <methods>;
 }



 #okhttp
 -dontwarn okhttp3.**
 -keep class okhttp3.**{*;}
 -keep interface okhttp3.**{*;}

 #okio
 -dontwarn okio.**
 -keep class okio.**{*;}
 -keep interface okio.**{*;}

   #EventBus
  -keepattributes *Annotation*
  -keepclassmembers class ** {
      @org.greenrobot.eventbus.Subscribe <methods>;
  }
  -keep enum org.greenrobot.eventbus.ThreadMode { *; }


  #Gson
  # removes such information by default, so configure it to keep all of it.
  -keepattributes Signature
  # Gson specific classes
  -keep class sun.misc.Unsafe { *; }
  -keep class com.google.gson.stream.** { *; }
  # Application classes that will be serialized/deserialized over Gson
  -keep class com.google.gson.examples.android.model.** { *; }
  -keep class com.google.gson.** { *;}

  #这句非常重要，主要是滤掉 com.merrichat.net.model包下的所有.class文件不进行混淆编译
  -keep class com.merrichat.net.model.** {*;}
  -keep class com.merrichat.net.message.cim.model.** {*;}


  -keep public interface org.apache.**

  -keepclasseswithmembernames class * {
      native <methods>;
  }

  -keepclasseswithmembers class * {
      public <init>(android.content.Context, android.util.AttributeSet);
  }

  -keepclasseswithmembers class * {
      public <init>(android.content.Context, android.util.AttributeSet, int);
  }


  #GreenDao
  -keep class org.greenrobot.greendao.**{*;}
  -keep public interface org.greenrobot.greendao.**
  -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
  public static java.lang.String TABLENAME;
  }
  -keep class **$Properties
  -keep class net.sqlcipher.database.**{*;}
  -keep public interface net.sqlcipher.database.**
  -dontwarn net.sqlcipher.database.**
  -dontwarn org.greenrobot.greendao.**


  #fastJson
  -keepattributes Signature
  -dontwarn com.alibaba.fastjson.**
  -keep class com.alibaba.fastjson.**{*; }
  -keepattributes Signature

  #Retrofit
  # Platform calls Class.forName on types which do not exist on Android to determine platform.
  -dontnote retrofit2.Platform
  # Platform used when running on RoboVM on iOS. Will not be used at runtime.
  -dontnote retrofit2.Platform$IOS$MainThreadExecutor
  # Platform used when running on Java 8 VMs. Will not be used at runtime.
  -dontwarn retrofit2.Platform$Java8
  # Retain generic type information for use by reflection by converters and adapters.
  -keepattributes Signature
  # Retain declared checked exceptions for use by a Proxy instance.
  -keepattributes Exceptions


  #Picasso
    -dontwarn com.squareup.okhttp.**

    # 源文件和行号的信息不混淆
    -keepattributes SourceFile,LineNumberTable

    -dontwarn com.yanzhenjie.permission.**

#    播放器混淆
    -keep class com.pili.pldroid.player.** { *; }
    -keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}

    -dontwarn org.codehaus.jackson.**
    -keep class org.codehaus.jackson.** { *;}

    -dontwarn org.akita.widget.**

    -dontwarn com.handmark.pulltorefresh.library.**
    -dontwarn com.dyhdyh.compat.mmrc.**
    -keep class com.alipay.android.phone.mrpc.core.**{ *;}
    -dontwarn com.alipay.android.phone.mrpc.core.b.*

     -keep class com.baidu.ocr.**{ *;}
    -dontwarn com.baidu.ocr.**
    -dontwarn android.net.SSLCertificateSocketFactory

