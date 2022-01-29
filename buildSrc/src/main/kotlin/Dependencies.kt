object Dependency {
    object GradlePlugin {
        const val build = "com.android.tools.build:gradle:${Versions.buildGradle}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinGradle}"
        const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltGradle}"
        const val ktLint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktLintGradle}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradle}"
    }


}