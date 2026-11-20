plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serializations.json)
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

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kotlinx.serialization.json)
}