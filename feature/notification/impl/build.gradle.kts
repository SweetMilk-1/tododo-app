plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.feature.notification.impl"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":core:resources"))

    implementation(project(":feature:notification:api"))
    implementation(project(":data:settings:api"))

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.hilt.android)

    ksp(libs.hilt.compiler)

}
