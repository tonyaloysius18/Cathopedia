import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            // SQLDelight's native-driver links sqlite3 symbols at Kotlin/Native compile
            // time but doesn't embed the system library reference; without this the Xcode
            // link step fails with "symbol(s) not found for architecture arm64".
            linkerOpts.add("-lsqlite3")
        }
    }

    androidLibrary {
        namespace = "com.ynotlabs.cathopedia.shared"
        compileSdk = 36
        minSdk = 26

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }

        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.sqldelight.coroutines.extensions)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.requery.sqlite.android)
            implementation(libs.androidx.activity.compose)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.ynotlabs.cathopedia.resources"
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("CathopediaDatabase") {
            packageName.set("com.ynotlabs.cathopedia.db")
            dialect(libs.sqldelight.dialect)
        }
    }
}

// Content pipeline: /content is the human-edited source of truth (one JSON
// file per entity, see content/README.md). This task compiles it into the
// single bundle the app actually reads at runtime — every per-type file is
// already a valid standalone JSON object, so building the combined catalog
// is plain array/object assembly, no JSON parser needed at build time.
val compileContent by tasks.registering {
    group = "content"
    description = "Compiles /content into shared's bundled catalog.json resource."

    val contentDir = rootProject.layout.projectDirectory.dir("content")
    val outputFile = layout.projectDirectory.file("src/commonMain/composeResources/files/content/catalog.json")

    inputs.dir(contentDir)
    outputs.file(outputFile)

    doLast {
        val contentTypes = listOf("saints", "popes", "apostles", "churches", "apparitions", "miracles", "feasts")

        fun collectArray(typeDir: String): String {
            val dir = contentDir.dir(typeDir).asFile
            val files = dir.takeIf { it.exists() }
                ?.listFiles { f -> f.isFile && f.extension == "json" }
                ?.sortedBy { it.name }
                .orEmpty()
            return files.joinToString(separator = ",\n", prefix = "[\n", postfix = "\n]") { it.readText().trim() }
        }

        val relationsFile = contentDir.file("relations.json").asFile
        val relationsJson = if (relationsFile.exists()) relationsFile.readText().trim() else "[]"

        val catalog = buildString {
            append("{\n")
            contentTypes.forEach { type -> append("\"$type\": ${collectArray(type)},\n") }
            append("\"relations\": $relationsJson\n")
            append("}\n")
        }

        val out = outputFile.asFile
        out.parentFile.mkdirs()
        out.writeText(catalog)
    }
}

// Every task that reads from composeResources (one family per target: common/
// android/ios…) needs the compiled catalog in place first, or Gradle's task
// validation fails the build on an undeclared-dependency race. Broad name
// match rather than an exhaustive list, since Compose Resources' internal
// task set has changed across versions and will again.
tasks.matching { it.name.contains("Resources") }.configureEach {
    if (name != compileContent.name) dependsOn(compileContent)
}
