import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import java.util.Properties

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}



plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
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
        //Dev
        // a_AllInOne
        // b_JomLaElectroLivreurGrossist_VendeurHost
        // c_JomLaElectroLivreurGrossist_PresenterScreen

        // Boutique_JeMla_All_In_On_Project

        versionName = "1.14.0.06_18.08:47.Dev"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        vectorDrawables { useSupportLibrary = true }
        multiDexEnabled = true

        buildConfigField("String", "DROPBOX_APP_KEY",       "\"${localProps.getProperty("DROPBOX_APP_KEY", "")}\"")
        buildConfigField("String", "DROPBOX_APP_SECRET",    "\"${localProps.getProperty("DROPBOX_APP_SECRET", "")}\"")
        buildConfigField("String", "DROPBOX_REFRESH_TOKEN", "\"${localProps.getProperty("DROPBOX_REFRESH_TOKEN", "")}\"")
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
    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures {
        compose = true
        buildConfig = true   // ← génère la classe BuildConfig
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
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
    // Core Android
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.engage.core)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)

    // Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.compose.material)
    implementation(libs.material)

    // AI & Serialization
    implementation(libs.generativeai)
    implementation(libs.kotlinx.serialization.json)

    // Image loading
    implementation(libs.coil.compose)
    implementation(libs.glide)
    kapt(libs.compiler)
    implementation(libs.compose.v100beta01)
    implementation(libs.glide.transformations)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Utilities
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.gson)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Maps
    implementation(libs.play.services.nearby)
    implementation(libs.osmdroid.android)
    implementation(libs.osmdroid.wms)
    implementation(libs.osmdroid.mapsforge)
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")

    // Animations
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // Koin DI
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    // Realm
    implementation("io.realm.kotlin:library-base:1.12.0")
    implementation("io.realm.kotlin:library-sync:1.12.0")

    // Camera
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // MongoDB
    implementation("org.mongodb:bson:4.11.1")

    // PDF
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:html2pdf:4.0.5")

    // Coroutines + Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Dropbox SDK officiel
    implementation("com.dropbox.core:dropbox-core-sdk:7.0.0")

    // ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")

    // ExifInterface
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Excel
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // Calendar
    implementation("com.aminography:primecalendar:1.7.0")

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("io.mockk:mockk-android:1.13.5")
    testImplementation("io.insert-koin:koin-test:3.5.0")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("io.insert-koin:koin-test:3.5.0")
    androidTestImplementation("io.insert-koin:koin-test-junit4:3.5.0")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
