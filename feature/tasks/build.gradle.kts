plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "ru.tododo.feature.tasks"
    compileSdk {
        version = release(37)
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Project
    implementation(project(":core:future"))
    implementation(project(":core:utils"))
    implementation(project(":data:dto"))
    implementation(project(":data:repository:api"))


}