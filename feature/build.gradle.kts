plugins { alias(libs.plugins.android.library) }

android {
    namespace = "org.maru.muaring.feature"
    compileSdk = 36
    defaultConfig { minSdk = 24; targetSdk = 36 }
    buildFeatures { viewBinding = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
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
}
