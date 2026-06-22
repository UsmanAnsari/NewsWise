plugins {
    id("newswise.android.library")
    id("newswise.android.hilt")
    id("newswise.android.room")
}

android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.room.paging)
    implementation(libs.androidx.paging.runtime)

    testImplementation(project(":core:testing"))
    testImplementation(libs.room.testing)
}