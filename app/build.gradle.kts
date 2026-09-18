plugins{alias(libs.plugins.android.application)}

android{
    namespace="com.app.recipick"
    compileSdk=35

    defaultConfig{
        applicationId="com.app.recipick"
        minSdk=24
        targetSdk=35
        versionCode=1
        versionName="1.0"
        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions{annotationProcessorOptions{arguments["room.schemaLocation"]="$projectDir/schemas"}}
    }

    buildTypes{
        release{
            isMinifyEnabled=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions{
        sourceCompatibility=JavaVersion.VERSION_11
        targetCompatibility=JavaVersion.VERSION_11
    }
}

dependencies{
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.room.runtime)
    implementation(libs.room.common)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.room.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}















