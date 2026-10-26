plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.core.utils"
    defaultConfig {
        minSdk = 24
    }
    compileSdk {
        version = release(37)
    }
}
dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.material)
}
