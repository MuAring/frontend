plugins { alias(libs.plugins.android.library) }

android {
    namespace = "org.maru.muaring.data"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson (JSON 파싱)
    implementation("com.google.code.gson:gson:2.10.1")
}
