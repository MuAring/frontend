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
}
