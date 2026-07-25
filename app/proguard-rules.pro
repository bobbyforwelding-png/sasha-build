# S.A.S.H.A. Anti-Reverse-Engineering ProGuard Rules

# Aggressive obfuscation
-repackageclasses ''
-allowaccessmodification
-optimizationpasses 5
-mergeinterfacesaggressively

# Rename source file to hide structure
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Obfuscate everything except what's needed
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Gemini AI SDK
-keep class com.google.ai.client.generativeai.** { *; }

# Keep Ktor
-keep class io.ktor.** { *; }

# Keep Room/SQLite
-keep class androidx.room.** { *; }

# Keep data classes (needed for JSON parsing)
-keep class com.example.domain.model.** { *; }
-keep class com.example.data.conversation.** { *; }
-keep class com.example.presentation.viewmodel.VaultChatMessage { *; }
-keep class com.example.presentation.viewmodel.VaultUiState { *; }
-keep class com.example.update.UpdateInfo { *; }
-keep class com.example.update.UpdateChecker { *; }

# Keep BuildConfig (API key)
-keep class com.example.BuildConfig { *; }

# Encrypt string literals (API keys, URLs)
-repackageclasses com.example.obfuscated

# Remove debug logging
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Anti-tampering
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Obfuscate class names
-renamesourcefileattribute obfuscated

# Keep XML parsers
-keep class org.xmlpull.v1.** { *; }

# Prevent stack trace analysis
-optimizations !code/simplification/variable,!code/simplification/arithmet,!code/simplification/cast,!field/*,!class/merging/*
