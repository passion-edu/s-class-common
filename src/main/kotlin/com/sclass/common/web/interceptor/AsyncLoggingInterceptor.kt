package com.sclass.common.web.interceptor

import com.sclass.common.util.LoggerUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 비동기 HTTP 요청/응답 로깅 인터셉터 (Pino 스타일)
 * 
 * 모든 HTTP 요청과 응답을 비동기로 로깅하여 API 성능에 영향을 주지 않습니다.
 * 
 * 특징:
 * - 비동기 로깅으로 성능 영향 최소화
 * - 구조화된 JSON 형식 로깅
 * - 프로덕션 환경에서 자동으로 불필요한 로그 제거
 * 
 * 사용 방법:
 * ```kotlin
 * @Configuration
 * class WebConfig : WebMvcConfigurer {
 *     override fun addInterceptors(registry: InterceptorRegistry) {
 *         registry.addInterceptor(AsyncLoggingInterceptor())
 *     }
 * }
 * ```
 */
class AsyncLoggingInterceptor(
    private val executor: Executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        { r ->
            val thread = Thread(r, "async-logger-${System.currentTimeMillis()}")
            thread.isDaemon = true
            thread
        }
    ),
) : HandlerInterceptor {

    @Suppress("UNCHECKED_CAST")
    private val logger: Logger = LoggerUtils.getLogger(AsyncLoggingInterceptor::class.java) as Logger
    private val isDebugEnabled = logger.isDebugEnabled
    private val isInfoEnabled = logger.isInfoEnabled

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val startTime = System.currentTimeMillis()
        request.setAttribute("startTime", startTime)
        request.setAttribute("requestId", generateRequestId())

        // MDC는 동기적으로 설정 (로그백의 AsyncAppender가 사용)
        val requestId = request.getAttribute("requestId") as String
        MDC.put("requestId", requestId)
        MDC.put("method", request.method)
        MDC.put("path", request.requestURI)

        // 비동기로 요청 로깅 (성능 영향 없음)
        if (isDebugEnabled) {
            CompletableFuture.runAsync({
                logRequest(request, requestId)
            }, executor)
        }

        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        // 응답 로깅은 afterCompletion에서 처리 (예외 포함)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val startTime = request.getAttribute("startTime") as? Long ?: return
        val requestId = request.getAttribute("requestId") as? String ?: return
        val executionTime = System.currentTimeMillis() - startTime

        // MDC 업데이트
        MDC.put("status", response.status.toString())
        MDC.put("executionTime", executionTime.toString())

        // 비동기로 응답 로깅 (성능 영향 없음)
        CompletableFuture.runAsync({
            logResponse(request, response, executionTime, ex)
        }, executor).exceptionally { throwable ->
            // 로깅 실패는 무시 (API 성능에 영향 없음)
            null
        }

        // MDC 정리 (요청 완료 후)
        MDC.clear()
    }

    private fun logRequest(request: HttpServletRequest, requestId: String) {
        val context = buildRequestContext(request, requestId)
        
        // 구조화된 로깅 (JSON 형식으로 출력되도록)
        logger.debug("HTTP Request | {}", formatAsJson(context))
    }

    private fun logResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        executionTime: Long,
        ex: Exception?,
    ) {
        val context = buildResponseContext(request, response, executionTime, ex)
        
        when {
            ex != null || response.status >= 500 -> {
                // 서버 에러는 항상 로깅
                logger.error("HTTP Response | {}", formatAsJson(context))
            }
            response.status >= 400 -> {
                // 클라이언트 에러는 WARN
                if (isInfoEnabled) {
                    logger.warn("HTTP Response | {}", formatAsJson(context))
                }
            }
            else -> {
                // 성공은 DEBUG 레벨 (프로덕션에서 자동으로 비활성화)
                if (isDebugEnabled) {
                    logger.debug("HTTP Response | {}", formatAsJson(context))
                }
            }
        }
    }

    private fun buildRequestContext(
        request: HttpServletRequest,
        requestId: String,
    ): Map<String, Any?> {
        return mapOf(
            "type" to "request",
            "requestId" to requestId,
            "method" to request.method,
            "path" to request.requestURI,
            "query" to (request.queryString ?: ""),
            "remoteAddr" to request.remoteAddr,
            "userAgent" to (request.getHeader("User-Agent") ?: ""),
            "timestamp" to System.currentTimeMillis(),
        )
    }

    private fun buildResponseContext(
        request: HttpServletRequest,
        response: HttpServletResponse,
        executionTime: Long,
        ex: Exception?,
    ): Map<String, Any?> {
        val context = mutableMapOf<String, Any?>(
            "type" to "response",
            "requestId" to (request.getAttribute("requestId") as? String),
            "method" to request.method,
            "path" to request.requestURI,
            "status" to response.status,
            "executionTime" to executionTime,
            "timestamp" to System.currentTimeMillis(),
        )

        if (ex != null) {
            context["error"] = mapOf(
                "type" to ex.javaClass.simpleName,
                "message" to (ex.message ?: ""),
            )
        }

        return context
    }

    private fun formatAsJson(context: Map<String, Any?>): String {
        // 간단한 JSON 형식 (실제 JSON 라이브러리 없이)
        // 프로덕션에서는 Logback의 JSON encoder를 사용하는 것을 권장
        return context.entries.joinToString(", ") { (key, value) ->
            "$key=$value"
        }
    }

    private fun generateRequestId(): String {
        return java.util.UUID.randomUUID().toString().substring(0, 8)
    }
}
