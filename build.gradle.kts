plugins {
    val kotlinVersion = "2.0.21"
    kotlin("jvm") version kotlinVersion
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.s-class"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // 필수 의존성 (항상 포함)
    // ULID 생성 라이브러리
    implementation(libs.ulid.creator)

    // 선택적 의존성 (사용하는 서비스에서 제공)
    // Spring Data (PageResponse를 위해) - optional
    compileOnly(libs.spring.data.commons)

    // Spring Web (GlobalExceptionHandler를 위해) - optional
    compileOnly(libs.spring.web)

    // Spring WebMVC (HandlerInterceptor를 위해) - optional
    compileOnly(libs.spring.webmvc)

    // Spring AOP (로깅 AOP를 위해) - optional
    compileOnly(libs.spring.aop)
    compileOnly(libs.aspectj.weaver)

    // Spring Context (Component 어노테이션을 위해) - optional
    compileOnly(libs.spring.context)

    // Jakarta Servlet API (인터셉터를 위해) - optional
    compileOnly(libs.jakarta.servlet.api)

    // OpenAPI (Swagger 어노테이션을 위해) - optional
    compileOnly(libs.swagger.annotations)

    // SLF4J (로깅 유틸리티를 위해) - optional
    compileOnly(libs.slf4j.api)

    // 테스트 의존성
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.bundles.test.junit)
    testImplementation(libs.mockito.kotlin)
    // 테스트에서는 선택적 의존성도 필요
    testImplementation(libs.spring.data.commons)
    testImplementation(libs.spring.web)
    testImplementation(libs.spring.webmvc)
    testImplementation(libs.spring.aop)
    testImplementation(libs.spring.context)
    testImplementation(libs.jakarta.servlet.api)
    testImplementation(libs.aspectj.weaver)
    testImplementation(libs.slf4j.api)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.platform.suite.engine)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    version.set("0.50.0")
    debug.set(false)
    verbose.set(false)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

// JAR에 포함할 리소스 필터링
tasks.named<Jar>("jar") {
    // 예시 파일은 JAR에 포함하지 않음
    exclude("**/*.example")
    exclude("**/*.template")
}

// GitHub Packages 배포 설정
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            // JAR에 포함할 파일 필터링
            // 테스트 파일, 문서 파일 등은 자동으로 제외됨
            // src/main의 Kotlin 파일과 리소스만 포함

            pom {
                name.set("S-Class Common Kotlin Library")
                description.set("공통 유틸리티 및 도메인 모델을 제공하는 라이브러리")
                url.set("https://github.com/passion-edu/s-class-common")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                // 선택적 의존성을 optional로 표시
                // compileOnly로 선언된 의존성은 Maven POM에 포함되지 않으므로
                // 수동으로 optional dependency로 추가
                withXml {
                    val root = asNode()
                    // dependencies 노드 찾기 또는 생성
                    // root.get()은 NodeList를 반환할 수 있으므로 첫 번째 요소를 가져옴
                    val dependenciesList = root.get("dependencies")
                    val dependenciesNode: groovy.util.Node = when {
                        dependenciesList is groovy.util.NodeList && dependenciesList.size > 0 -> {
                            dependenciesList[0] as groovy.util.Node
                        }
                        dependenciesList is groovy.util.Node -> {
                            dependenciesList
                        }
                        else -> {
                            root.appendNode("dependencies")
                        }
                    }

                    // compileOnly 의존성을 optional로 추가
                    val optionalDeps = listOf(
                        Triple(
                            "org.springframework.data",
                            "spring-data-commons",
                            libs.versions.spring.data.commons.get(),
                        ),
                        Triple(
                            "org.springframework",
                            "spring-web",
                            libs.versions.spring.web.get(),
                        ),
                        Triple(
                            "org.springframework",
                            "spring-webmvc",
                            libs.versions.spring.webmvc.get(),
                        ),
                        Triple(
                            "org.springframework",
                            "spring-aop",
                            libs.versions.spring.aop.get(),
                        ),
                        Triple(
                            "org.springframework",
                            "spring-context",
                            libs.versions.spring.context.get(),
                        ),
                        Triple(
                            "jakarta.servlet",
                            "jakarta.servlet-api",
                            libs.versions.jakarta.servlet.api.get(),
                        ),
                        Triple("org.aspectj", "aspectjweaver", "1.9.20.1"),
                        Triple(
                            "io.swagger.core.v3",
                            "swagger-annotations",
                            libs.versions.swagger.annotations.get(),
                        ),
                        Triple(
                            "org.slf4j",
                            "slf4j-api",
                            libs.versions.slf4j.api.get(),
                        ),
                    )

                    optionalDeps.forEach { (groupId, artifactId, version) ->
                        val dep = dependenciesNode.appendNode("dependency")
                        dep.appendNode("groupId", groupId)
                        dep.appendNode("artifactId", artifactId)
                        dep.appendNode("version", version)
                        dep.appendNode("optional", "true")
                    }
                }
            }
        }
    }

    repositories {
        // GitHub Packages 배포 (환경 변수로 활성화)
        val githubPackagesUrl = project.findProperty("github.packages.url") as String?
        val githubToken = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")

        if (githubPackagesUrl != null && githubToken != null) {
            maven {
                name = "GitHubPackages"
                url = uri(githubPackagesUrl)
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR") ?: "github"
                    password = githubToken
                }
            }
        }
    }
}
