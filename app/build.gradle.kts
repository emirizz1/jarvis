plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.jarvis"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.jarvis"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-vision-learning"
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("dev.rikka.shizuku:api:13.1.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0") // Gemini
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
