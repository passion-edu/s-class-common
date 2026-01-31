# 라이브러리 아키텍처 가이드

## 현재 구조: 단일 모듈

현재 `common-kotlin-lib`는 **단일 모듈 구조**로 되어 있습니다.

```
common-kotlin-lib/
├── src/main/kotlin/com/sclass/common/
│   ├── util/          # 유틸리티 (Ulid, PaginationUtils, DateTimeUtils, ValidationUtils, LoggerUtils)
│   ├── exception/   # 예외 처리 (BusinessException)
│   ├── dto/         # DTO (ApiResponse, ErrorResponse, PageResponse)
│   ├── domain/      # 도메인 모델 (UlidValueObject)
│   └── web/         # 웹 관련 (GlobalExceptionHandler)
└── build.gradle.kts
```

### 특징

- ✅ **단순함**: 하나의 JAR로 배포되어 관리가 쉬움
- ✅ **의존성 최소화**: 필요한 것만 선택적으로 포함
- ✅ **빠른 통합**: 서비스에서 한 번에 모든 유틸리티 사용 가능
- ❌ **독립 배포 불가**: 각 유틸리티를 개별적으로 배포할 수 없음
- ❌ **선택적 사용 제한**: 일부 유틸리티만 필요해도 전체 라이브러리를 포함해야 함

---

## 독립 배포 구조: 멀티 모듈

각 유틸리티를 독립적으로 배포하려면 **멀티 모듈 구조**로 변경해야 합니다.

### 구조 예시

```
common-kotlin-lib/
├── build.gradle.kts          # 루트 빌드 파일
├── settings.gradle.kts       # 모듈 정의
│
├── common-util-ulid/         # ULID 유틸리티 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-util-pagination/   # 페이징 유틸리티 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-util-datetime/     # 날짜/시간 유틸리티 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-util-validation/   # 검증 유틸리티 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-util-logging/      # 로깅 유틸리티 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-exception/         # 예외 처리 모듈
│   ├── build.gradle.kts
│   └── src/
│
├── common-dto/               # DTO 모듈
│   ├── build.gradle.kts
│   └── src/
│
└── common-web/               # 웹 관련 모듈
    ├── build.gradle.kts
    └── src/
```

### 멀티 모듈 설정 예시

#### `settings.gradle.kts`

```kotlin
rootProject.name = "common-kotlin-lib"

include(
    "common-util-ulid",
    "common-util-pagination",
    "common-util-datetime",
    "common-util-validation",
    "common-util-logging",
    "common-exception",
    "common-dto",
    "common-web"
)
```

#### 각 모듈의 `build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "com.s-class"
version = "1.0.0-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
```

### 멀티 모듈의 장단점

#### 장점

- ✅ **독립 배포**: 각 모듈을 개별적으로 버전 관리 및 배포 가능
- ✅ **선택적 사용**: 필요한 모듈만 의존성에 추가 가능
- ✅ **의존성 분리**: 각 모듈의 의존성을 독립적으로 관리
- ✅ **빌드 최적화**: 변경된 모듈만 빌드 가능

#### 단점

- ❌ **복잡도 증가**: 모듈 간 의존성 관리 필요
- ❌ **버전 관리 복잡**: 각 모듈의 버전을 개별적으로 관리해야 함
- ❌ **통합 테스트 어려움**: 모듈 간 통합 테스트가 복잡해짐
- ❌ **빌드 시간 증가**: 여러 모듈을 빌드해야 함

---

## 권장 사항

### 현재 구조 유지 (현재)

**이유:**
- 프로젝트 규모가 크지 않음
- 대부분의 서비스가 모든 유틸리티를 사용함
- 단일 모듈이 관리하기 더 쉬움
- 빌드 및 배포가 간단함

**사용 방법:**
```kotlin
// 모든 유틸리티를 한 번에 사용
dependencies {
    implementation("com.s-class:common-kotlin-lib:1.0.0")
}
```

### 멀티 모듈로 전환을 고려할 때

**조건:**
- 각 유틸리티의 사용 빈도가 크게 다를 때
- 일부 유틸리티만 필요한 서비스가 많을 때
- 각 모듈의 독립적인 버전 관리가 필요할 때
- 모듈 간 의존성이 명확히 분리될 때

**전환 시 고려사항:**
1. 모듈 간 의존성 정의 (예: `common-dto`는 `common-exception`에 의존)
2. 버전 관리 전략 (단일 버전 vs 개별 버전)
3. 통합 빌드 스크립트
4. 문서화 및 사용 가이드

---

## 하이브리드 접근법

필요한 경우 **하이브리드 접근법**도 고려할 수 있습니다:

### 구조

```
common-kotlin-lib/
├── common-core/          # 핵심 모듈 (Ulid, Exception)
├── common-util/         # 유틸리티 모듈 (Pagination, DateTime, Validation, Logging)
├── common-web/          # 웹 모듈 (DTO, GlobalExceptionHandler)
└── common-all/          # 전체 통합 모듈 (편의용)
```

### 사용 방법

```kotlin
// 핵심만 필요할 때
dependencies {
    implementation("com.s-class:common-core:1.0.0")
}

// 유틸리티만 필요할 때
dependencies {
    implementation("com.s-class:common-util:1.0.0")
}

// 전체가 필요할 때
dependencies {
    implementation("com.s-class:common-all:1.0.0")
}
```

---

## 결론

**현재는 단일 모듈 구조를 유지하는 것을 권장합니다.**

이유:
1. 프로젝트 규모에 적합함
2. 관리가 간단함
3. 대부분의 서비스가 모든 기능을 사용함
4. 필요시 나중에 멀티 모듈로 전환 가능

**멀티 모듈로 전환하는 시점:**
- 프로젝트가 더 커지고
- 각 유틸리티의 독립적인 버전 관리가 필요해지고
- 모듈별 사용 빈도가 크게 다를 때
