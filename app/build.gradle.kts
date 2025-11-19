plugins {
    alias(libs.plugins.android.application)
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
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
//    androidTestImplementation(libs["ext-junit"])
//    androidTestImplementation(libs["espresso-core"])

    // Retrofit (네트워크 통신)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (HTTP 클라이언트)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson (JSON 파싱)
    implementation("com.google.code.gson:gson:2.10.1")
}
