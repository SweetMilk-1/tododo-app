plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.data.repository.api"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}

dependencies {
    api(project(":data:dto"))

    implementation(libs.kotlinx.coroutines.core)
}