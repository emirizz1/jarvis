plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jarvis"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.jarvis"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-minimal"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    // agir kutuphaneleri simdilik kapattik - build gecince ekleyecegiz
    // implementation("androidx.room:room-runtime:2.6.1")
    // implementation("dev.rikka.shizuku:api:13.1.0")
}
