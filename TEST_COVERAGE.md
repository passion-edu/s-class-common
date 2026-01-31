# 테스트 커버리지 가이드

## 테스트 실행

### 모든 테스트 실행

```bash
./gradlew test
```

### 특정 패키지 테스트 실행

```bash
./gradlew test --tests "com.sclass.common.util.*"
./gradlew test --tests "com.sclass.common.dto.*"
./gradlew test --tests "com.sclass.common.exception.*"
```

### 테스트 리포트 확인

```bash
./gradlew test
# 리포트 위치: build/reports/tests/test/index.html
```

## 테스트 커버리지

현재 포함된 테스트:

### 1. ULID 유틸리티 (`UlidTest.kt`)
- ✅ ULID 생성 테스트
- ✅ 유효한 ULID 검증 테스트
- ✅ 유효하지 않은 ULID 검증 테스트 (길이 부족, 잘못된 문자)
- ✅ ULID 파싱 테스트 (유효한/유효하지 않은 ULID)

### 2. API 응답 (`ApiResponseTest.kt`)
- ✅ 성공 응답 생성 테스트
- ✅ 에러 응답 생성 테스트
- ✅ null 데이터 처리 테스트
- ✅ 복잡한 데이터 타입 테스트
- ✅ 리스트 데이터 테스트
- ✅ equals 테스트

### 3. 에러 응답 (`ErrorResponseTest.kt`)
- ✅ ErrorResponse 생성 테스트
- ✅ equals 테스트
- ✅ hashCode 테스트

### 4. 페이지네이션 응답 (`PageResponseTest.kt`)
- ✅ Spring Data Page 변환 테스트
- ✅ 빈 페이지 테스트
- ✅ 마지막 페이지 테스트
- ✅ 중간 페이지 테스트

### 5. 비즈니스 예외 (`BusinessExceptionTest.kt`)
- ✅ BusinessException 생성 테스트
- ✅ cause 포함 생성 테스트
- ✅ null 메시지 처리 테스트

### 6. Value Object 헬퍼 (`UlidValueObjectCompanionTest.kt`)
- ✅ of 메서드 테스트 (유효한/유효하지 않은 ULID)
- ✅ from 메서드 테스트
- ✅ generate 메서드 테스트
- ✅ equals 테스트

### 7. 전역 예외 처리 (`GlobalExceptionHandlerTest.kt`)
- ✅ BusinessException 처리 테스트
- ✅ IllegalArgumentException 처리 테스트
- ✅ IllegalStateException 처리 테스트
- ✅ RuntimeException 처리 테스트
- ✅ null 메시지 처리 테스트

## 테스트 커버리지 확인

### JaCoCo 플러그인 추가 (선택사항)

`build.gradle.kts`에 추가:

```kotlin
plugins {
    // ...
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

실행:

```bash
./gradlew test jacocoTestReport
# 리포트 위치: build/reports/jacoco/test/html/index.html
```

## 테스트 작성 가이드

### 새 모듈 추가 시

1. `src/test/kotlin/com/sclass/common/{package}/`에 테스트 파일 생성
2. 파일명: `{ClassName}Test.kt`
3. 최소한 다음을 테스트:
   - 정상 케이스
   - 예외 케이스
   - 경계값 (null, 빈 값 등)
   - equals/hashCode (data class인 경우)

### 테스트 네이밍

```kotlin
@Test
fun `설명적인 테스트 이름`() {
    // Given
    // When
    // Then
}
```

또는

```kotlin
@Test
fun testMethodName_condition_expectedResult() {
    // ...
}
```

## CI/CD 통합

GitHub Actions에서 테스트 실행:

```yaml
- name: Run tests
  run: ./gradlew test

- name: Upload test results
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: test-results
    path: build/reports/tests/test/
```
