plugins {
	java
	id("net.fabricmc.fabric-loom-remap") version "1.17.+"
	id("ploceus") version "1.17.+"
}

group = "eu.midnightdust.blur"
version = "6.3.3+mc1.8.9-fabric"
base.archivesName = "LegacyGuiBlur"

val oneConfigVersion = "1.2.3"

repositories {
	maven("https://moehreag.duckdns.org/maven/releases")
	maven("https://repo.polyfrost.org/releases")
	mavenCentral()
}

ploceus {
	setIntermediaryGeneration(2)
}

loom {
	mods {
		create("blur") {
			sourceSet("main")
		}
	}
	runs {
		remove(getByName("server"))
	}
}

dependencies {
	minecraft("com.mojang:minecraft:1.8.9")
	mappings(ploceus.featherMappings("1"))

	modImplementation("net.fabricmc:fabric-loader:0.19.3")
	ploceus.dependOsl("0.20.3")
	modImplementation("com.terraformersmc:modmenu:0.5.0+mc1.8.9")
	// OneConfig publishes Java 21 variant metadata; the bytecode itself is fine on the 17 classpath.
	listOf("config", "config-impl").forEach { module ->
		components.withModule("org.polyfrost.oneconfig:$module") {
			allVariants {
				attributes { attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17) }
			}
		}
		compileOnly("org.polyfrost.oneconfig:$module:$oneConfigVersion") { isTransitive = false }
	}
}

tasks.processResources {
	inputs.property("version", version)
	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.release = 17
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	withSourcesJar()
}
