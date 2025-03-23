import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html
            //noinspection UseTomlInstead
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4") //libs.androidx.lifecycle.viewmodel)
            //noinspection UseTomlInstead
            //implementation("androidx.activity:activity-compose:1.10.1")
            implementation(libs.androidx.lifecycle.runtime.compose)
            // Appleroot adaptive-ui
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-170.html#across-platforms
            //noinspection UseTomlInstead
            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.0-beta01")
            //noinspection UseTomlInstead
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.0-beta01")
            //noinspection UseTomlInstead
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.0-beta01")
            // material3.material3-adaptive-navigation-suite
            implementation(compose.material3AdaptiveNavigationSuite)
            // material3:material3-window-size-class
            //noinspection UseTomlInstead
            implementation("org.jetbrains.compose.material3:material3-window-size-class:1.8.0-alpha04")
            // material-navigation
            //noinspection UseTomlInstead
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-alpha15")
            //noinspection UseTomlInstead
            implementation("org.jetbrains.compose.material:material-navigation:1.8.0-beta01")

            // https://stackoverflow.com/questions/79340066/backhandler-on-compose-multiplatform-android-and-ios
            // noinspection UseTomlInstead
            implementation("org.jetbrains.compose.ui:ui-backhandler:1.8.0-beta01")

            // icons
            //noinspection UseTomlInstead
            implementation("br.com.devsrsouza.compose.icons:feather:1.1.1")

            // correct theming handle
            implementation(libs.materialKolor)

            //noinspection UseTomlInstead
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.example.reply"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.reply"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.example.reply.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.reply"
            packageVersion = "1.0.0"
        }
    }
}
