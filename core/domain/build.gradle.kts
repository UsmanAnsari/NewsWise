plugins {
    id("newswise.jvm.library")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)

    // Unit test dependencies — JVM compatible, no Android required
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}