// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url="https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath(Dependency.GradlePlugin.build)
        classpath(Dependency.GradlePlugin.googleService)
        classpath(Dependency.GradlePlugin.kotlin)
        classpath(Dependency.GradlePlugin.hilt)
        classpath(Dependency.GradlePlugin.ktLint)
        classpath(Dependency.GradlePlugin.crashlytics)
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