import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
  // supabase setup
  kotlin("plugin.serialization") version "2.0.0-RC1"
  alias(libs.plugins.jetbrainsKotlinAndroid)
  alias(libs.plugins.ktfmt)
  // alias(libs.plugins.sonar)
  alias(libs.plugins.compose.compiler)

  id("com.android.application")
  id("com.google.gms.google-services")
  id("kotlin-android")
  id("jacoco")
  id("org.sonarqube") version "5.1.0.4882"
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
  namespace = "com.android.periodpals"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.android.periodpals"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildFeatures { buildConfig = true }
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    debug {
      enableUnitTestCoverage = true
      enableAndroidTestCoverage = true
    }
  }

  testCoverage { jacocoVersion = "0.8.8" }

  buildFeatures { compose = true }

  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions { jvmTarget = "11" }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      merges += "META-INF/LICENSE.md"
      merges += "META-INF/LICENSE-notice.md"
      excludes += "META-INF/LICENSE-notice.md"
      excludes += "META-INF/LICENSE.md"
      excludes += "META-INF/LICENSE"
      excludes += "META-INF/LICENSE.txt"
      excludes += "META-INF/NOTICE"
      excludes += "META-INF/NOTICE.txt"
    }
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
    }
    packagingOptions { jniLibs { useLegacyPackaging = true } }
  }

  // Robolectric needs to be run only in debug. But its tests are placed in the shared source set
  // (test)
  // The next lines transfers the src/test/* from shared to the testDebug one
  //
  // This prevent errors from occurring during unit tests
  sourceSets.getByName("testDebug") {
    val test = sourceSets.getByName("test")

    java.setSrcDirs(test.java.srcDirs)
    res.setSrcDirs(test.res.srcDirs)
    resources.setSrcDirs(test.resources.srcDirs)
  }

  sourceSets.getByName("test") {
    java.setSrcDirs(emptyList<File>())
    res.setSrcDirs(emptyList<File>())
    resources.setSrcDirs(emptyList<File>())
  }
}

sonar {
  properties {
    property("sonar.projectKey", "PeriodPals_periodpals")
    property("sonar.organization", "periodpals-1")
    property("sonar.host.url", "https://sonarcloud.io")
    // Comma-separated paths to the various directories containing the *.xml JUnit report files.
    // Each path may be absolute or relative to the project base directory.
    property(
        "sonar.junit.reportPaths",
        "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/",
    )
    // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will
    // have to be changed too.
    property(
        "sonar.androidLint.reportPaths",
        "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml",
    )
    // Paths to JaCoCo XML coverage report files.
    property(
        "sonar.coverage.jacoco.xmlReportPaths",
        "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml",
    )
  }
}

// When a library is used both by robolectric and connected tests, use this function
fun DependencyHandlerScope.globalTestImplementation(dep: Any) {
  androidTestImplementation(dep)
  testImplementation(dep)
  testImplementation(libs.robolectric)
  testImplementation("org.mockito:mockito-core:4.0.0")
}

dependencies {
  //// Core
  // implementation(libs.core.ktx)
  // implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.lifecycle.runtime.ktx)
  // implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.appcompat)
  // implementation(libs.androidx.constraintlayout)
  // implementation(libs.androidx.fragment.ktx)
  // implementation(libs.kotlinx.serialization.json)

  // form validation
  implementation(libs.form.builder)

  // credentials
  implementation("androidx.credentials:credentials:1.3.0-alpha01")
  implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha01")
  implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.messaging.ktx)
  implementation(libs.play.services.base)

  implementation(libs.androidx.activity.ktx)
  implementation(libs.androidx.fragment.ktx.v184)
  implementation(libs.compose)
  implementation(libs.mockk.v1120)
  implementation(libs.androidx.ui.test.junit4.v105)
  implementation(libs.androidx.ui.test.manifest.v105)

  configurations.all {
    resolutionStrategy {
      force("androidx.test.ext:junit:1.1.5")
      force("androidx.test.espresso:espresso-core:3.5.0")
    }
  }
  // supabase setup
  implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
  implementation(libs.github.postgrest.kt)
  implementation(libs.ktor.client.android.v300rc1)
  implementation(libs.supabase.postgrest.kt)
  implementation(libs.auth.kt)
  implementation(libs.realtime.kt)
  implementation(libs.ktor.client.android.v300rc1)
  implementation(libs.kotlinx.serialization.json.v162)
  implementation(libs.storage.kt)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(platform(libs.compose.bom))
  implementation(libs.androidx.navigation.compose.v282)
  implementation(libs.androidx.espresso.intents)
  implementation(libs.androidx.espresso.core)

  testImplementation(libs.junit)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.robolectric)
  globalTestImplementation(libs.androidx.junit)
  globalTestImplementation(libs.androidx.espresso.core)

  // ------------- Jetpack Compose ------------------
  val composeBom = platform(libs.compose.bom)
  implementation(composeBom)
  globalTestImplementation(composeBom)

  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  // Material Design 3
  implementation(libs.compose.material3)
  // Integration with activities
  implementation(libs.compose.activity)
  // Integration with ViewModels
  implementation(libs.compose.viewmodel)
  // Android Studio Preview support
  implementation(libs.compose.preview)
  debugImplementation(libs.compose.tooling)
  // UI Tests
  globalTestImplementation(libs.compose.test.junit)
  debugImplementation(libs.compose.test.manifest)

  // --------- Kaspresso test framework ----------
  globalTestImplementation(libs.kaspresso)
  globalTestImplementation(libs.kaspresso.compose)

  // ----------       Robolectric     ------------
  testImplementation(libs.robolectric)

  // Material Icons
  implementation(libs.androidx.material.icons.extended)

  /// Mockito for android testing
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.mockito.kotlin)
  androidTestImplementation(libs.dexmaker.mockito.inline)

  // Mockito for unit testing
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.core)

  // testImplementation(libs.mockito.core.v540)
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

  // OpenStreetMap (osmdroid) dependency
  implementation("org.osmdroid:osmdroid-android:6.1.13")
  // Location Services
  implementation("com.google.android.gms:play-services-location:21.0.1")
  // Networking with OkHttp
  implementation(libs.okhttp)

  // mockEngine
  testImplementation(libs.ktor.client.mock)

  // Window Size Class
  implementation("androidx.compose.material3:material3-window-size-class:1.3.0")

  // Live Data
  implementation(libs.androidx.runtime.livedata)
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "secrets.defaults.properties"
}

tasks.withType<Test> {
  // Configure Jacoco for each tests
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
  systemProperty("robolectric.logging", "stdout")
}

tasks.register("jacocoTestReport", JacocoReport::class) {
  mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

  reports {
    xml.required = true
    html.required = true
  }

  val fileFilter =
      listOf(
          "**/R.class",
          "**/R$*.class",
          "**/BuildConfig.*",
          "**/Manifest*.*",
          "**/*Test*.*",
          "android/**/*.*",
      )

  val debugTree =
      fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
      }

  val mainSrc = "${project.layout.projectDirectory}/src/main/java"
  sourceDirectories.setFrom(files(mainSrc))
  classDirectories.setFrom(files(debugTree))
  executionData.setFrom(
      fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
      })
}

// Avoid redundant tests, debug (with Robolectric) is sufficient
tasks.withType<Test> { onlyIf { !name.toLowerCaseAsciiOnly().contains("release") } }
