# 배포 체크리스트

## 📋 배포 전 확인 사항

### 1. 빌드 및 테스트 ✅

```bash
cd common-kotlin-lib
./gradlew clean build test
```

- [ ] 빌드 성공
- [ ] 모든 테스트 통과
- [ ] 컴파일 경고 없음

### 2. 버전 확인 ✅

현재 버전: `1.0.0-SNAPSHOT`

**선택지:**
- [ ] **SNAPSHOT 유지**: 개발 중인 버전 (계속 업데이트 가능)
- [ ] **1.0.0 릴리즈**: 첫 안정 버전 (태그 생성 필요)

### 3. 문서 확인 ✅

- [ ] README.md 최신화
- [ ] DEPLOYMENT.md 확인
- [ ] LOGGING_GUIDE.md 확인
- [ ] 사용 예시 코드 검증

### 4. 의존성 확인 ✅

- [ ] 필수 의존성만 포함 (ulid-creator)
- [ ] 선택적 의존성은 compileOnly로 선언
- [ ] 버전 카탈로그 최신화

### 5. 코드 품질 ✅

- [ ] Linter 오류 없음
- [ ] 코드 스타일 일관성
- [ ] 주석 및 문서화 완료

---

## 🚀 배포 단계

### Step 1: 빌드 및 테스트

```bash
cd common-kotlin-lib
./gradlew clean build test
```

### Step 2: 버전 결정

#### 옵션 A: SNAPSHOT 배포 (개발용)

```bash
# 버전은 그대로 1.0.0-SNAPSHOT 유지
./gradlew clean build test
```

#### 옵션 B: 릴리즈 버전 배포

```bash
# 1. build.gradle.kts에서 버전 변경
# version = "1.0.0"  # SNAPSHOT 제거

# 2. 빌드 및 배포
./gradlew clean build publishToMavenLocal

# 3. Git 태그 생성 (선택사항)
# git tag v1.0.0
# git push origin v1.0.0
```

### Step 3: 배포 실행

#### 로컬 빌드 테스트 (배포 전 확인)

```bash
./gradlew clean build test
```

#### GitHub Packages 배포 (팀 공유용)

```bash
# 환경 변수 설정
export GITHUB_TOKEN=your_github_token
export GITHUB_ACTOR=your_github_username

# 배포
./gradlew publish \
  -Pgithub.packages.url=https://maven.pkg.github.com/passion-edu/s-class-common \
  -Pgpr.user=$GITHUB_ACTOR \
  -Pgpr.token=$GITHUB_TOKEN
```

### Step 4: 배포 확인

#### 빌드 확인

```bash
ls -la ~/.m2/repository/com/s-class/common-kotlin-lib/
```

다음 파일들이 있어야 합니다:
- `common-kotlin-lib-1.0.0-SNAPSHOT.jar`
- `common-kotlin-lib-1.0.0-SNAPSHOT-sources.jar`
- `common-kotlin-lib-1.0.0-SNAPSHOT-javadoc.jar`
- `common-kotlin-lib-1.0.0-SNAPSHOT.pom`

#### 테스트 서비스에서 확인

```kotlin
// build.gradle.kts
repositories {
    // GitHub Packages repository
}

dependencies {
    implementation("com.s-class:common-kotlin-lib:1.0.0-SNAPSHOT")
}
```

---

## 📝 배포 후 작업

### 1. 서비스 적용

- [ ] payment-service에 적용 (이미 완료)
- [ ] 다른 서비스에 적용 계획 수립

### 2. 문서 업데이트

- [ ] 배포 버전 기록
- [ ] 변경 사항 문서화
- [ ] 사용 가이드 업데이트

### 3. 모니터링

- [ ] 배포된 라이브러리 사용 확인
- [ ] 오류 모니터링
- [ ] 성능 확인

---

## 🔄 버전 업데이트 프로세스

### SNAPSHOT 업데이트

```bash
# 1. 코드 수정
# 2. 빌드 및 테스트
./gradlew clean build test

# 3. 배포 (버전은 그대로)
./gradlew clean build test

# 4. 서비스에서 버전 업데이트 불필요 (SNAPSHOT은 항상 최신)
```

### 릴리즈 버전 업데이트

```bash
# 1. build.gradle.kts에서 버전 변경
# version = "1.0.1"  # 패치 버전 증가

# 2. 빌드 및 테스트
./gradlew clean build test

# 3. 배포
./gradlew clean build test

# 4. Git 태그 생성
git tag v1.0.1
git push origin v1.0.1

# 5. 서비스에서 버전 업데이트 필요
# implementation("com.s-class:common-kotlin-lib:1.0.1")
```

---

## ⚠️ 주의사항

1. **SNAPSHOT 버전**: 개발 중인 버전, 계속 변경 가능
2. **릴리즈 버전**: 안정 버전, 변경 시 새 버전 배포 필요
3. **하위 호환성**: 기존 API 변경 시 MAJOR 버전 증가
4. **의존성 관리**: 선택적 의존성은 서비스에서 제공해야 함

---

## 🆘 문제 해결

### 빌드 실패 시

```bash
# 캐시 정리
./gradlew clean

# 의존성 새로고침
./gradlew --refresh-dependencies build
```

### 배포 실패 시

```bash
# GitHub Packages 확인
# GitHub 저장소 → Packages → common-kotlin-lib에서 버전 확인

# 권한 확인
chmod +x gradlew

# 상세 로그 확인
./gradlew clean build test --info
```

### 의존성 찾을 수 없을 때

```bash
# Repository 확인
# build.gradle.kts에 mavenLocal() 또는 GitHub Packages 추가 확인

# 버전 확인
# 배포된 버전과 사용하는 버전 일치 확인
```
