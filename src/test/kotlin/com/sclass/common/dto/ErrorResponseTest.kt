package com.sclass.common.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class ErrorResponseTest {
    @Test
    fun `ErrorResponse 생성 테스트`() {
        val errorResponse = ErrorResponse(
            code = "ERROR_CODE",
            message = "에러 메시지",
        )

        assertEquals("ERROR_CODE", errorResponse.code)
        assertEquals("에러 메시지", errorResponse.message)
    }

    @Test
    fun `ErrorResponse equals 테스트`() {
        val error1 = ErrorResponse("CODE", "Message")
        val error2 = ErrorResponse("CODE", "Message")
        val error3 = ErrorResponse("DIFFERENT_CODE", "Message")

        assertEquals(error1, error2)
        assertNotEquals(error1, error3)
    }

    @Test
    fun `ErrorResponse hashCode 테스트`() {
        val error1 = ErrorResponse("CODE", "Message")
        val error2 = ErrorResponse("CODE", "Message")

        assertEquals(error1.hashCode(), error2.hashCode())
    }
}
