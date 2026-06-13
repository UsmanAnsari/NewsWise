plugins {
    id("newswise.android.feature")
    alias(libs.plugins.kotlin.serialization)
}


dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}