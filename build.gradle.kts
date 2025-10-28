import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    androidLibrary {
        namespace = "com.mocharealm.gaze.capsule"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        minSdk = libs.versions.android.minSdk.get().toInt()


        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "GazeCapsule"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true
        )
    )

    signAllPublications()

    coordinates("com.mocharealm.gaze", "capsule", "2.1.1-patch2")

    pom {
        name = "Gaze Capsule"
        description = "Compose Multiplatform G2 continuous rounded rectangles"
        inceptionYear = "2025"
        url = "https://mocharealm.com/open-source"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "6xingyv"
                name = "Simon Scholz"
                url = "https://github.com/6xingyv"
            }
        }
        scm {
            url = "https://github.com/6xingyv/gaze-capsule"
            connection = "scm:git:git://github.com/6xingyv/gaze-capsule.git"
            developerConnection = "scm:git:ssh://git@github.com/6xingyv/gaze-capsule.git"
        }
    }
}