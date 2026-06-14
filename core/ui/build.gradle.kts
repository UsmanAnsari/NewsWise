plugins {
    id("newswise.android.library")
    id("newswise.android.compose")
}


dependencies {
    implementation(project(":core:domain"))
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.compose.material.icons)
    api(libs.androidx.paging.compose)
}