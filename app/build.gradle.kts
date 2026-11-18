import java.io.StringReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Read only at build time; .env is excluded from version control and APK assets.
// fileContents lets Gradle invalidate the configuration cache when .env changes.
val localBooksApiKey = providers.fileContents(rootProject.layout.projectDirectory.file(".env"))
    .asText.map { contents ->
        Properties().apply {
            load(StringReader(contents.removePrefix("\uFEFF")))
        }.getProperty("BOOKS_API_KEY", "").trim().removeSurrounding("\"").removeSurrounding("'")
    }

android {
    namespace = "com.example.appbookshelf"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appbookshelf"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        val booksApiKey = providers.gradleProperty("BOOKS_API_KEY")
            .orElse(providers.environmentVariable("BOOKS_API_KEY"))
            .orElse(localBooksApiKey)
            .getOrElse("")
        buildConfigField("String", "BOOKS_API_KEY", "\"${booksApiKey.replace("\\", "\\\\").replace("\"", "\\\"")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
