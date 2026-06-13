import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("newswise.android.library")
            pluginManager.apply("newswise.android.compose")
            pluginManager.apply("newswise.android.hilt")

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner =
                        "com.uansari.newswise.core.testing.HiltTestRunner"
                }
            }

            dependencies {
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:common"))

                add("implementation", libsCatalog.library("hilt-navigation-compose"))
                add("implementation", libsCatalog.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libsCatalog.library("androidx-lifecycle-runtime-compose"))

                add("testImplementation", project(":core:testing"))
                add("androidTestImplementation", project(":core:testing"))
            }
        }
    }
}