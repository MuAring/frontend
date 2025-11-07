plugins { alias(libs.plugins.android.library) }

android {
    namespace = "org.maru.muaring.design"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    buildFeatures { viewBinding = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
}
