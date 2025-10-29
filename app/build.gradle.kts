plugins {
    alias(libs.plugins.android.application)

//    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.computerselling"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.computerselling"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    implementation("com.google.firebase:firebase-analytics")

    // CÁC THƯ VIỆN BẮT BUỘC CHO FIREBASE VÀ GLIDE

    implementation("com.google.firebase:firebase-analytics")

    // THÊM: Firebase Storage (để lấy URL ảnh và tải ảnh lên)
    implementation("com.google.firebase:firebase-storage")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // THÊM: GLIDE (Thư viện tải và hiển thị ảnh từ URL)
//    implementation 'com.github.bumptech.glide:glide:4.16.0'
//    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
}