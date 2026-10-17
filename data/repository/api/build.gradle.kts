plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.data.repository.api"
    compileSdk {
        version = release(37)
    }
}

dependencies {
    api(project(":data:dto"))

    implementation(libs.kotlinx.coroutines.core)
}