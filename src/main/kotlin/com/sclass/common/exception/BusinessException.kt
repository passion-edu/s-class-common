package com.sclass.common.exception

/**
 * 비즈니스 로직 예외
 *
 * 에러 코드와 메시지를 포함하는 예외 클래스입니다.
 * 마이크로서비스 간 일관된 에러 처리를 위해 사용합니다.
 *
 * @param errorCode 에러 코드 (예: "USER_NOT_FOUND", "INVALID_INPUT")
 * @param message 에러 메시지
 */
class BusinessException(
    val errorCode: String,
    message: String,
) : IllegalArgumentException(message) {
    constructor(errorCode: String, message: String, cause: Throwable) : this(errorCode, message) {
        initCause(cause)
    }
}
