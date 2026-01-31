package com.sclass.common.dto

/**
 * 공통 API 응답 래퍼
 * 
 * 모든 REST API 응답을 일관된 형식으로 제공합니다.
 * 
 * @param T 응답 데이터 타입
 * @param success 성공 여부
 * @param data 성공 시 데이터 (실패 시 null)
 * @param error 실패 시 에러 정보 (성공 시 null)
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorResponse? = null,
) {
    companion object {
        /**
         * 성공 응답 생성
         */
        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(
            success = true,
            data = data,
            error = null,
        )

        /**
         * 에러 응답 생성
         */
        fun <T> error(code: String, message: String): ApiResponse<T> =
            ApiResponse(
                success = false,
                data = null,
                error = ErrorResponse(code, message),
            )
    }
}

/**
 * 에러 응답 정보
 * 
 * @param code 에러 코드
 * @param message 에러 메시지
 */
data class ErrorResponse(
    val code: String,
    val message: String,
)
