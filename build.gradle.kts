plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-amqp") // RabbitMQ
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
	implementation("org.mongodb:mongodb-driver-sync:5.1.4")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation ("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.2.RELEASE")
	testImplementation ("org.springframework.security:spring-security-test")
	implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("org.springframework.boot:spring-boot-starter-data-mongodb")
	runtimeOnly ("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
