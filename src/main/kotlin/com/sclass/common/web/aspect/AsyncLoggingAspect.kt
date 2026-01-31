package com.sclass.common.web.aspect

import com.sclass.common.util.LoggerUtils
import com.sclass.common.web.annotation.Loggable
import com.sclass.common.web.annotation.LogLevel
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 비동기 로깅 AOP Aspect (Pino 스타일)
 * 
 * @Loggable 어노테이션이 붙은 메서드의 자동 로깅을 비동기로 처리합니다.
 * API 성능에 영향을 주지 않도록 비동기 로깅을 사용합니다.
 */
@Aspect
@Component
class AsyncLoggingAspect {

    @Suppress("UNCHECKED_CAST")
    private val logger: Logger = LoggerUtils.getLogger(AsyncLoggingAspect::class.java) as Logger
    
    // 비동기 로깅을 위한 Executor (데몬 스레드로 생성)
    private val executor: Executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        { r ->
            val thread = Thread(r, "async-logger-aspect-${System.currentTimeMillis()}")
            thread.isDaemon = true
            thread
        }
    )

    @Around("@annotation(loggable) || @within(loggable)")
    fun logMethod(joinPoint: ProceedingJoinPoint, loggable: Loggable?): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val methodName = methodSignature.name
        val className = joinPoint.target.javaClass.simpleName
        val fullMethodName = "$className.$methodName"

        // 로그 레벨 확인 (불필요한 로깅 방지)
        val logLevel = loggable?.level ?: LogLevel.DEBUG
        if (!isLogLevelEnabled(logLevel)) {
            // 로그 레벨이 비활성화되어 있으면 바로 실행 (성능 최적화)
            return joinPoint.proceed()
        }

        // MDC에 메서드 정보 추가 (동기적으로만 설정)
        MDC.put("method", fullMethodName)
        MDC.put("class", className)

        try {
            // 파라미터 로깅 (비동기)
            if (loggable?.includeParams == true) {
                CompletableFuture.runAsync({
                    logParameters(joinPoint, methodSignature, logLevel)
                }, executor)
            }

            // 실행 시간 측정
            val startTime = if (loggable?.measureTime == true) System.currentTimeMillis() else null

            // 메서드 실행
            val result = joinPoint.proceed()

            // 실행 시간 계산
            val executionTime = startTime?.let { System.currentTimeMillis() - it }

            // 반환값 로깅 (비동기)
            if (loggable?.includeResult == true || executionTime != null) {
                CompletableFuture.runAsync({
                    logResult(methodName, result, logLevel, executionTime, loggable?.includeResult == true)
                }, executor).exceptionally { null } // 로깅 실패는 무시
            }

            return result
        } catch (e: Throwable) {
            // 예외는 동기적으로 로깅 (중요한 정보)
            logException(methodName, e, logLevel)
            throw e
        } finally {
            // MDC 정리
            MDC.remove("method")
            MDC.remove("class")
        }
    }

    private fun isLogLevelEnabled(level: LogLevel): Boolean {
        return when (level) {
            LogLevel.TRACE -> logger.isTraceEnabled
            LogLevel.DEBUG -> logger.isDebugEnabled
            LogLevel.INFO -> logger.isInfoEnabled
            LogLevel.WARN -> logger.isWarnEnabled
            LogLevel.ERROR -> logger.isErrorEnabled
        }
    }

    private fun logParameters(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        level: LogLevel,
    ) {
        val paramNames = methodSignature.parameterNames
        val paramValues = joinPoint.args
        val params = paramNames.mapIndexed { index, name ->
            "$name=${formatValue(paramValues[index])}"
        }.joinToString(", ")

        val context = mapOf(
            "type" to "method_entry",
            "class" to methodSignature.declaringType.simpleName,
            "method" to methodSignature.name,
            "params" to params,
        )

        log(level, "Method Entry | {}", formatAsJson(context))
    }

    private fun logResult(
        methodName: String,
        result: Any?,
        level: LogLevel,
        executionTime: Long?,
        includeResult: Boolean,
    ) {
        val context = mutableMapOf<String, Any?>(
            "type" to "method_exit",
            "method" to methodName,
        )

        if (includeResult) {
            context["result"] = formatValue(result)
        }

        if (executionTime != null) {
            context["executionTime"] = "${executionTime}ms"
        }

        log(level, "Method Exit | {}", formatAsJson(context))
    }

    private fun logException(
        methodName: String,
        exception: Throwable,
        level: LogLevel,
    ) {
        val context = mapOf(
            "type" to "method_exception",
            "method" to methodName,
            "error" to mapOf(
                "type" to exception.javaClass.simpleName,
                "message" to (exception.message ?: ""),
            ),
        )

        when (level) {
            LogLevel.ERROR -> logger.error("Method Exception | {}", formatAsJson(context), exception)
            LogLevel.WARN -> logger.warn("Method Exception | {}", formatAsJson(context), exception)
            else -> logger.error("Method Exception | {}", formatAsJson(context), exception)
        }
    }

    private fun log(level: LogLevel, message: String, vararg args: Any?) {
        when (level) {
            LogLevel.TRACE -> if (logger.isTraceEnabled) logger.trace(message, *args)
            LogLevel.DEBUG -> if (logger.isDebugEnabled) logger.debug(message, *args)
            LogLevel.INFO -> if (logger.isInfoEnabled) logger.info(message, *args)
            LogLevel.WARN -> if (logger.isWarnEnabled) logger.warn(message, *args)
            LogLevel.ERROR -> if (logger.isErrorEnabled) logger.error(message, *args)
        }
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"$value\""
            is Collection<*> -> "[${value.size} items]"
            is Map<*, *> -> "{${value.size} entries}"
            is Array<*> -> "[${value.size} items]"
            else -> value.toString()
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
