import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.TestExtension
import com.android.build.api.dsl.LibraryExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.Properties
import kotlin.apply

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false

    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

val compileSdkVersion = 36
val minSdkVersion = 29
val javaVersion = JavaVersion.VERSION_21
val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
val kotlinJvmTarget = JvmTarget.JVM_21

extra["rootNamespace"] = "toaru.ray.android"

// Release signing uses keystore.properties when present; falls back to debug key for templates/CI.
data class ReleaseSigningConfig(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
)

val releaseSigningConfig: ReleaseSigningConfig? =
    run {
        val propsFile = rootProject.file("keystore.properties")
        if (!propsFile.exists()) return@run null

        val properties =
            Properties().apply {
                propsFile.inputStream().use(::load)
            }

        fun requireNonBlank(key: String): String? =
            properties.getProperty(key)?.trim()?.takeIf { it.isNotBlank() }

        val storeFilePath = requireNonBlank("storeFile") ?: return@run null
        val storeFile = rootProject.file(storeFilePath)
        if (!storeFile.exists()) return@run null

        ReleaseSigningConfig(
            storeFile = storeFile,
            storePassword = requireNonBlank("storePassword") ?: return@run null,
            keyAlias = requireNonBlank("keyAlias") ?: return@run null,
            keyPassword = requireNonBlank("keyPassword") ?: return@run null,
        )
    }

fun CommonExtension.configureAndroidDefaults() {
    compileSdk = compileSdkVersion
    defaultConfig.apply {
        minSdk = minSdkVersion
        testInstrumentationRunner = instrumentationRunner
    }
    compileOptions.apply {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    lint.apply {
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        disable += "GradleDependency"
    }
    packaging.apply {
        resources {
            // Keep multi-release metadata; only exclude common license files
            excludes += setOf("META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.ktlint.get().pluginId)
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
    }

    // ktlint configuration (applies to all modules)
    plugins.withId("org.jlleitschuh.gradle.ktlint") {
        extensions.configure<KtlintExtension>("ktlint") {
            // Treat style issues as errors in CI, keep strict locally
            ignoreFailures.set(false)
            outputToConsole.set(true)
            // Exclude generated/build output
            filter {
                exclude("**/build/**")
                exclude("**/generated/**")
            }
        }

        // Ensure style checks run as part of `check`
        tasks.matching { it.name == "check" }.configureEach {
            dependsOn("ktlintCheck")
        }
    }

    // detekt configuration (applies to all modules)
    plugins.withId("io.gitlab.arturbosch.detekt") {
        // Add formatting ruleset
        dependencies.add("detektPlugins", rootProject.libs.detekt.formatting)

        val detektAutoCorrect =
            providers
                .gradleProperty("detekt.autoCorrect")
                .map { it.equals("true", ignoreCase = true) }
                .orElse(false)
                .get()

        extensions.configure<DetektExtension>("detekt") {
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = detektAutoCorrect
            config.setFrom(files(rootProject.file("config/detekt/detekt.yml")))

            // Use baseline if present without forcing it
            val baselineFile = rootProject.file("config/detekt/baseline.xml")
            if (baselineFile.exists()) {
                baseline = baselineFile
            }
        }

        // Configure reports and exclude build/generated directories
        tasks.withType<Detekt>().configureEach {
            reports {
                html.required.set(true)
                xml.required.set(true)
                txt.required.set(false)
                sarif.required.set(false)
                md.required.set(false)
            }
            setSource(files(projectDir))
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**", "**/generated/**")
        }

        // Ensure static analysis runs as part of `check`
        tasks.matching { it.name == "check" }.configureEach {
            dependsOn("detekt")
        }
    }

    plugins.withType<com.android.build.gradle.AppPlugin> {
        extensions.configure<ApplicationExtension>("android") {
            configureAndroidDefaults()
            defaultConfig {
                targetSdk = compileSdkVersion
            }

            buildTypes {
                debug {
                    manifestPlaceholders["profileableShell"] = "true"
                }
                release {
                    manifestPlaceholders["profileableShell"] = "false"
                }
            }

            signingConfigs {
                create("release") {
                    val config = releaseSigningConfig
                    if (config != null) {
                        storeFile = config.storeFile
                        storePassword = config.storePassword
                        keyAlias = config.keyAlias
                        keyPassword = config.keyPassword
                    } else {
                        initWith(getByName("debug"))
                    }
                }
            }
        }
    }

    plugins.withType<com.android.build.gradle.LibraryPlugin> {
        extensions.configure<LibraryExtension>("android") {
            configureAndroidDefaults()
        }
    }

    plugins.withType<com.android.build.gradle.TestPlugin> {
        extensions.configure<TestExtension>("android") {
            configureAndroidDefaults()

            targetProjectPath = ":app"
        }
    }

    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<KotlinAndroidProjectExtension> {
            compilerOptions {
                jvmTarget.set(kotlinJvmTarget)
                freeCompilerArgs.addAll(
                    listOf(
                        "-jvm-default=enable",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=kotlinx.coroutines.FlowPreview",
                    ),
                )
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                jvmTarget.set(kotlinJvmTarget)
            }
        }
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
        extensions.findByType<ApplicationExtension>()?.buildFeatures?.compose = true
        extensions.findByType<LibraryExtension>()?.buildFeatures?.compose = true
    }
}
