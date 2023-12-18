plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "project.finalterm.quizapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "project.finalterm.quizapp"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-ml-natural-language:22.0.0")
    implementation("com.google.firebase:firebase-ml-natural-language-language-id-model:20.0.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // https://mvnrepository.com/artifact/de.hdodenhof/circleimageview
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    // https://mvnrepository.com/artifact/com.jakewharton/process-phoenix
    implementation("com.jakewharton:process-phoenix:2.1.2")
    implementation("org.apache.poi:poi:5.2.4")
    implementation("org.apache.poi:poi-ooxml:5.2.4")
    implementation("androidx.core:core-splashscreen:1.0.0")
    // https://mvnrepository.com/artifact/com.google.zxing/core
    implementation("com.google.zxing:core:3.4.1")
    // https://mvnrepository.com/artifact/com.journeyapps/zxing-android-embedded
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")

}