const val kotlinVersion = "1.3.41"

object BuildPlugins {
    object Versions {
        const val gradlePluginVersion = "3.5.0-rc02"
        const val safeArgs = "2.1.0-alpha04"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradlePluginVersion}"
    const val gradlePlugin = "gradle-plugin"
    const val safeArgsPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeArgs}"

    const val androidApplication = "com.android.application"
    const val android = "android"
    const val androidExtensions = "android.extensions"
    const val kapt = "kapt"
    const val safeArgs = "androidx.navigation.safeargs.kotlin"
}

object AndroidSdk {
    const val min = 24
    @Suppress("MemberVisibilityCanBePrivate")
    const val compile = 28
    const val target = compile
}

object Libraries {
    private object Versions {
        const val releaseBetanet = "release~1.0.0-beta.1-SNAPSHOT"

        const val ktlint = "0.33.0"
        const val ankoCommons = "0.10.8"
        const val appCompat = "1.1.0-alpha05"
        const val constraintLayout = "2.0.0-beta1"
        const val coreKtx = "1.1.0-beta01"
        const val navigationVersion = "2.1.0-alpha04"
        const val lifeCycleExtentions = "2.2.0-alpha01"
        const val material = "1.1.0-alpha07"
        const val glide = "4.9.0"
        const val playServicesVision = "17.0.2"
        const val qrGen = "2.5.0"
        const val rxAndroid = "2.1.1"
        const val rxKotlin = "2.3.0"
        const val timber = "4.7.1"
        const val lottie = "3.0.3"
        const val fragmentBack = "0.2.3"
    }

    const val ktlint = "com.pinterest:ktlint:${Versions.ktlint}"

    const val radixdltJava = "com.radixdlt:radixdlt-java:${Versions.releaseBetanet}"

    const val ankoCommons =  "org.jetbrains.anko:anko-commons:${Versions.ankoCommons}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val material = "com.google.android.material:material:${Versions.material}"

    const val lifeCycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleExtentions}"
    const val lifeCycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleExtentions}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
    const val fragmentBack = "net.skoumal.fragmentback:fragment-back:${Versions.fragmentBack}"

    const val playServicesVision = "com.google.android.gms:play-services-vision:${Versions.playServicesVision}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxKotlin =  "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val vault = "com.bottlerocketstudios:vault:1.4.2"

    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    const val qrGen = "com.github.kenglxn.QRGen:android:${Versions.qrGen}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
}

object TestLibraries {
    private object Versions {
        const val junit4 = "4.13-beta-3"
        const val runner = "1.2.0"
        const val espressoCore = "3.2.0"
    }

    const val junit4 = "junit:junit:${Versions.junit4}"
    const val runner = "androidx.test:runner:${Versions.runner}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
}
