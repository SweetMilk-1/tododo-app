plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.core.utils"
    compileSdk {
        version = release(37)
    }
}
