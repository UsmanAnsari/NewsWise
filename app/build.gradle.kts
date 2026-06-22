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
    signingConfigs {
        create("release") {
            val keystorePath = localProperties.getProperty("KEYSTORE_FILE", "")
            val keystorePass = localProperties.getProperty("KEYSTORE_PASSWORD", "")
            val keyAliasVal = localProperties.getProperty("KEY_ALIAS", "")
            val keyPass = localProperties.getProperty("KEY_PASSWORD", "")

            // CI: workflow writes all four values → guard passes → Gradle signs.
            if (keystorePath.isNotEmpty() && keystorePass.isNotEmpty() && keyAliasVal.isNotEmpty() && keyPass.isNotEmpty()) {
                storeFile = file(keystorePath)
                storePassword = keystorePass
                keyAlias = keyAliasVal
                keyPassword = keyPass
            }
        }
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
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
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