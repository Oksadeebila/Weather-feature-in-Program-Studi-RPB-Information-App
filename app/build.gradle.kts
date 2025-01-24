plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fteknik"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fteknik"
        minSdk = 24
        targetSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("androidx.cardview:cardview:1.0.0")
    //---Lottie------//
    implementation("com.airbnb.android:lottie:6.4.1")
    //---Location----//
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Place Name
    implementation("com.google.android.gms:play-services-places:17.0.0")

    //---- Volley Library
    implementation("com.android.volley:volley:1.2.1")
}