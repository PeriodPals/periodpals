// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ktfmt) apply false
    kotlin("plugin.serialization") version "2.0.0-RC1" apply false
    alias(libs.plugins.compose.compiler) apply false
}
