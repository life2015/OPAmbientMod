# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.retrox.aodmod.*

-dontwarn android.support.annotation.Keep
#保留注解，如果不添加改行会导致我们的@Keep注解失效
-keepattributes *Annotation*
-keep @android.support.annotation.Keep class **{
@android.support.annotation.Keep <fields>;
@android.support.annotation.Keep <methods>;
}

-dontwarn com.retrox.aodmod.app.util.Utils

-keep class * implements android.os.Parcelable { # 保持Parcelable不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keep class com.retrox.aodmod.proxy.DreamProxy {
    private *;
    final *;
    public *;
}

-keep class com.retrox.aodmod.data.NowPlayingMediaData{*;}
-keep class com.retrox.aodmod.shared.data.SharedState{*;}
-keep class com.retrox.aodmod.proxy.view.theme.ThemeClockPack{*;}
-keep class com.retrox.aodmod.app.util.Utils{*;}

# Do not remove - Required by the EdXposed check
-keep class com.retrox.aodmod.app.XposedUtils{*;}

# Kotlin
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
