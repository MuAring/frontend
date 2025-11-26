plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.maru.muaring.data"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_21; targetCompatibility = JavaVersion.VERSION_21 }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(":core"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.dagger:hilt-android:2.52")
    annotationProcessor ("com.google.dagger:hilt-compiler:2.52")
}
