pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // 카카오 SDK만 제한적으로 받기
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/")
            content {
                includeGroup("com.kakao.sdk")
                includeGroup("com.kakao.maps.open")

            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 카카오 SDK만 제한적으로 받기
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/")
            content {
                includeGroup("com.kakao.sdk")
                includeGroup("com.kakao.maps.open")
            }
        }
    }
}

rootProject.name = "Muaring"
include(":app")
include(":core")
include(":data")
include(":design")
include(":feature")
