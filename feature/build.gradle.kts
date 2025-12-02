plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "org.maru.muaring.feature"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    buildFeatures { viewBinding = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_21; targetCompatibility = JavaVersion.VERSION_21 }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":design"))

    implementation ("com.google.dagger:hilt-android:2.52")
    annotationProcessor ("com.google.dagger:hilt-compiler:2.52")

    implementation("com.kakao.sdk:v2-user:2.20.1")

    // app 말고 여기도 Glide 추가
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.android.flexbox:flexbox:3.0.0")
}
