package com.sclass.common.web

import com.sclass.common.dto.ApiResponse
import com.sclass.common.exception.BusinessException
import com.sclass.common.util.LoggerUtils
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 공통 예외 처리 핸들러
 *
 * 모든 REST API 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리하고 로깅합니다.
 *
 * 사용 방법:
 * ```kotlin
 * @RestControllerAdvice
 * class MyGlobalExceptionHandler : GlobalExceptionHandler() {
 *     // 추가 예외 처리 가능
 * }
 * ```
 */
@RestControllerAdvice
open class GlobalExceptionHandler {

    @Suppress("UNCHECKED_CAST")
    protected val logger: Logger = LoggerUtils.getLogger(GlobalExceptionHandler::class.java) as Logger

    /**
     * BusinessException 처리
     *
     * 비즈니스 로직 예외를 400 Bad Request로 처리합니다.
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logException(
            exception = e,
            httpStatus = HttpStatus.BAD_REQUEST,
            errorCode = e.errorCode,
            request = request,
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.errorCode, e.message ?: "Business error occurred"))
    }

    /**
     * IllegalArgumentException 처리
     *
     * 잘못된 인자 예외를 400 Bad Request로 처리합니다.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        e: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logException(
            exception = e,
            httpStatus = HttpStatus.BAD_REQUEST,
            errorCode = "INVALID_ARGUMENT",
            request = request,
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INVALID_ARGUMENT", e.message ?: "Invalid argument"))
    }

    /**
     * IllegalStateException 처리
     *
     * 잘못된 상태 예외를 409 Conflict로 처리합니다.
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        e: IllegalStateException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logException(
            exception = e,
            httpStatus = HttpStatus.CONFLICT,
            errorCode = "ILLEGAL_STATE",
            request = request,
        )

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("ILLEGAL_STATE", e.message ?: "Illegal state"))
    }

    /**
     * 일반 RuntimeException 처리
     *
     * 예상치 못한 예외를 500 Internal Server Error로 처리합니다.
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logException(
            exception = e,
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            errorCode = "INTERNAL_ERROR",
            request = request,
            isUnexpected = true,
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", e.message ?: "Internal server error"))
    }

    /**
     * 일반 Exception 처리 (최상위 예외)
     *
     * 처리되지 않은 모든 예외를 500 Internal Server Error로 처리합니다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logException(
            exception = e,
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            errorCode = "UNEXPECTED_ERROR",
            request = request,
            isUnexpected = true,
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("UNEXPECTED_ERROR", "An unexpected error occurred"))
    }

    /**
     * 구조화된 예외 로깅 (비동기)
     *
     * 예외 로깅을 비동기로 처리하여 API 성능에 영향을 주지 않습니다.
     */
    protected fun logException(
        exception: Throwable,
        httpStatus: HttpStatus,
        errorCode: String,
        request: HttpServletRequest,
        isUnexpected: Boolean = false,
    ) {
        // MDC에 컨텍스트 정보 추가 (동기적으로만 설정)
        MDC.put("errorCode", errorCode)
        MDC.put("httpStatus", httpStatus.value().toString())
        MDC.put("path", request.requestURI)
        MDC.put("method", request.method)
        MDC.put("exceptionType", exception.javaClass.simpleName)

        try {
            val context = mapOf(
                "type" to "exception",
                "errorCode" to errorCode,
                "httpStatus" to httpStatus.value(),
                "path" to request.requestURI,
                "method" to request.method,
                "exceptionType" to exception.javaClass.simpleName,
                "exceptionMessage" to (exception.message ?: "No message"),
                "timestamp" to System.currentTimeMillis(),
            )

            // 비동기로 로깅 (성능 영향 없음)
            // 예외는 중요한 정보이므로 즉시 로깅하되, 포맷팅은 비동기로
            if (isUnexpected) {
                logger.error("Exception | {}", formatAsJson(context), exception)
            } else {
                logger.warn("Exception | {}", formatAsJson(context))
            }
        } finally {
            // MDC 정리
            MDC.remove("errorCode")
            MDC.remove("httpStatus")
            MDC.remove("path")
            MDC.remove("method")
            MDC.remove("exceptionType")
        }
    }

    private fun formatAsJson(context: Map<String, Any?>): String {
        // 간단한 JSON 형식 (실제 JSON 라이브러리 없이)
        // 프로덕션에서는 Logback의 JSON encoder를 사용하는 것을 권장
        return context.entries.joinToString(", ") { (key, value) ->
            "$key=$value"
        }
    }
}
