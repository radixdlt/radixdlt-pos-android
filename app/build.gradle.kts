import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id(BuildPlugins.androidApplication)
    kotlin(BuildPlugins.android)
    kotlin(BuildPlugins.androidExtensions)
    kotlin(BuildPlugins.kapt)
    id(BuildPlugins.safeArgs)
}

android {
    compileSdkVersion(AndroidSdk.compile)
    defaultConfig {
        applicationId = "com.radixdlt.android.apps.pos"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            resValue("string", "app_name", "Radix Pay POS")
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions("normal")
    productFlavors {
        create("normal") {
            resValue("string", "app_name", "Radix Pay POS")
            setDimension("normal")
            minSdkVersion(AndroidSdk.min)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE-notice.md")
    }
}

val ktlint: Configuration by configurations.creating

tasks {

    check {
        dependsOn(ktlint)
    }

    create("ktlint", JavaExec::class) {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args("src/**/*.kt")
    }

    create("ktlintFormat", JavaExec::class) {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args("-F", "src/**/*.kt")
    }
}

dependencies {
    ktlint(Libraries.ktlint)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Libraries.radixdltJava)

    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(Libraries.ankoCommons)
    implementation(Libraries.appCompat)
    implementation(Libraries.material)
    implementation(Libraries.ktxCore)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.navigationFragmentKtx)
    implementation(Libraries.navigationUiKtx)
    implementation(Libraries.lifeCycleExtensions)
    implementation(Libraries.lifeCycleRuntimeKtx)
    implementation(Libraries.timber)
    implementation(Libraries.material)
    implementation(Libraries.rxAndroid)
    implementation(Libraries.rxKotlin) {
        exclude("io.reactivex.rxjava2", "rxjava")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }

    implementation(Libraries.vault)
    implementation(Libraries.lottie)

    implementation(Libraries.playServicesVision)

    implementation(Libraries.fragmentBack)
    implementation(Libraries.qrGen)

    implementation(Libraries.glide)
    kapt(Libraries.glideCompiler)

    // Testing dependencies
    testImplementation(TestLibraries.junit4)
    androidTestImplementation(TestLibraries.runner)
    androidTestImplementation(TestLibraries.espressoCore)
}
