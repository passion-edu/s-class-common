# 빌드 결과물 구성

## 📦 배포되는 파일

### 1. 메인 JAR 파일
- **파일명**: `common-kotlin-lib-{version}.jar`
- **포함 내용**:
  - `src/main/kotlin/`의 모든 Kotlin 소스 파일 (컴파일된 .class)
  - `src/main/resources/`의 리소스 파일
- **제외 내용**:
  - 테스트 파일 (`src/test/`)
  - 문서 파일 (`.md`)
  - 빌드 스크립트 (`build.gradle.kts`, `settings.gradle.kts`)
  - 스크립트 파일 (`.sh`)

### 2. Sources JAR
- **파일명**: `common-kotlin-lib-{version}-sources.jar`
- **포함 내용**: `src/main/kotlin/`의 원본 Kotlin 소스 파일

### 3. Javadoc JAR
- **파일명**: `common-kotlin-lib-{version}-javadoc.jar`
- **포함 내용**: Kotlin 문서 주석에서 생성된 Javadoc

### 4. POM 파일
- **파일명**: `common-kotlin-lib-{version}.pom`
- **포함 내용**:
  - 메타데이터 (그룹, 아티팩트, 버전)
  - 의존성 정보
  - 선택적 의존성 표시 (`optional=true`)

## 📁 포함되는 소스 파일

### Kotlin 소스 파일
```
src/main/kotlin/com/sclass/common/
├── domain/
│   └── UlidValueObjectCompanion.kt
├── dto/
│   ├── ApiResponse.kt
│   └── PageResponse.kt
├── exception/
│   └── BusinessException.kt
├── util/
│   ├── DateTimeUtils.kt
│   ├── LoggerExtensions.kt
│   ├── LoggerUtils.kt
│   ├── PaginationUtils.kt
│   ├── Ulid.kt
│   └── ValidationUtils.kt
└── web/
    ├── annotation/
    │   └── Loggable.kt
    ├── aspect/
    │   ├── AsyncLoggingAspect.kt
    │   └── LoggingAspect.kt
    ├── GlobalExceptionHandler.kt
    └── interceptor/
        ├── AsyncLoggingInterceptor.kt
        └── LoggingInterceptor.kt
```

### 리소스 파일
```
src/main/resources/
└── logback-spring.xml.example  # 예시 파일 (참고용)
```

## 🚫 제외되는 파일

### 테스트 파일
- `src/test/` 디렉토리 전체
- 테스트 의존성 (`testImplementation`)

### 문서 파일
- `*.md` 파일들 (README, 가이드 등)
- 레포지토리에는 포함되지만 JAR에는 포함되지 않음

### 빌드 관련 파일
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/` 디렉토리
- `gradlew`, `gradlew.bat`

### 스크립트 파일
- `*.sh` 파일들
- 레포지토리 관리용 스크립트

## ✅ 확인 방법

### 로컬에서 JAR 내용 확인

```bash
cd common-kotlin-lib
./gradlew clean build

# JAR 파일 내용 확인
jar -tf build/libs/common-kotlin-lib-1.0.0-SNAPSHOT.jar | head -20

# Sources JAR 확인
jar -tf build/libs/common-kotlin-lib-1.0.0-SNAPSHOT-sources.jar | head -20
```

### 배포 후 확인

```bash
# GitHub Packages에서 다운로드한 후 확인
cd /tmp
mvn dependency:get \
  -Dartifact=com.s-class:common-kotlin-lib:1.0.0 \
  -DremoteRepositories=https://maven.pkg.github.com/passion-edu/s-class-common

# JAR 내용 확인
jar -tf ~/.m2/repository/com/s-class/common-kotlin-lib/1.0.0/common-kotlin-lib-1.0.0.jar
```

## 📋 체크리스트

배포 전 확인:
- [ ] 테스트 파일이 JAR에 포함되지 않음
- [ ] 문서 파일이 JAR에 포함되지 않음
- [ ] 필요한 소스 파일만 포함됨
- [ ] 리소스 파일이 올바르게 포함됨
- [ ] POM 파일에 올바른 의존성 정보 포함
