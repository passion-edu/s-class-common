package com.sclass.common.web.aspect

import com.sclass.common.util.LoggerUtils
import com.sclass.common.web.annotation.LogLevel
import com.sclass.common.web.annotation.Loggable
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * 로깅 AOP Aspect
 *
 * @Loggable 어노테이션이 붙은 메서드의 자동 로깅을 처리합니다.
 */
@Aspect
@Component
class LoggingAspect {

    @Suppress("UNCHECKED_CAST")
    private val logger: Logger = LoggerUtils.getLogger(LoggingAspect::class.java) as Logger

    @Around("@annotation(loggable) || @within(loggable)")
    fun logMethod(joinPoint: ProceedingJoinPoint, loggable: Loggable?): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val methodName = methodSignature.name
        val className = joinPoint.target.javaClass.simpleName
        val fullMethodName = "$className.$methodName"

        // MDC에 메서드 정보 추가
        MDC.put("method", fullMethodName)
        MDC.put("class", className)

        try {
            // 파라미터 로깅
            if (loggable?.includeParams == true) {
                logParameters(joinPoint, methodSignature, loggable.level)
            }

            // 실행 시간 측정
            val startTime = if (loggable?.measureTime == true) System.currentTimeMillis() else null

            // 메서드 실행
            val result = joinPoint.proceed()

            // 실행 시간 계산
            val executionTime = startTime?.let { System.currentTimeMillis() - it }

            // 반환값 로깅
            if (loggable?.includeResult == true) {
                logResult(methodName, result, loggable.level, executionTime)
            } else if (executionTime != null) {
                logExecutionTime(methodName, executionTime, loggable?.level ?: LogLevel.DEBUG)
            }

            return result
        } catch (e: Throwable) {
            // 예외 로깅
            logException(methodName, e, loggable?.level ?: LogLevel.ERROR)
            throw e
        } finally {
            // MDC 정리
            MDC.remove("method")
            MDC.remove("class")
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

        log(level, "→ {}.{}({})", methodSignature.declaringType.simpleName, methodSignature.name, params)
    }

    private fun logResult(
        methodName: String,
        result: Any?,
        level: LogLevel,
        executionTime: Long?,
    ) {
        val timeStr = executionTime?.let { " (${it}ms)" } ?: ""
        val resultStr = formatValue(result)
        log(level, "← {}({}){}", methodName, resultStr, timeStr)
    }

    private fun logExecutionTime(
        methodName: String,
        executionTime: Long,
        level: LogLevel,
    ) {
        log(level, "← {}() ({}ms)", methodName, executionTime)
    }

    private fun logException(
        methodName: String,
        exception: Throwable,
        level: LogLevel,
    ) {
        when (level) {
            LogLevel.ERROR -> logger.error("✗ {}() - Exception: {}", methodName, exception.message, exception)
            LogLevel.WARN -> logger.warn("✗ {}() - Exception: {}", methodName, exception.message, exception)
            else -> logger.error("✗ {}() - Exception: {}", methodName, exception.message, exception)
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
}
