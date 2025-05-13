import org.gradle.testing.jacoco.tasks.JacocoReport

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "coverage"
    description = "Generates JaCoCo coverage report for the test task."

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