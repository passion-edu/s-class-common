# 최적화 가이드

## 현재 설정 평가

### ✅ 잘 구성된 부분

1. **Gradle Version Catalog**
   - 모든 의존성 버전을 `gradle/libs.versions.toml`에서 중앙 관리
   - 버전 업데이트가 한 곳에서 가능
   - 타입 안전성 보장

2. **최소 의존성 전략**
   - 필수 의존성만 `implementation`으로 포함
   - 선택적 의존성은 `compileOnly`로 선언
   - 런타임 의존성 최소화

3. **선택적 의존성 관리**
   - Spring, Swagger 등은 `compileOnly`로 선언
   - Maven POM에 `optional=true`로 표시
   - 사용하는 서비스에서 필요한 의존성만 추가

4. **테스트 커버리지**
   - 모든 모듈에 대한 테스트 코드 포함
   - JUnit 5 사용

### 🔧 최적화된 부분

1. **Maven POM 생성**
   - `compileOnly` 의존성을 `optional`로 명시적으로 표시
   - 사용자가 필요한 의존성을 쉽게 파악 가능

2. **배포 전략**
   - Maven Local (개발용)
   - GitHub Packages (프로덕션용)
   - 환경 변수로 유연하게 전환 가능

3. **CI/CD 통합**
   - GitHub Actions로 자동 배포
   - 태그 기반 버전 관리

---

## 의존성 관리 전략

### 필수 의존성 (implementation)

```kotlin
implementation(libs.ulid.creator)  // ULID 생성 라이브러리
```

**이유**: 라이브러리의 핵심 기능이므로 항상 포함되어야 함.

### 선택적 의존성 (compileOnly)

```kotlin
compileOnly(libs.spring.data.commons)  // PageResponse 사용 시
compileOnly(libs.spring.web)          // GlobalExceptionHandler 사용 시
compileOnly(libs.swagger.annotations)  // API 문서화 시
```

**이유**: 
- 모든 서비스가 Spring을 사용하지 않을 수 있음
- 라이브러리 크기 최소화
- 사용하는 서비스에서 필요한 버전 선택 가능

### 테스트 의존성

```kotlin
testImplementation(libs.spring.data.commons)
testImplementation(libs.spring.web)
```

**이유**: 테스트 코드에서 선택적 의존성을 사용하므로 필요.

---

## Maven POM 최적화

### 현재 방식

```kotlin
withXml {
    // compileOnly 의존성을 optional로 수동 추가
    val optionalDeps = listOf(
        Triple("org.springframework.data", "spring-data-commons", ...),
        // ...
    )
    optionalDeps.forEach { (groupId, artifactId, version) ->
        val dep = dependencies.appendNode("dependency")
        dep.appendNode("groupId", groupId)
        dep.appendNode("artifactId", artifactId)
        dep.appendNode("version", version)
        dep.appendNode("optional", "true")
    }
}
```

**장점**:
- 명시적으로 optional 의존성 표시
- 사용자가 필요한 의존성을 쉽게 파악

**단점**:
- 코드가 다소 복잡함
- 버전을 수동으로 관리해야 함

### 대안 (고려했지만 채택하지 않은 방법)

1. **`api` vs `implementation` 구분**
   - Kotlin Multiplatform에서는 유용하지만, 단일 플랫폼에서는 `compileOnly`가 더 적합

2. **BOM (Bill of Materials) 사용**
   - Spring BOM은 Spring 프로젝트에만 유용
   - 범용 라이브러리에는 부적합

3. **의존성 제외 후 수동 추가**
   - 더 복잡하고 유지보수 어려움

**결론**: 현재 방식이 가장 명확하고 유지보수하기 좋음.

---

## 성능 최적화

### 빌드 성능

1. **Gradle 캐시 활용**
   ```bash
   # GitHub Actions에서
   - uses: actions/cache@v4
     with:
       path: ~/.gradle/caches
       key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
   ```

2. **병렬 빌드**
   ```bash
   ./gradlew build --parallel
   ```

3. **빌드 캐시**
   ```bash
   ./gradlew build --build-cache
   ```

### 런타임 성능

- 최소한의 의존성만 포함하여 클래스 로딩 시간 단축
- 선택적 의존성으로 불필요한 클래스 로드 방지

---

## 버전 관리 최적화

### Semantic Versioning

```
MAJOR.MINOR.PATCH[-SNAPSHOT]
```

- **MAJOR**: 하위 호환성 없는 변경
- **MINOR**: 하위 호환성 있는 기능 추가
- **PATCH**: 버그 수정
- **SNAPSHOT**: 개발 중인 버전

### 버전 업데이트 전략

1. **개발 중**: `1.0.0-SNAPSHOT` 사용
2. **릴리즈**: `1.0.0-SNAPSHOT` → `1.0.0`
3. **태그 생성**: `git tag v1.0.0`
4. **자동 배포**: GitHub Actions 트리거

---

## 보안 최적화

### 의존성 취약점 검사

```bash
# Gradle Dependency Check Plugin 사용
./gradlew dependencyCheckAnalyze
```

### 신뢰할 수 있는 저장소만 사용

```kotlin
repositories {
    mavenCentral()  // 신뢰할 수 있는 저장소
    // maven { url = uri("https://unknown-repo.com") }  // 피해야 함
}
```

---

## 모니터링 및 메트릭

### 라이브러리 사용 통계

- GitHub Packages에서 다운로드 통계 확인
- 각 서비스에서 사용하는 버전 추적

### 빌드 시간 모니터링

```bash
./gradlew build --profile
```

---

## 향후 개선 사항

1. **API 안정성**
   - `@Deprecated` 어노테이션으로 하위 호환성 유지
   - 마이그레이션 가이드 제공

2. **문서화**
   - KDoc 주석 보강
   - 사용 예시 추가

3. **성능 벤치마크**
   - JMH를 사용한 성능 테스트
   - 회귀 테스트 추가

4. **다중 플랫폼 지원**
   - Kotlin Multiplatform 고려
   - JVM 외 플랫폼 지원

---

## 참고 자료

- [Gradle Best Practices](https://docs.gradle.org/current/userguide/performance.html)
- [Maven Optional Dependencies](https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html)
- [Semantic Versioning](https://semver.org/)
