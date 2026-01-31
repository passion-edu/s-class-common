package com.sclass.common.util

import org.slf4j.Logger

/**
 * 로깅 확장 함수
 * 
 * 일관된 로깅 패턴을 제공하는 확장 함수입니다.
 * 
 * 사용 방법:
 * ```kotlin
 * logger.infoWithContext("작업 시작", mapOf("userId" to userId))
 * logger.errorWithContext("에러 발생", exception, mapOf("paymentId" to paymentId))
 * ```
 */

/**
 * 컨텍스트 정보와 함께 INFO 로그 기록
 * 
 * @param message 로그 메시지
 * @param context 컨텍스트 정보 (키-값 쌍)
 */
fun Logger.infoWithContext(message: String, context: Map<String, Any?> = emptyMap()) {
    if (context.isEmpty()) {
        info(message)
    } else {
        val contextStr = context.entries.joinToString(", ") { "${it.key}=${it.value}" }
        info("$message | $contextStr")
    }
}

/**
 * 컨텍스트 정보와 함께 ERROR 로그 기록
 * 
 * @param message 로그 메시지
 * @param throwable 예외 정보
 * @param context 컨텍스트 정보 (키-값 쌍)
 */
fun Logger.errorWithContext(
    message: String,
    throwable: Throwable? = null,
    context: Map<String, Any?> = emptyMap(),
) {
    val contextStr = if (context.isEmpty()) {
        ""
    } else {
        " | ${context.entries.joinToString(", ") { "${it.key}=${it.value}" }}"
    }
    
    if (throwable != null) {
        error("$message$contextStr", throwable)
    } else {
        error("$message$contextStr")
    }
}

/**
 * 컨텍스트 정보와 함께 WARN 로그 기록
 * 
 * @param message 로그 메시지
 * @param context 컨텍스트 정보 (키-값 쌍)
 */
fun Logger.warnWithContext(message: String, context: Map<String, Any?> = emptyMap()) {
    if (context.isEmpty()) {
        warn(message)
    } else {
        val contextStr = context.entries.joinToString(", ") { "${it.key}=${it.value}" }
        warn("$message | $contextStr")
    }
}

/**
 * 컨텍스트 정보와 함께 DEBUG 로그 기록
 * 
 * @param message 로그 메시지
 * @param context 컨텍스트 정보 (키-값 쌍)
 */
fun Logger.debugWithContext(message: String, context: Map<String, Any?> = emptyMap()) {
    if (isDebugEnabled) {
        if (context.isEmpty()) {
            debug(message)
        } else {
            val contextStr = context.entries.joinToString(", ") { "${it.key}=${it.value}" }
            debug("$message | $contextStr")
        }
    }
}

/**
 * 메서드 진입 로그 기록
 * 
 * @param methodName 메서드 이름
 * @param params 메서드 파라미터 (선택적)
 */
fun Logger.logMethodEntry(methodName: String, params: Map<String, Any?> = emptyMap()) {
    if (isDebugEnabled) {
        if (params.isEmpty()) {
            debug("→ $methodName()")
        } else {
            val paramsStr = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
            debug("→ $methodName($paramsStr)")
        }
    }
}

/**
 * 메서드 종료 로그 기록
 * 
 * @param methodName 메서드 이름
 * @param result 반환값 (선택적)
 */
fun Logger.logMethodExit(methodName: String, result: Any? = null) {
    if (isDebugEnabled) {
        if (result != null) {
            debug("← $methodName() = $result")
        } else {
            debug("← $methodName()")
        }
    }
}
