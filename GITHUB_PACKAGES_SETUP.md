# GitHub Packages 배포 및 사용 가이드

## 📋 목차

1. [배포 방법](#배포-방법)
2. [서비스에서 사용하기](#서비스에서-사용하기)
3. [CI/CD에서 사용하기](#cicd에서-사용하기)
4. [Organization 사용 시 주의사항](#organization-사용-시-주의사항)
5. [문제 해결](#문제-해결)

---

## 배포 방법

### 1. 자동 배포 (GitHub Actions)

#### 태그를 통한 배포 (권장)

```bash
# 1. 버전 업데이트
cd common-kotlin-lib
# build.gradle.kts에서 version 수정
# version = "1.0.0"

# 2. 커밋 및 푸시
git add build.gradle.kts
git commit -m "chore: bump version to 1.0.0"
git push

# 3. 태그 생성 및 푸시
git tag v1.0.0
git push origin v1.0.0
```

태그가 푸시되면 자동으로 GitHub Packages에 배포됩니다.

#### 수동 배포 (GitHub Actions)

1. GitHub 저장소 → **Actions** 탭
2. **Publish Library** 워크플로우 선택
3. **Run workflow** 클릭
4. 버전 입력 (예: `1.0.0`)
5. **Run workflow** 실행

### 2. 로컬에서 직접 배포

```bash
cd common-kotlin-lib

# GitHub Personal Access Token 필요
# 권한: write:packages, read:packages

export GITHUB_TOKEN=your_github_token
export GITHUB_ACTOR=your_github_username

./gradlew clean build test publish \
  -Pgithub.packages.url=https://maven.pkg.github.com/YOUR_ORG/s-class \
  -Pgpr.user=$GITHUB_ACTOR \
  -Pgpr.token=$GITHUB_TOKEN
```

---

## 서비스에서 사용하기

### 1. build.gradle.kts 설정

각 서비스의 `build.gradle.kts`에 다음을 추가합니다:

```kotlin
repositories {
    mavenCentral()
    
    // GitHub Packages 추가
    // Organization: passion-edu
    // Repository: s-class-common
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/passion-edu/s-class-common")
        credentials {
            username = project.findProperty("gpr.user") as String? 
                ?: System.getenv("GITHUB_ACTOR") 
                ?: "github"
            password = project.findProperty("gpr.token") as String? 
                ?: System.getenv("GITHUB_TOKEN") 
                ?: ""
        }
    }
}

dependencies {
    // 공통 라이브러리 사용
    implementation("com.s-class:common-kotlin-lib:1.0.0")
    
    // 선택적 의존성 (필요한 경우)
    // Spring Web (GlobalExceptionHandler, LoggingInterceptor 사용 시)
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring AOP (LoggingAspect 사용 시)
    implementation("org.springframework.boot:spring-boot-starter-aop")
    
    // SLF4J (로깅 사용 시)
    implementation("org.slf4j:slf4j-api")
    runtimeOnly("ch.qos.logback:logback-classic") // 또는 다른 SLF4J 구현체
}
```

### 2. 인증 설정

#### 방법 1: 환경 변수 사용 (권장)

```bash
export GITHUB_TOKEN=your_github_token
export GITHUB_ACTOR=your_github_username
```

#### 방법 2: gradle.properties 사용

`~/.gradle/gradle.properties` 또는 프로젝트 루트의 `gradle.properties`:

```properties
gpr.user=your_github_username
gpr.token=your_github_token
```

**주의**: `gradle.properties`는 `.gitignore`에 추가하여 커밋하지 마세요!

#### 방법 3: GitHub Personal Access Token 생성

1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. **Generate new token (classic)** 클릭
3. 권한 선택:
   - `read:packages` (필수)
   - `write:packages` (배포 시 필요)
4. **Organization 사용 시 추가 설정:**
   - **Organization access** 섹션에서 Organization 선택
   - `read:packages`, `write:packages` 권한 부여
   - 또는 Organization 관리자에게 토큰 승인 요청
5. 토큰 생성 후 복사 (한 번만 표시됨)

### 3. 서비스별 적용 예시

#### payment-service

```kotlin
// build.gradle.kts
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
    // 기존 의존성...
    implementation("com.s-class:common-kotlin-lib:1.0.0")
    // mavenLocal() 의존성 제거
}
```

#### account-service, auth, lms-service 등

동일한 방식으로 적용합니다.

---

## Organization 사용 시 주의사항

GitHub Organization을 사용하는 경우, 개인 계정과 거의 동일하게 작동하지만 몇 가지 추가 설정이 필요합니다.

### 1. GitHub Packages URL 형식

Organization을 사용할 때도 URL 형식은 동일합니다:

```kotlin
url = uri("https://maven.pkg.github.com/YOUR_ORG_NAME/s-class")
```

**예시:**
- 개인 계정: `https://maven.pkg.github.com/seeun0210/s-class`
- Organization: `https://maven.pkg.github.com/my-company/s-class`

### 2. Personal Access Token 권한

Organization의 패키지에 접근하려면 Personal Access Token에 다음 권한이 필요합니다:

#### 필수 권한
- ✅ `read:packages` - 패키지 읽기
- ✅ `write:packages` - 패키지 쓰기 (배포 시)
- ✅ `delete:packages` - 패키지 삭제 (선택사항)

#### Organization 권한 설정

1. **GitHub Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
2. 토큰 생성 시 **Organization access** 섹션에서:
   - Organization을 선택
   - `read:packages`, `write:packages` 권한 부여

또는 Organization 관리자가 **Organization settings** → **Third-party access**에서 토큰 승인

### 3. Organization의 패키지 가시성

Organization의 패키지는 기본적으로 **Private**입니다. 다음 설정을 확인하세요:

1. **Organization Settings** → **Packages**
2. 패키지 가시성 확인:
   - **Private**: Organization 멤버만 접근 가능
   - **Public**: 모든 사용자가 접근 가능

### 4. Organization 멤버 권한

각 서비스를 사용하는 개발자는 다음 중 하나여야 합니다:

- ✅ Organization의 멤버
- ✅ Organization의 패키지에 대한 읽기 권한이 있는 외부 협력자

### 5. CI/CD에서 Organization 사용

GitHub Actions에서 Organization의 패키지를 사용할 때는 자동으로 인증됩니다:

```yaml
# .github/workflows/deploy.yml
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read  # Organization 패키지 읽기 권한
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
      
      # GITHUB_TOKEN은 자동으로 Organization 패키지에 접근 가능
      - name: Build
        run: ./gradlew build
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GITHUB_ACTOR: ${{ github.actor }}
```

**주의**: Organization의 패키지에 쓰려면 워크플로우에 `packages: write` 권한이 필요합니다.

### 6. 로컬 개발 환경 설정

Organization의 패키지를 로컬에서 사용하려면:

#### 방법 1: Personal Access Token 사용 (권장)

```bash
export GITHUB_TOKEN=your_personal_access_token
export GITHUB_ACTOR=your_github_username
```

토큰은 Organization의 패키지에 대한 접근 권한이 있어야 합니다.

#### 방법 2: Organization의 Service Account 사용

Organization에서 Service Account를 생성하고 토큰을 발급받아 사용할 수 있습니다.

### 7. Organization vs 개인 계정 비교

| 항목 | 개인 계정 | Organization |
|------|----------|--------------|
| URL 형식 | `maven.pkg.github.com/USERNAME/repo` | `maven.pkg.github.com/ORG_NAME/repo` |
| 인증 | Personal Access Token | Personal Access Token (Org 권한 필요) |
| 가시성 | Private/Public 선택 가능 | 기본 Private, Public 가능 |
| 멤버 접근 | 본인만 | Organization 멤버 전체 |
| CI/CD | 자동 인증 | 자동 인증 (권한 확인 필요) |

### 8. Organization 설정 확인 체크리스트

- [ ] Organization의 패키지 가시성 확인
- [ ] Personal Access Token에 Organization 권한 부여
- [ ] Organization 멤버 권한 확인
- [ ] CI/CD 워크플로우에 `packages: read/write` 권한 설정
- [ ] 각 서비스의 `build.gradle.kts`에서 Organization 이름 확인

---

## CI/CD에서 사용하기

### GitHub Actions

각 서비스의 GitHub Actions 워크플로우에서 자동으로 인증됩니다:

```yaml
# .github/workflows/deploy.yml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
      
      # GitHub Packages 인증은 자동으로 처리됨
      - name: Build
        run: ./gradlew build
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GITHUB_ACTOR: ${{ github.actor }}
```

### 로컬 빌드

로컬에서 빌드할 때는 환경 변수를 설정하거나 `gradle.properties`를 사용하세요.

---

## 문제 해결

### 1. 인증 실패

**증상**: `Could not find artifact com.s-class:common-kotlin-lib`

**해결**:
- `GITHUB_TOKEN` 환경 변수 확인
- GitHub Personal Access Token 권한 확인 (`read:packages`)
- **Organization 사용 시**: Organization 권한이 토큰에 부여되었는지 확인
- 저장소 이름 확인 (`YOUR_ORG/s-class` 또는 `YOUR_ORG_NAME/s-class`)
- Organization의 패키지 가시성 확인 (Private인 경우 멤버 권한 필요)

### 2. 버전을 찾을 수 없음

**증상**: `Could not find com.s-class:common-kotlin-lib:1.0.0`

**해결**:
- 해당 버전이 GitHub Packages에 배포되었는지 확인
- GitHub 저장소 → Packages → `common-kotlin-lib` → 버전 확인
- 버전 번호 오타 확인

### 3. 의존성 충돌

**증상**: 버전 충돌 또는 클래스 중복

**해결**:
- `./gradlew dependencies`로 의존성 트리 확인
- `exclude`로 충돌하는 의존성 제외
- 공통 라이브러리 버전 업데이트

### 4. 로컬에서만 작동하는 경우

**증상**: 로컬에서는 되는데 CI/CD에서 실패

**해결**:
- CI/CD 워크플로우에 `GITHUB_TOKEN` 환경 변수 추가 확인
- `mavenLocal()` 제거 (로컬 캐시 의존 방지)

---

## 버전 관리 전략

### Semantic Versioning (SemVer)

- **MAJOR**: 호환되지 않는 API 변경
- **MINOR**: 하위 호환되는 기능 추가
- **PATCH**: 하위 호환되는 버그 수정

### 예시

```
1.0.0 → 1.0.1 (버그 수정)
1.0.1 → 1.1.0 (새 기능 추가)
1.1.0 → 2.0.0 (Breaking change)
```

### 배포 프로세스

1. `CHANGELOG.md` 업데이트
2. `build.gradle.kts`에서 버전 업데이트
3. 커밋 및 푸시
4. 태그 생성 및 푸시: `git tag v1.0.0 && git push origin v1.0.0`
5. GitHub Actions가 자동으로 배포

---

## 참고 자료

- [GitHub Packages 문서](https://docs.github.com/en/packages)
- [Gradle Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Semantic Versioning](https://semver.org/)
