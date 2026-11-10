plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.data.settings.impl"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":data:settings:api"))
    implementation(project(":data:settings:keys"))
    implementation(libs.hilt.android)

    ksp(libs.hilt.compiler)
}