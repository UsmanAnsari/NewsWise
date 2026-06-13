plugins {
    id("newswise.android.library")
    id("newswise.android.hilt")
    id("newswise.android.room")
}


dependencies {
    implementation(project(":core:common"))
    implementation(libs.room.paging)
    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.room.testing)
}