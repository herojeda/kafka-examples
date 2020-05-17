import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.mutableListOf

plugins {
    kotlin("jvm") version "1.3.72"
    id("application")
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "org.hojeda"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.hojeda.kafka.consumer.rx.RxConsumerEntrypointKt"
}

val vertxVersion = "3.8.5"
val mainClassName = "com.hojeda.kafka.consumer.rx.RxConsumerEntrypointKt"
val mainVerticleName = "com.hojeda.kafka.consumer.rx.vertx.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.vertx:vertx-rx-java2:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-kafka-client:$vertxVersion")

    implementation("org.apache.kafka:kafka-clients:2.5.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
    args = mutableListOf(
        "run", mainVerticleName,
        "--redeploy=$watchForChange",
        "--launcher-class=$mainClassName",
        "--on-redeploy=$doOnChange"
    )
}