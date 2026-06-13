import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures.compose = true
                }
            }

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures.compose = true
                }
            }

            dependencies {
                val bom = platform(libsCatalog.library("androidx-compose-bom"))
                add("implementation", bom)
                add("androidTestImplementation", bom)

                add("implementation", libsCatalog.library("androidx-compose-ui"))
                add("implementation", libsCatalog.library("androidx-compose-ui-graphics"))
                add("implementation", libsCatalog.library("androidx-compose-runtime"))
                add("implementation", libsCatalog.library("androidx-compose-material3"))
                add("implementation", libsCatalog.library("androidx-compose-ui-tooling-preview"))

                add("debugImplementation", libsCatalog.library("androidx-compose-ui-tooling"))
                add(
                    "androidTestImplementation",
                    libsCatalog.library("androidx-compose-ui-test-junit4")
                )
                add("debugImplementation", libsCatalog.library("androidx-compose-ui-test-manifest"))
            }
        }
    }
}