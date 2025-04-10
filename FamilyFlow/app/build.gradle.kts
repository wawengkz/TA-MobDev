plugins {
    alias(libs.plugins.android.application) // Android plugin without version
    alias(libs.plugins.kotlin.android) // Kotlin Android plugin
    alias(libs.plugins.kotlin.compose) // Kotlin Compose plugin
    id("kotlin-kapt") // For Room annotation processing
}

android {
    namespace = "com.example.familyflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.familyflow"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add Room schema export directory
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    // Updated packaging options using the new syntax
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/*.kotlin_module")
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
            // Exclude JUnit related files
            excludes.add("META-INF/junit.properties")
            // Add more comprehensive exclusions
            excludes.add("META-INF/versions/**")
            excludes.add("META-INF/services/**")
        }
    }
}

dependencies {
    // Updated: Better approach to handle hamcrest conflict
    configurations.all {
        resolutionStrategy {
            // Force specific versions for problematic dependencies
            force("org.hamcrest:hamcrest-core:2.2")
            force("junit:junit:4.13.2")

            // Prevent duplicate Guava listenablefuture class issues
            force("com.google.guava:guava:31.1-jre")
            force("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

            // Force the latest ConstraintLayout versions
            force("androidx.constraintlayout:constraintlayout:2.2.1")
            force("androidx.constraintlayout:constraintlayout-compose:1.1.1")
        }
    }

    // JUnit with explicit exclusion
    testImplementation("junit:junit:4.13.2") {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }

    // Use hamcrest-all instead of just core to avoid version conflicts
    testImplementation("org.hamcrest:hamcrest:2.2")

    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.transport.api)
    implementation(libs.androidx.navigation.compose)

    // Updated ConstraintLayout dependencies with the latest versions
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.runtime.livedata)

    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.play.services.cast.framework)
    implementation(libs.testng)
    kapt("androidx.room:room-compiler:2.6.1")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Retrofit and network dependencies with explicit exclusions
    implementation("com.squareup.retrofit2:retrofit:2.9.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Fixed Guava dependency
    implementation("com.google.guava:guava:31.1-jre") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    // Use the empty listenablefuture to avoid conflicts
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    // Coroutines for network calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Test dependencies for API testing
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    // Android Test dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // LiveData Testing Dependencies
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Unit Testing Dependencies
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("io.mockk:mockk:1.13.8")

    // Android Testing Dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // Espresso Dependencies
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // Compose Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

    // Truth for assertions
    testImplementation("com.google.truth:truth:1.1.5")
    androidTestImplementation("com.google.truth:truth:1.1.5")

    // Robolectric for testing without device
    testImplementation("org.robolectric:robolectric:4.11.1")
}