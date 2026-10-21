plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.data.dto"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}

dependencies {

}