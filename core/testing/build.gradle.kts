plugins {
    id("newswise.android.library")
    id("newswise.android.compose")
    id("newswise.android.hilt")
}


dependencies {
    api(project(":core:domain"))
    api(project(":core:database"))

    api(libs.androidx.paging.testing)

    // ── Unit testing
    api(libs.junit)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.google.truth)
    api(libs.kotlinx.coroutines.test)

    // Instrumented / Android testing
    api(libs.androidx.junit)
    api(libs.androidx.junit.ktx)
    api(libs.androidx.test.core)
    api(libs.androidx.test.core.ktx)
    api(libs.androidx.espresso.core)
    api(libs.androidx.truth.ext)
    api(libs.navigation.testing)
    api(libs.robolectric)
    implementation(libs.room.runtime)

    // Hilt testing
    implementation(libs.hilt.android.testing)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}