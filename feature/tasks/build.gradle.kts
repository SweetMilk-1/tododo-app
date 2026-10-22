plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.android.navigation.safeargs)
}

android {
    namespace = "ru.tododo.feature.tasks"
    defaultConfig {
        minSdk = 24
    }
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
    implementation(libs.androidx.recyclerview)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Project
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))
    implementation(project(":data:dto"))
    implementation(project(":data:repository:api"))
}