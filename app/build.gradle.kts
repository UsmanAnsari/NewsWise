import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.uansari.newswise"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.uansari.newswise"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.uansari.newswise.core.testing.HiltTestRunner"

        buildConfigField(
            "String", "NEWS_API_KEY", "\"${localProperties.getProperty("NEWS_API_KEY", "")}\""
        )

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}
kotlin {
    jvmToolchain(21)
}

dependencies {

    implementation(project(":feature:headlines"))
    implementation(project(":feature:search"))
    implementation(project(":feature:bookmarks"))
    implementation(project(":feature:detail"))

    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.compose.adaptive)
    implementation(libs.compose.window.size)
    implementation(libs.compose.adaptive.navigation.suite)
    implementation(libs.compose.adaptive.layout)
    implementation(libs.compose.adaptive.navigation)
}