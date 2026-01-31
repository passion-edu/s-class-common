# S-Class Common Kotlin Library

공통 유틸리티 및 도메인 모델을 제공하는 Kotlin 라이브러리입니다.

## 📦 설치

### GitHub Packages에서 설치

```kotlin
repositories {
    mavenCentral()
    
    // GitHub Packages (passion-edu/s-class-common)
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/passion-edu/s-class-common")
        credentials {
            username = project.findProperty("gpr.user") as String? 
                ?: System.getenv("GITHUB_ACTOR") ?: "github"
            password = project.findProperty("gpr.token") as String? 
                ?: System.getenv("GITHUB_TOKEN") ?: ""
        }
    }
}

dependencies {
    implementation("com.s-class:common-kotlin-lib:1.0.0")
}
```

### 인증 설정

로컬 개발 환경:

```bash
export GITHUB_TOKEN=your_github_token
export GITHUB_ACTOR=your_github_username
```

또는 `~/.gradle/gradle.properties`:

```properties
gpr.user=your_github_username
gpr.token=your_github_token
```

## 📚 주요 기능

### 유틸리티

- **ULID**: ULID 생성 및 검증
- **PaginationUtils**: 페이지네이션 파라미터 정규화
- **DateTimeUtils**: 날짜/시간 변환 유틸리티
- **ValidationUtils**: 공통 검증 함수

### DTO

- **ApiResponse**: 표준화된 API 응답 래퍼
- **PageResponse**: 페이지네이션 응답 DTO
- **ErrorResponse**: 에러 응답 DTO

### 예외 처리

- **BusinessException**: 비즈니스 로직 예외
- **GlobalExceptionHandler**: 전역 예외 핸들러

### 로깅

- **LoggerUtils**: 로거 생성 유틸리티
- **LoggerExtensions**: 구조화된 로깅 확장 함수
- **@Loggable**: AOP 기반 메서드 로깅
- **LoggingInterceptor**: HTTP 요청/응답 로깅

## 📖 문서

- [배포 가이드](DEPLOYMENT.md)
- [GitHub Packages 설정](GITHUB_PACKAGES_SETUP.md)
- [로깅 가이드](LOGGING_GUIDE.md)
- [아키텍처](ARCHITECTURE.md)
- [성능 최적화](PERFORMANCE_OPTIMIZATION.md)
- [릴리스 체크리스트](RELEASE_CHECKLIST.md)

## 🔗 링크

- **GitHub Repository**: [passion-edu/s-class-common](https://github.com/passion-edu/s-class-common)
- **GitHub Packages**: [maven.pkg.github.com/passion-edu/s-class-common](https://maven.pkg.github.com/passion-edu/s-class-common)

## 📝 라이선스

Apache License 2.0
