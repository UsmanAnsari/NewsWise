plugins {
    id("newswise.android.library")
    id("newswise.android.hilt")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
}