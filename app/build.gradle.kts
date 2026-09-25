


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") // Hilt usa KSP para generar código
}

android {
    namespace = "com.example.radiostreaming"
    compileSdk = 34  // subido de 33 a 34

    defaultConfig {
        applicationId = "com.example.radiostreaming"
        minSdk = 24
        targetSdk = 34  // subido de 33 a 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.14"
        }
    }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")                          // bajado de 1.18.0
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")         // bajado de 2.10.0
    implementation("androidx.activity:activity-compose:1.9.3")               // bajado de 1.13.0
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))      // actualizado
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:19.1.0")

    
        implementation("androidx.media3:media3-exoplayer:1.4.1")
        implementation("androidx.media3:media3-common:1.4.1")
        // ... el resto de tus dependencias
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // ... el resto de tus dependencias

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
        implementation("androidx.navigation:navigation-compose:2.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}