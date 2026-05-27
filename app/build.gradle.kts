plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.conectatec"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.conectatec"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // 10.0.2.2 = localhost de tu máquina desde el emulador
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
            isDebuggable = true
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://tu-servidor.com/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig  = true
    }
}

// Forzar javapoet 1.13.0 para evitar conflictos entre Hilt y Glide compilers
configurations.all {
    resolutionStrategy {
        force("com.squareup:javapoet:1.13.0")
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.swiperefresh)

    // Material Design 3
    implementation(libs.material)

    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Hilt
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // CircleImageView
    implementation(libs.circleimageview)

    // DataStore (guardar JWT)
    implementation(libs.datastore.prefs)

    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // ML Kit — Escaneo QR
    implementation(libs.mlkit.barcode)


    implementation(libs.lottie)

    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // ZXing — generación de QR
    implementation(libs.zxing.core)

    // Google Sign-In — Credential Manager
    implementation(libs.credentials)
    implementation(libs.credentials.gms)
    implementation(libs.google.identity)
    implementation(libs.security.crypto)

    testImplementation(libs.junit)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}