plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.feature.notifications.api"
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