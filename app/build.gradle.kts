plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sonar)
    id("jacoco")
}

android {
    namespace = "com.eldirohmanur.photogram"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eldirohmanur.photogram"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all {
                it.extensions.configure(JacocoTaskExtension::class.java) {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    lint {
        checkTestSources = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    sonar {
        properties {
            property("sonar.projectKey", "rohmanureldi_PhotoGram")
            property("sonar.organization", "rohmanureldi")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**", // Typically exclude DI modules
        "**/*_Factory.*", // Dagger generated code
        "**/*_MembersInjector.*", // Dagger generated code
        "**/*_*Factory.*", // Dagger generated code
        "**/generated/**",
        "**/*_Provide*Factory*.*"
    )

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(arrayOf(mainSrc)))
    classDirectories.setFrom(
        files(
            arrayOf(
                fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
                    exclude(fileFilter)
                }
            )
        )
    )

    executionData.setFrom(files(arrayOf("${project.layout.buildDirectory.get()}/jacoco/testDebugUnitTest.exec")))
}

tasks.named("check") {
    finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = "0.8.10" // Use the latest stable version
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)


    // Coil
    implementation(libs.coil.compose)
    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Unit Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.turbine)
    testImplementation(libs.junit)

    // UI Test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)


    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}