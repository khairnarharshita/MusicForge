plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.music_artist_collab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.music_artist_collab"
        minSdk = 19
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding =true;
    }
}

dependencies {
//    video

    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.legacy:legacy-support-core-ui:1.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.3.0")
    implementation ("com.google.android.material:material:1.10.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0-alpha01")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}