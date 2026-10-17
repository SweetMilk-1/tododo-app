plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.data.repository.room"
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":data:dto"))
    implementation(project(":data:repository:api"))

    implementation(libs.androidx.room3.common)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.hilt.android)

    ksp(libs.androidx.room3.compiler)
    ksp(libs.hilt.compiler)
}