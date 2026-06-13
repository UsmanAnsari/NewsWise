plugins {
    id("newswise.android.library")
    alias(libs.plugins.kotlin.serialization)
}


dependencies {
    api(libs.navigation.compose)
    api(libs.kotlinx.serialization.json)
}