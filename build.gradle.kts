// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url="https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        //classpath("com.google.gms:google-services:4.3.10")
        //classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        //classpath("com.google.dagger:hilt-android-gradle-plugin:2.37")
        //classpath("org.jlleitschuh.gradle:ktlint-gradle:10.1.0")
        //classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        //classpath(Dependency.GradlePlugin.build)
        classpath(Dependency.GradlePlugin.googleService)
        classpath(Dependency.GradlePlugin.kotlin)
        classpath(Dependency.GradlePlugin.hilt)
        classpath(Dependency.GradlePlugin.ktLint)
        classpath(Dependency.GradlePlugin.crashlytics)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}
repositories {
    mavenCentral()
}
/*check {
    dependsOn "installKotlinterPrePushHook"
}*/
allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url="https://jitpack.io")
    }
}

/*task clean(type: Delete) {
    delete rootProject.buildDir
}*/
task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}