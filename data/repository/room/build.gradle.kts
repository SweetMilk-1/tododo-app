plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.tododo.data.repository.room"
    compileSdk {
        version = release(37)
    }
}
