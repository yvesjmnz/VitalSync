plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Google services plugin for Firebase
}

android {
    namespace = "com.example.vitalsync"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vitalsync"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase BOM - automatically manages versions for all Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    implementation ("com.google.code.gson:gson:2.8.9")


    // Firebase and Google Sign-In dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-auth")

    //firebase database
    implementation("com.google.firebase:firebase-database:20.0.6")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //receiver
    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")

    // Other dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}

// Apply the Google services plugin
apply(plugin = "com.google.gms.google-services")
