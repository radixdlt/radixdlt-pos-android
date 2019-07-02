// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(BuildPlugins.androidGradlePlugin)
        classpath(kotlin(BuildPlugins.gradlePlugin, kotlinVersion))
        classpath(BuildPlugins.safeArgsPlugin)
    }
}

plugins {
    id("com.github.ben-manes.versions") version ("0.21.0")
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url ="https://jitpack.io")
    }
}

tasks.register("clean").configure {
    delete("build")
}
