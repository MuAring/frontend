plugins { alias(libs.plugins.android.library) }

android {
    namespace = "org.maru.muaring.core"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(project(":data"))
    implementation(project(":design"))

    // Retrofit (네트워크 통신)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (HTTP 클라이언트)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson (JSON 파싱)
    implementation("com.google.code.gson:gson:2.10.1")
}
