import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "org.maru.muaring"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.maru.muaring"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        localProperties.load(FileInputStream(rootProject.file("local.properties")))
        val kakaoKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        buildConfigField ("String", "SPOTIFY_REDIRECT_URI", "\"muaring://spotify-redirect\"")

        manifestPlaceholders["kakao_scheme"] =
            "kakao${localProperties.getProperty("KAKAO_NATIVE_APP_KEY")}"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    // 다른 내부 모듈들
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":design"))
    implementation(project(":feature")) // 단일 feature 모듈 (home/search/group 다 포함)

    // Android 기본
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // 테스트
    testImplementation(libs.junit)

    implementation("com.kakao.sdk:v2-user:2.20.1")

    implementation ("com.google.dagger:hilt-android:2.52")
    annotationProcessor ("com.google.dagger:hilt-compiler:2.52")

    // Glide 추가
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}
