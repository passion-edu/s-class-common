# 의존성 버전 관리

## 버전 정책

`common-kotlin-lib`는 최소한의 의존성만 포함하고, 선택적 의존성은 `compileOnly`로 선언하여 사용하는 서비스에서 제공하도록 합니다.

## 필수 의존성

다음 의존성은 항상 포함됩니다:

- `ulid-creator`: ULID 생성 라이브러리 (필수)

## 선택적 의존성

다음 의존성은 선택적이며, 사용하는 서비스에서 제공해야 합니다:

- `spring-data-commons`: `PageResponse` 사용 시 필요
- `spring-web`: `GlobalExceptionHandler` 사용 시 필요
- `swagger-annotations`: Swagger 어노테이션 사용 시 필요

## 버전 업데이트

버전은 `build.gradle.kts`의 `versions` 맵에서 중앙 관리됩니다:

```kotlin
val versions = mapOf(
    "ulid-creator" to "5.2.3",
    "spring-data-commons" to "3.2.0",
    "spring-web" to "6.1.5",
    "swagger-annotations" to "2.2.22",
    // ...
)
```

## 서비스에서 사용 시

서비스의 `build.gradle.kts`에서 필요한 의존성을 추가하세요:

```kotlin
dependencies {
    // 공통 라이브러리
    implementation("com.s-class:common-kotlin-lib:1.0.0-SNAPSHOT")
    
    // 선택적 의존성 (사용하는 기능에 따라)
    // PageResponse 사용 시
    implementation("org.springframework.data:spring-data-commons:3.2.0")
    
    // GlobalExceptionHandler 사용 시
    implementation("org.springframework:spring-web:6.1.5")
    // 또는 Spring Boot를 사용하는 경우
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Swagger 어노테이션 사용 시
    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
}
```

## 버전 호환성

- Spring Boot 3.5.8과 호환되는 버전 사용
- Spring Framework 6.1.x
- Spring Data 3.2.x
