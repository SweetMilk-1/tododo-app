plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.core.notifications.api"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}
