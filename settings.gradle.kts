pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Ajout pour le plugin Realm
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    // Déclarez les plugins ici
    plugins {
        // Vos autres plugins
        id("io.realm.kotlin") version "1.12.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Ajout pour les dépendances Realm
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

rootProject.name = "Client JetPack"
include(":app")
