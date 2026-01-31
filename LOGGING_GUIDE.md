# 구조화된 로깅 가이드

## 개요

이 라이브러리는 Spring 기반 애플리케이션에서 일관되고 구조화된 로깅을 제공합니다.

## 주요 기능

1. **GlobalExceptionHandler 통합 로깅**: 모든 예외를 구조화된 형식으로 로깅
2. **AOP 기반 메서드 로깅**: `@Loggable` 어노테이션으로 메서드 자동 로깅
3. **HTTP 요청/응답 로깅**: 인터셉터를 통한 HTTP 요청/응답 로깅
4. **MDC (Mapped Diagnostic Context) 활용**: 컨텍스트 정보를 로그에 자동 포함

---

## 1. GlobalExceptionHandler 로깅

### 기본 사용

`GlobalExceptionHandler`는 모든 예외를 자동으로 구조화된 형식으로 로깅합니다.

```kotlin
@RestControllerAdvice
class MyGlobalExceptionHandler : GlobalExceptionHandler() {
    // 예외 처리는 부모 클래스에서 자동으로 처리되고 로깅됩니다
}
```

### 로그 형식

```
WARN | errorCode=INVALID_ARGUMENT, httpStatus=400, path=/api/users, method=POST, exceptionType=IllegalArgumentException, exceptionMessage=Invalid user ID
```

### MDC 컨텍스트

예외 발생 시 다음 정보가 MDC에 자동으로 추가됩니다:
- `errorCode`: 에러 코드
- `httpStatus`: HTTP 상태 코드
- `path`: 요청 경로
- `method`: HTTP 메서드
- `exceptionType`: 예외 타입

---

## 2. AOP 기반 메서드 로깅

### 기본 사용

```kotlin
import com.sclass.common.web.annotation.Loggable
import com.sclass.common.web.annotation.LogLevel

@Service
class PaymentService {
    
    @Loggable(level = LogLevel.INFO, includeParams = true, includeResult = true)
    fun processPayment(paymentId: String, amount: Long): PaymentResult {
        // 메서드 진입/종료, 파라미터, 반환값이 자동으로 로깅됩니다
        return PaymentResult(...)
    }
    
    @Loggable(level = LogLevel.DEBUG, measureTime = true)
    fun validatePayment(paymentId: String): Boolean {
        // 실행 시간도 함께 로깅됩니다
        return true
    }
}
```

### 클래스 레벨 적용

```kotlin
@Loggable(level = LogLevel.DEBUG)
@Service
class PaymentService {
    // 모든 public 메서드에 자동으로 적용됩니다
    fun method1() { }
    fun method2() { }
}
```

### 로그 형식

**메서드 진입:**
```
DEBUG | → PaymentService.processPayment(paymentId="PAY123", amount=10000)
```

**메서드 종료:**
```
DEBUG | ← processPayment(PaymentResult(id=PAY123, status=SUCCESS)) (45ms)
```

**예외 발생:**
```
ERROR | ✗ processPayment() - Exception: Payment not found
```

### 설정 옵션

- `level`: 로그 레벨 (TRACE, DEBUG, INFO, WARN, ERROR)
- `includeParams`: 파라미터 로깅 여부
- `includeResult`: 반환값 로깅 여부
- `measureTime`: 실행 시간 측정 여부

---

## 3. HTTP 요청/응답 로깅

### 비동기 로깅 (권장 - 성능 최적화)

**Pino 스타일의 비동기 로깅**을 사용하여 API 성능에 영향을 주지 않습니다.

```kotlin
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        // AsyncLoggingInterceptor 사용 (성능 최적화)
        registry.addInterceptor(AsyncLoggingInterceptor())
            .addPathPatterns("/api/**")  // 특정 경로만 로깅
            .excludePathPatterns("/health", "/actuator/**")  // 제외 경로
    }
}
```

### 동기 로깅 (개발 환경용)

개발 환경에서만 사용하는 경우:

```kotlin
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoggingInterceptor())  // 동기 로깅
            .addPathPatterns("/api/**")
    }
}
```

### 로그 형식

**요청:**
```
DEBUG | → HTTP Request | method=POST, path=/api/payments, queryString=, remoteAddr=127.0.0.1, userAgent=Mozilla/5.0...
```

**응답:**
```
DEBUG | ← HTTP Response (Success) | status=200, executionTime=45ms, path=/api/payments
WARN  | ← HTTP Response (Client Error) | status=400, executionTime=12ms, path=/api/payments
ERROR | ← HTTP Response (Server Error) | status=500, executionTime=123ms, path=/api/payments
```

### MDC 컨텍스트

HTTP 요청 시 다음 정보가 MDC에 자동으로 추가됩니다:
- `requestId`: 요청 고유 ID
- `method`: HTTP 메서드
- `path`: 요청 경로
- `queryString`: 쿼리 문자열
- `remoteAddr`: 클라이언트 IP
- `userAgent`: User-Agent
- `status`: HTTP 상태 코드
- `executionTime`: 실행 시간

---

## 4. 로깅 유틸리티 사용

### LoggerUtils

```kotlin
import com.sclass.common.util.LoggerUtils

class MyService {
    private val logger = LoggerUtils.getLogger<MyService>()
    
    fun doSomething() {
        logger.info("작업 시작")
    }
}
```

### LoggerExtensions

```kotlin
import com.sclass.common.util.*

logger.infoWithContext(
    "결제 처리 시작",
    mapOf(
        "paymentId" to paymentId,
        "userId" to userId,
        "amount" to amount
    )
)

logger.errorWithContext(
    "결제 실패",
    exception,
    mapOf(
        "paymentId" to paymentId,
        "errorCode" to errorCode
    )
)
```

---

## 5. 로깅 설정 예시

### application.properties

```properties
# 로깅 레벨 설정
logging.level.com.sclass=INFO
logging.level.com.sclass.common.web.aspect=DEBUG  # AOP 로깅
logging.level.com.sclass.common.web.interceptor=DEBUG  # 인터셉터 로깅

# 로그 패턴 (MDC 포함)
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{method}] %logger{36} - %msg%n
```

### logback-spring.xml (선택사항)

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{method}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

---

## 6. 모범 사례

### 1. 예외 로깅은 GlobalExceptionHandler에 맡기기

```kotlin
// ❌ 나쁜 예: 수동으로 예외 로깅
try {
    processPayment()
} catch (e: Exception) {
    logger.error("에러 발생", e)  // 중복 로깅
    throw e
}

// ✅ 좋은 예: 예외를 그냥 던지기
fun processPayment() {
    // 예외는 GlobalExceptionHandler에서 자동으로 로깅됩니다
    if (payment == null) {
        throw BusinessException("PAYMENT_NOT_FOUND", "결제를 찾을 수 없습니다")
    }
}
```

### 2. 중요한 비즈니스 로직에만 @Loggable 사용

```kotlin
// ✅ 좋은 예: 중요한 비즈니스 로직
@Loggable(level = LogLevel.INFO)
fun processPayment(paymentId: String): PaymentResult {
    // ...
}

// ❌ 나쁜 예: 모든 메서드에 적용
@Loggable  // 불필요한 로깅
fun getPaymentStatus(paymentId: String): String {
    return "SUCCESS"
}
```

### 3. 컨텍스트 정보 활용

```kotlin
// ✅ 좋은 예: 구조화된 로깅
logger.infoWithContext(
    "결제 처리 완료",
    mapOf(
        "paymentId" to paymentId,
        "amount" to amount,
        "userId" to userId
    )
)

// ❌ 나쁜 예: 문자열 연결
logger.info("결제 처리 완료: paymentId=$paymentId, amount=$amount, userId=$userId")
```

---

## 7. 의존성 추가

### build.gradle.kts

```kotlin
dependencies {
    // 공통 라이브러리
    implementation("com.s-class:common-kotlin-lib:1.0.0")
    
    // Spring AOP (로깅 AOP 사용 시)
    implementation("org.springframework:spring-aop:6.1.5")
    implementation("org.aspectj:aspectjweaver:1.9.20.1")
    
    // SLF4J (로깅 사용 시)
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")  // 또는 다른 SLF4J 구현체
}
```

---

## 8. 문제 해결

### AOP가 작동하지 않을 때

1. `@EnableAspectJAutoProxy` 확인 (Spring Boot는 자동 활성화)
2. Aspect 클래스에 `@Component` 또는 `@Aspect` 확인
3. 의존성 확인: `spring-aop`, `aspectjweaver`

### MDC가 비어있을 때

1. 로깅 인터셉터가 등록되었는지 확인
2. 로그 패턴에 `%X{key}` 형식 사용 확인

### 로그가 너무 많을 때

1. `@Loggable`의 `level` 조정
2. 인터셉터의 경로 패턴 제한
3. 로깅 레벨을 INFO 이상으로 설정

---

## 참고 자료

- [Spring AOP Documentation](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [SLF4J MDC Documentation](http://www.slf4j.org/manual.html#mdc)
- [Logback Configuration](http://logback.qos.ch/manual/configuration.html)
