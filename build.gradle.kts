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
	maven("https://maven.cloverclient.com/releases") {
		content { includeGroup("pl.tomgirl") }
	}
	mavenCentral()
	google()
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
	modImplementation("org.polyfrost.oneconfig:1.8.9-ornithe:$oneConfigVersion")
	for (module in arrayOf("commands", "config", "config-impl", "events", "internal", "ui", "utils", "hud")) {
		implementation("org.polyfrost.oneconfig:$module:$oneConfigVersion")
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
	options.release = 25
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
	withSourcesJar()
}
