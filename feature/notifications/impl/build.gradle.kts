plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.feature.notifications.impl"
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
    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(project(":feature:notifications:api"))
    implementation(project(":core:notifications:api"))
    implementation(project(":data:settings:api"))
    implementation(project(":data:repository:api"))
}