plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("org.jlleitschuh.gradle.ktlint")
    id("kotlin-android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "com.example.todolist"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }


}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")

    // gson - gson encoder, decoder - https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.8.9") // 오픈소스라이선스 출처등록 O

    // Retrofit2 - Http Communication Library, gson 컨버터 - https://square.github.io/retrofit/
    implementation("com.squareup.retrofit2:retrofit:2.9.0")  // 오픈소스라이선스 출처등록 O
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")  // 오픈소스라이선스 출처등록 O
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")  // 오픈소스라이선스 출처등록 O
    implementation("com.squareup.retrofit2:adapter-rxjava:2.9.0")  // 오픈소스라이선스 출처등록 O

    // Glide - Image Loader - https://github.com/bumptech/glide
    implementation("com.github.bumptech.glide:glide:4.12.0")  // 오픈소스라이선스 출처등록 O
    kapt("com.github.bumptech.glide:compiler:4.12.0")  // 오픈소스라이선스 출처등록 O
    kapt("com.github.bumptech.glide:glide:4.12.0")  // 오픈소스라이선스 출처등록 O

    //Coroutine - Thread for Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // AAC - AAC에 사용됨
    implementation("androidx.fragment:fragment-ktx:1.4.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    // MpAndroidChart - Chart Library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // 오픈소스라이선스 출처등록 O

    // RxJava, RxAndroid, RxLifeCycle
    implementation("io.reactivex.rxjava3:rxjava:3.1.3") // 오픈소스라이선스 출처등록 해야함
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0") // 오픈소스라이선스 출처등록 해야함
    implementation("com.trello.rxlifecycle2:rxlifecycle:2.2.2") // 오픈소스라이선스 출처등록 해야함
    implementation("com.trello.rxlifecycle2:rxlifecycle-android:2.2.2") // 오픈소스라이선스 출처등록 해야함
    implementation("com.trello.rxlifecycle2:rxlifecycle-components:2.2.2") // 오픈소스라이선스 출처등록 해야함


    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.40.5") // 오픈소스라이선스 출처등록 O
    kapt("com.google.dagger:hilt-android-compiler:2.40.5") // 오픈소스라이선스 출처등록 O

    // 점수표현 - https://github.com/YvesCheung/RollingText
    implementation("com.github.YvesCheung:RollingText:1.2.0")// 오픈소스라이선스 출처등록 O
    // Play Asset Delivery
    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:core-ktx:1.8.1")

    // Facebook Shimmer - http://facebook.github.io/shimmer-android/
    implementation("com.facebook.shimmer:shimmer:0.5.0")// 오픈소스라이선스 출처등록 O

    // ShowCaseView - https://github.com/mreram/ShowCaseView
    implementation("com.github.mreram:showcaseview:1.2.0")

    // ShowCaseView v2 - https://github.com/erkutaras/ShowcaseView
    implementation("com.github.erkutaras:ShowcaseView:1.5.0")

    // Particle v2 - https://github.com/DanielMartinus/Konfetti
    implementation("nl.dionsegijn:konfetti:1.3.2")

    // ViewPager Dot Indicator - https://github.com/tommybuonomo/dotsindicator
    implementation("com.tbuonomo:dotsindicator:4.2")

    // RecyclerView Dot Indicator - https://github.com/TinkoffCreditSystems/ScrollingPagerIndicator
    implementation("ru.tinkoff.scrollingpagerindicator:scrollingpagerindicator:1.2.1")

    // Lottie Animation - https://github.com/airbnb/lottie-android
    implementation("com.airbnb.android:lottie:4.2.2")

    // https://github.com/zhaolei9527/Particle-master
    implementation("com.github.zhaolei9527:Particle-master:v1.0.1")

    // https://github.com/google/flexbox-layout
    implementation("com.google.android.flexbox:flexbox:3.0.0")


// Integration with activities
    implementation ("androidx.activity:activity-compose:1.4.0")
    // Compose Material Design
    implementation ("androidx.compose.material:material:1.0.5")
    // Animations
    implementation ("androidx.compose.animation:animation:1.0.5")
    // Tooling support (Previews, etc.)
    implementation ("androidx.compose.ui:ui-tooling:1.0.5")
    // Integration with ViewModels
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.0.5")


    implementation("androidx.compose.ui:ui:1.0.5")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.5")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.5")
    // Material Design
    implementation("androidx.compose.material:material:1.0.5")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.5")
    implementation("androidx.compose.material:material-icons-extended:1.0.5")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.5")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.5")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")


    // When using a MDC theme
    implementation("com.google.android.material:compose-theme-adapter:1.1.3")

    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.16.0")

    implementation ("com.google.accompanist:accompanist-insets:0.24.1-alpha")
    // If using insets-ui
    implementation ("com.google.accompanist:accompanist-insets-ui:0.24.1-alpha")


}

apply(mapOf("plugin" to "com.google.firebase.crashlytics"))
apply(mapOf("plugin" to "dagger.hilt.android.plugin"))