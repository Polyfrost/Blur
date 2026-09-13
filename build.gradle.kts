import java.nio.file.Files
import java.security.MessageDigest

plugins {
	java
	id("net.fabricmc.fabric-loom-remap") version "1.17.+"
	id("ploceus") version "1.17.+"
}

group = "eu.midnightdust.blur"
version = "6.3.2+mc1.8.9-fabric"
base.archivesName = "LegacyGuiBlur"

repositories {
	maven("https://moehreag.duckdns.org/maven/releases")
	mavenCentral()
}

val oneConfigCommit = "077ff616455e3b67d1a117feb5d9d25e7d57a2ef"
val oneConfigWorkflowRun = "33715707534"
val oneConfigArtifact = "9878636690"
val oneConfigArtifactSha256 = "c419e36c813cbefc493e311f57c1b40953a110df6348a5e572aa4eb01978b1e0"
val oneConfigVersion = "1.1.12"
val oneConfigDirectory = layout.buildDirectory.dir("oneconfig")
val oneConfigBootstrap = oneConfigDirectory.map { it.file("OneConfig-1.8.9-ornithe-$oneConfigVersion.jar") }
val oneConfigCompileDirectory = oneConfigDirectory.map { it.dir("compile") }

val downloadOneConfig = tasks.register("downloadOneConfig") {
	group = "setup"
	description = "Downloads the verified OneConfig Fabric (Ornithe) 1.8.9 CI artifact using GitHub CLI."
	inputs.property("oneConfigCommit", oneConfigCommit)
	inputs.property("oneConfigWorkflowRun", oneConfigWorkflowRun)
	inputs.property("oneConfigArtifact", oneConfigArtifact)
	inputs.property("oneConfigArtifactSha256", oneConfigArtifactSha256)
	outputs.file(oneConfigBootstrap)
	outputs.dir(oneConfigCompileDirectory)

	doLast {
		val directory = oneConfigDirectory.get().asFile
		val bootstrap = oneConfigBootstrap.get().asFile
		val compileDirectory = oneConfigCompileDirectory.get().asFile
		directory.mkdirs()

		if (!bootstrap.isFile) {
			val archive = directory.resolve("oneconfig-$oneConfigCommit-bootstrap.zip")
			if (!archive.isFile) {
				val process = ProcessBuilder(
					"gh", "api", "--method", "GET",
					"-H", "Accept: application/vnd.github+json",
					"/repos/Polyfrost/OneConfig/actions/artifacts/$oneConfigArtifact/zip"
				)
					.redirectOutput(archive)
					.redirectError(ProcessBuilder.Redirect.INHERIT)
					.start()
				check(process.waitFor() == 0) { "GitHub CLI could not download the OneConfig artifact; run 'gh auth login' and retry" }
			}

			val hash = MessageDigest.getInstance("SHA-256")
				.digest(Files.readAllBytes(archive.toPath()))
				.joinToString("") { "%02x".format(it) }
			check(hash == oneConfigArtifactSha256) {
				"OneConfig artifact checksum mismatch: expected $oneConfigArtifactSha256, got $hash"
			}

			copy {
				from(zipTree(archive))
				include("1.8.9-ornithe/build/libs/OneConfig-1.8.9-ornithe-$oneConfigVersion.jar")
				into(directory)
				eachFile { path = bootstrap.name }
				includeEmptyDirs = false
			}
			check(bootstrap.isFile) { "OneConfig 1.8.9 Fabric (Ornithe) bootstrap JAR was not found in the verified artifact" }
		}

		if (!compileDirectory.isDirectory || compileDirectory.listFiles().isNullOrEmpty()) {
			delete(compileDirectory)
			copy {
				from(zipTree(bootstrap))
				include("META-INF/jars/*.jar")
				into(compileDirectory)
				eachFile { path = name }
				includeEmptyDirs = false
			}
		}
	}
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
	compileOnly(files(fileTree(oneConfigCompileDirectory) { include("*.jar") }))
}

tasks.named<JavaCompile>("compileJava") {
	dependsOn(downloadOneConfig)
}

tasks.matching { it.name == "remapSourcesJar" }.configureEach {
	dependsOn(downloadOneConfig)
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
