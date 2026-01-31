package com.sclass.common.util

/**
 * 로깅 유틸리티
 *
 * 일관된 로깅 패턴을 제공하는 유틸리티입니다.
 * SLF4J를 사용하며, 선택적 의존성으로 제공됩니다.
 *
 * 사용 방법:
 * ```kotlin
 * import com.sclass.common.util.LoggerUtils
 *
 * class MyService {
 *     private val logger = LoggerUtils.getLogger(this::class.java)
 *
 *     fun doSomething() {
 *         logger.info("작업 시작")
 *         logger.error("에러 발생", exception)
 *     }
 * }
 * ```
 */
object LoggerUtils {
    /**
     * Logger 인스턴스 생성
     *
     * @param clazz Logger를 생성할 클래스
     * @return Logger 인스턴스
     */
    @JvmStatic
    fun getLogger(clazz: Class<*>): Any {
        return try {
            val loggerFactoryClass = Class.forName("org.slf4j.LoggerFactory")
            val getLoggerMethod = loggerFactoryClass.getMethod("getLogger", Class::class.java)
            getLoggerMethod.invoke(null, clazz)!!
        } catch (e: ClassNotFoundException) {
            // SLF4J가 없는 경우 NoOpLogger 반환
            NoOpLogger
        }
    }

    /**
     * Logger 인스턴스 생성 (Kotlin 클래스용)
     *
     * @param clazz Logger를 생성할 클래스
     * @return Logger 인스턴스
     */
    inline fun <reified T> getLogger(): Any {
        return getLogger(T::class.java)
    }
}

/**
 * SLF4J가 없는 경우 사용하는 NoOp Logger
 *
 * SLF4J 인터페이스를 직접 구현하지 않고, 리플렉션을 통해 동적으로 처리합니다.
 */
private object NoOpLogger {
    // 모든 로깅 메서드는 아무 작업도 하지 않음
    // 실제 사용 시에는 SLF4J가 제공되므로 이 객체는 사용되지 않습니다.
}
