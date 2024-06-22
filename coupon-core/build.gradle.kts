val bootJar: org.springframework.boot.gradle.tasks.bundling.BootJar by tasks

bootJar.enabled = false

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.redisson:redisson-spring-boot-starter:3.31.0")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.github.ben-manes.caffeine:caffeine")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
