plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.tododo.core.future"
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":core:utils"))
}
