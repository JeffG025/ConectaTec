// Forzar javapoet 1.13.0 en el classpath del buildscript (plugins de Hilt y AGP)
buildscript {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

// Forzar javapoet 1.13.0 en todos los subproyectos (annotation processors)
subprojects {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}
