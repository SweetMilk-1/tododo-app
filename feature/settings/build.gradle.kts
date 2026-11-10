plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.feature.settings"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.androidx.preference.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    project(":data:settings:keys")
}