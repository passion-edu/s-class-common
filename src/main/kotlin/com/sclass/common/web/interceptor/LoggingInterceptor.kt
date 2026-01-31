package com.sclass.common.web.interceptor

import com.sclass.common.util.LoggerUtils
import com.sclass.common.util.debugWithContext
import com.sclass.common.util.errorWithContext
import com.sclass.common.util.warnWithContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

/**
 * HTTP 요청/응답 로깅 인터셉터
 *
 * 모든 HTTP 요청과 응답을 구조화된 형식으로 로깅합니다.
 *
 * 사용 방법:
 * ```kotlin
 * @Configuration
 * class WebConfig : WebMvcConfigurer {
 *     override fun addInterceptors(registry: InterceptorRegistry) {
 *         registry.addInterceptor(LoggingInterceptor())
 *     }
 * }
 * ```
 */
class LoggingInterceptor : HandlerInterceptor {

    @Suppress("UNCHECKED_CAST")
    private val logger: Logger = LoggerUtils.getLogger(LoggingInterceptor::class.java) as Logger

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val startTime = System.currentTimeMillis()
        request.setAttribute("startTime", startTime)

        // MDC에 요청 정보 추가
        MDC.put("requestId", generateRequestId())
        MDC.put("method", request.method)
        MDC.put("path", request.requestURI)
        MDC.put("queryString", request.queryString ?: "")
        MDC.put("remoteAddr", request.remoteAddr)
        MDC.put("userAgent", request.getHeader("User-Agent") ?: "")

        // 요청 로깅
        if (logger.isDebugEnabled) {
            val context = mapOf(
                "method" to request.method,
                "path" to request.requestURI,
                "queryString" to (request.queryString ?: ""),
                "remoteAddr" to request.remoteAddr,
                "userAgent" to (request.getHeader("User-Agent") ?: ""),
            )
            logger.debugWithContext("→ HTTP Request", context)
        }

        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        val startTime = request.getAttribute("startTime") as? Long ?: return
        val executionTime = System.currentTimeMillis() - startTime

        // MDC에 응답 정보 추가
        MDC.put("status", response.status.toString())
        MDC.put("executionTime", executionTime.toString())

        // 응답 로깅
        val context = mapOf(
            "status" to response.status,
            "executionTime" to "${executionTime}ms",
            "path" to request.requestURI,
        )

        when {
            response.status >= 500 -> {
                logger.errorWithContext("← HTTP Response (Server Error)", null, context)
            }
            response.status >= 400 -> {
                logger.warnWithContext("← HTTP Response (Client Error)", context)
            }
            else -> {
                if (logger.isDebugEnabled) {
                    logger.debugWithContext("← HTTP Response (Success)", context)
                }
            }
        }
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        // 예외가 발생한 경우 로깅
        if (ex != null) {
            val context = mapOf(
                "path" to request.requestURI,
                "method" to request.method,
                "status" to response.status,
            )
            logger.errorWithContext("HTTP Request failed", ex, context)
        }

        // MDC 정리
        MDC.clear()
    }

    private fun generateRequestId(): String {
        return java.util.UUID.randomUUID().toString().substring(0, 8)
    }
}
