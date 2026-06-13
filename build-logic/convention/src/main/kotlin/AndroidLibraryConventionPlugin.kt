import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 37
                defaultConfig.minSdk = 26

                // Dynamic Namespace Generation
                // Converts module path ":core:common" -> "com.uansari.newswise.core.common"
                val modulePath = project.path.split(":").drop(1).joinToString(".")
                namespace = if (modulePath.isNotEmpty()) {
                    "com.uansari.newswise.$modulePath"
                } else {
                    "com.uansari.newswise"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)
            }
        }
    }
}