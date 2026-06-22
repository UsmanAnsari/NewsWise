plugins {
    id("newswise.android.library")
    id("newswise.android.hilt")
}


dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))

    implementation(libs.retrofit)
    implementation(libs.room.runtime)
    implementation(libs.androidx.paging.runtime)
}