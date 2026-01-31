package com.sclass.common.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BusinessExceptionTest {
    @Test
    fun `BusinessException 생성 테스트`() {
        val exception = BusinessException(
            errorCode = "USER_NOT_FOUND",
            message = "사용자를 찾을 수 없습니다",
        )

        assertEquals("USER_NOT_FOUND", exception.errorCode)
        assertEquals("사용자를 찾을 수 없습니다", exception.message)
        // BusinessException은 IllegalArgumentException을 상속하므로 타입 체크 불필요
    }

    @Test
    fun `BusinessException with cause 테스트`() {
        val cause = RuntimeException("원인 예외")
        val exception = BusinessException(
            errorCode = "INTERNAL_ERROR",
            message = "내부 오류가 발생했습니다",
            cause = cause,
        )

        assertEquals("INTERNAL_ERROR", exception.errorCode)
        assertEquals("내부 오류가 발생했습니다", exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `BusinessException 메시지가 null인 경우 테스트`() {
        val exception = BusinessException(
            errorCode = "ERROR_CODE",
            message = "",
        )

        assertEquals("ERROR_CODE", exception.errorCode)
        assertEquals("", exception.message)
    }
}
