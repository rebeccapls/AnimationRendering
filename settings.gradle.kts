pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/release") }
		gradlePluginPortal()
	}
}

include(":RuneTek3")
rootProject.name = "rebecca-test"
