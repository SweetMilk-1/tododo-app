plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.feature.notification.api"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}
