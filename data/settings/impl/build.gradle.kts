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
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.androidx.room3.compiler)
    ksp(libs.hilt.compiler)
}