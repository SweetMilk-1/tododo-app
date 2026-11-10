plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.data.settings.api"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}