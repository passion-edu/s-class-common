package com.sclass.common.web.annotation

/**
 * 메서드 로깅 어노테이션
 * 
 * 메서드의 진입/종료 및 파라미터/반환값을 자동으로 로깅합니다.
 * 
 * 사용 방법:
 * ```kotlin
 * @Loggable(level = LogLevel.INFO, includeParams = true, includeResult = true)
 * fun processPayment(paymentId: String, amount: Long): PaymentResult {
 *     // ...
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Loggable(
    /**
     * 로그 레벨
     */
    val level: LogLevel = LogLevel.DEBUG,
    
    /**
     * 파라미터 포함 여부
     */
    val includeParams: Boolean = true,
    
    /**
     * 반환값 포함 여부
     */
    val includeResult: Boolean = true,
    
    /**
     * 실행 시간 측정 여부
     */
    val measureTime: Boolean = true,
)

/**
 * 로그 레벨
 */
enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
}
