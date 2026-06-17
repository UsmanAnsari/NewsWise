plugins {
    id("newswise.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.coil.compose)
}