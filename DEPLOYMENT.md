# 공통 라이브러리 배포 가이드

## 📋 목차

1. [배포 방법](#배포-방법)
2. [버전 관리 전략](#버전-관리-전략)
3. [서비스에서 사용하기](#서비스에서-사용하기)
4. [CI/CD 자동 배포](#cicd-자동-배포)

---

## 배포 방법

### GitHub Packages 배포 (프로덕션 권장)

팀 전체가 사용할 수 있는 중앙 저장소입니다.

#### 사전 준비

1. **GitHub Personal Access Token 생성**
   - GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
   - 권한: `write:packages`, `read:packages`, `delete:packages`

2. **환경 변수 설정**
   ```bash
   export GITHUB_TOKEN=your_github_token
   export GITHUB_ACTOR=your_github_username
   ```

3. **gradle.properties 설정** (선택사항)
   ```properties
   github.packages.url=https://maven.pkg.github.com/passion-edu/s-class-common
   gpr.user=YOUR_GITHUB_USERNAME
   gpr.token=YOUR_GITHUB_TOKEN
   ```

#### 배포 명령어

```bash
cd common-kotlin-lib

# 환경 변수로 배포
GITHUB_TOKEN=your_token GITHUB_ACTOR=your_username \
  ./gradlew clean build test publish \
  -Pgithub.packages.url=https://maven.pkg.github.com/passion-edu/s-class-common \
  -Pgpr.user=$GITHUB_ACTOR \
  -Pgpr.token=$GITHUB_TOKEN

# 또는 gradle.properties 사용
./gradlew clean build test publish
```

#### 서비스에서 사용하기

각 서비스의 `build.gradle.kts`에 추가:

```kotlin
repositories {
    mavenCentral()
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

**장점:**
- 팀 전체 공유 가능
- 버전 관리 용이
- CI/CD 통합 가능
- 무료 (GitHub 사용 시)

**단점:**
- GitHub 계정 필요
- 초기 설정 필요

---

### 3. 사설 Maven 저장소 (조직 내부)

Nexus, Artifactory 등 사설 저장소 사용 시:

```kotlin
// build.gradle.kts
publishing {
    repositories {
        maven {
            name = "PrivateRepository"
            url = uri("https://nexus.your-company.com/repository/maven-releases")
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
```

---

## 버전 관리 전략

### Semantic Versioning (SemVer)

버전 형식: `MAJOR.MINOR.PATCH[-SNAPSHOT]`

- **MAJOR**: 하위 호환성 없는 변경 (예: 1.0.0 → 2.0.0)
- **MINOR**: 하위 호환성 있는 기능 추가 (예: 1.0.0 → 1.1.0)
- **PATCH**: 버그 수정 (예: 1.0.0 → 1.0.1)
- **SNAPSHOT**: 개발 중인 버전 (예: 1.0.0-SNAPSHOT)

### 버전 업데이트 프로세스

1. **개발 중**: `1.0.0-SNAPSHOT` 사용
2. **릴리즈 준비**: `1.0.0-SNAPSHOT` → `1.0.0`
3. **배포**: GitHub Packages에 배포
4. **태그 생성**: `git tag v1.0.0 && git push origin v1.0.0`

### 버전 변경 예시

```kotlin
// build.gradle.kts
version = "1.0.0"  // 릴리즈 버전
// 또는
version = "1.0.1-SNAPSHOT"  // 개발 버전
```

---

## 서비스에서 사용하기

### Step 1: Repository 추가

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
    // GitHub Packages만 사용
    
    // GitHub Packages 사용 시
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/passion-edu/s-class-common")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: "github"
            password = System.getenv("GITHUB_TOKEN") ?: ""
        }
    }
}
```

### Step 2: 의존성 추가

```kotlin
dependencies {
    // 공통 라이브러리
    implementation("com.s-class:common-kotlin-lib:1.0.0")
    
    // 선택적 의존성 (필요한 경우)
    // Spring Web (GlobalExceptionHandler 사용 시)
    implementation("org.springframework:spring-web:6.1.5")
    
    // Spring Data (PageResponse 사용 시)
    implementation("org.springframework.data:spring-data-commons:3.2.0")
    
    // Swagger (API 문서화 시)
    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
}
```

### Step 3: 코드 사용

```kotlin
// ULID 생성
import com.sclass.common.util.Ulid
val id = Ulid.generate()

// API 응답
import com.sclass.common.dto.ApiResponse
return ApiResponse.success(data)

// 예외 처리
import com.sclass.common.exception.BusinessException
throw BusinessException("ERROR_CODE", "Error message")

// Value Object
import com.sclass.common.domain.UlidValueObjectCompanion
class UserId(value: String) {
    companion object : UlidValueObjectCompanion<UserId> {
        override fun create(value: String): UserId = UserId(value)
    }
}
```

---

## CI/CD 자동 배포

### GitHub Actions 워크플로우

`.github/workflows/publish.yml` 파일 생성:

```yaml
name: Publish Library

on:
  push:
    tags:
      - 'v*'  # v1.0.0, v1.1.0 등 태그 푸시 시 배포
  workflow_dispatch:  # 수동 실행 가능

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Build and Test
        run: ./gradlew clean build test
      
      - name: Publish to GitHub Packages
        run: ./gradlew publish
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GITHUB_ACTOR: ${{ github.actor }}
        working-directory: common-kotlin-lib
      
      - name: Extract version from tag
        if: startsWith(github.ref, 'refs/tags/')
        id: tag
        run: echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT
      
      - name: Create Release
        if: startsWith(github.ref, 'refs/tags/')
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: ${{ github.ref }}
          release_name: Release ${{ steps.tag.outputs.VERSION }}
          body: |
            ## Changes
            - See [CHANGELOG.md](../CHANGELOG.md)
          draft: false
          prerelease: false
```

### 배포 프로세스

1. **개발**: `1.0.0-SNAPSHOT` 버전으로 개발
2. **테스트**: 로컬에서 `./gradlew test` 실행
3. **릴리즈 준비**: `build.gradle.kts`에서 `version = "1.0.0"` 설정
4. **태그 생성**: `git tag v1.0.0 && git push origin v1.0.0`
5. **자동 배포**: GitHub Actions가 자동으로 배포

---

## 배포 방법 비교

| 방법 | 용도 | 장점 | 단점 |
|------|------|------|------|
| **Maven Local** | 로컬 개발 | 빠름, 간단 | 공유 불가 |
| **GitHub Packages** | 프로덕션 | 무료, 통합 용이 | GitHub 계정 필요 |
| **사설 저장소** | 대규모 조직 | 완전한 제어 | 인프라 필요 |

---

## 문제 해결

### 의존성을 찾을 수 없을 때

1. **Repository 확인**: GitHub Packages repository가 추가되었는지 확인
2. **버전 확인**: 배포된 버전과 사용하는 버전이 일치하는지 확인
3. **인증 확인**: GitHub Packages 사용 시 토큰이 올바른지 확인

### 빌드 실패 시

```bash
# 캐시 정리
./gradlew clean

# 의존성 새로고침
./gradlew --refresh-dependencies build

# 상세 로그 확인
./gradlew build --info
```

---

## 참고 자료

- [Gradle Publishing Guide](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [GitHub Packages Guide](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)
- [Semantic Versioning](https://semver.org/)
