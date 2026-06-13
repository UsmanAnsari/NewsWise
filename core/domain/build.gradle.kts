plugins {
    id("newswise.jvm.library")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    implementation(libs.kotlinx.serialization.json)
}