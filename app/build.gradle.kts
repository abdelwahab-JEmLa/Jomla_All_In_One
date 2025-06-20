plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("io.realm.kotlin")
}

android {
    namespace = "com.example.clientjetpack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clientjetpack"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Add this to potentially bypass some restrictions
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res",
                "src/main/res-layouts",
                "src/main/res-main",
                "src/main/res-xml"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.engage.core)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.generativeai)
    implementation(libs.material)

    // Test dependencies
    testImplementation(libs.junit)
    // Mockito dependencies pour tests unitaires
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0") // Pour mocker les méthodes finales
    // Coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // Pour tester les ViewModel et LiveData
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Add this line to fix the InstantTaskExecutorRule issue
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)

    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.gson)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    // Glide core dependencies
    implementation(libs.glide)
    kapt(libs.compiler)

    // Glide Compose integration
    implementation(libs.compose.v100beta01)
    implementation(libs.glide.transformations)
    implementation(libs.play.services.nearby)

    // OSMDroid dependencies
    implementation(libs.osmdroid.android)
    implementation(libs.osmdroid.wms)
    implementation(libs.osmdroid.mapsforge)

    // Lottie Compose
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // Koin pour Android
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    // Koin Testing dependencies - ADDED TO FIX TODOs
    testImplementation("io.insert-koin:koin-test:3.5.0")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.0")
    androidTestImplementation("io.insert-koin:koin-test:3.5.0")
    androidTestImplementation("io.insert-koin:koin-test-junit4:3.5.0")

    // Dépendances Realm
    implementation("io.realm.kotlin:library-base:1.12.0")
    implementation("io.realm.kotlin:library-sync:1.12.0")

    // MockK library for testing
    testImplementation("io.mockk:mockk:1.13.5")

    // If you need Android-specific features of MockK
    testImplementation("io.mockk:mockk-android:1.13.5")

    // Other testing dependencies you might need if not already included
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    //Dependencies ProtoIndex0
    // Guava for ListenableFuture (required by CameraX)
    implementation("com.google.guava:guava:32.1.3-android")

    // CameraX (used in B_1_CameraFAB.kt)
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // mongodb
    implementation("org.mongodb:bson:4.11.1")

}

apply(plugin = "com.google.gms.google-services")
