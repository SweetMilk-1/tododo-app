plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.core.notifications.impl"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(libs.hilt.android)

    ksp(libs.hilt.compiler)

    implementation(project(":core:notifications:api"))
    implementation(project(":core:resources"))
}
