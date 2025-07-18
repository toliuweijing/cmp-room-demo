import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.composeCompiler)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

kotlin {
    // Define targets
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
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

    sourceSets {
        // Define versions
        val coroutinesVersion = "1.8.0"
        val ktorVersion = "3.1.2"
        val roomVersion = "2.7.1"
        val pagingVersion = "3.3.0"
        val lifecycleVersion = "2.8.4"
        val navVersion = "2.7.7"
        val koinVersion = "3.5.6" // Use the latest version

        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Paging
            implementation("app.cash.paging:paging-common:3.3.0-alpha02-0.5.1")
            implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.5.1")

            // Room
            implementation("androidx.room:room-paging:${roomVersion}")
            implementation("androidx.room:room-ktx:$roomVersion")
            implementation("androidx.room:room-runtime:$roomVersion")
//            kapt("androidx.room:room-compiler:2.7.1")

            // Ktor
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

            // Coroutines & Lifecycle
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

            // Navigation
            implementation("androidx.navigation:navigation-compose:$navVersion")
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            implementation("androidx.paging:paging-runtime:3.3.0-alpha02")
            implementation("androidx.paging:paging-compose:3.3.0-alpha02")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Room Schema Location
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP Dependencies
dependencies {
//    kapt("androidx.room:room-compiler:2.7.1")
    add("kspCommonMainMetadata", "androidx.room:room-compiler:2.7.1")
    add("kspAndroid", "androidx.room:room-compiler:2.7.1")
    add("kspIosX64", "androidx.room:room-compiler:2.7.1")
    add("kspIosArm64", "androidx.room:room-compiler:2.7.1")
    add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.7.1")
}
