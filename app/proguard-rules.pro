-keep class com.example.cleanerapp.MainActivity {
    public *;
}
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
