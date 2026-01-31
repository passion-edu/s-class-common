package com.sclass.common.web

import com.sclass.common.exception.BusinessException
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {
    private lateinit var handler: GlobalExceptionHandler
    private lateinit var mockRequest: HttpServletRequest

    @BeforeEach
    fun setUp() {
        handler = GlobalExceptionHandler()
        mockRequest = mock(HttpServletRequest::class.java)
        whenever(mockRequest.requestURI).thenReturn("/api/test")
        whenever(mockRequest.method).thenReturn("GET")
    }

    @Test
    fun `BusinessException 처리 테스트`() {
        val exception = BusinessException(
            errorCode = "USER_NOT_FOUND",
            message = "사용자를 찾을 수 없습니다",
        )

        val response = handler.handleBusinessException(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.success)
        assertEquals("USER_NOT_FOUND", response.body!!.error?.code)
        assertEquals("사용자를 찾을 수 없습니다", response.body!!.error?.message)
        assertNull(response.body!!.data)
    }

    @Test
    fun `IllegalArgumentException 처리 테스트`() {
        val exception = IllegalArgumentException("잘못된 인자입니다")

        val response = handler.handleIllegalArgument(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.success)
        assertEquals("INVALID_ARGUMENT", response.body!!.error?.code)
        assertEquals("잘못된 인자입니다", response.body!!.error?.message)
    }

    @Test
    fun `IllegalArgumentException 메시지가 null인 경우 테스트`() {
        val exception = IllegalArgumentException()

        val response = handler.handleIllegalArgument(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid argument", response.body!!.error?.message)
    }

    @Test
    fun `IllegalStateException 처리 테스트`() {
        val exception = IllegalStateException("잘못된 상태입니다")

        val response = handler.handleIllegalState(exception, mockRequest)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.success)
        assertEquals("ILLEGAL_STATE", response.body!!.error?.code)
        assertEquals("잘못된 상태입니다", response.body!!.error?.message)
    }

    @Test
    fun `RuntimeException 처리 테스트`() {
        val exception = RuntimeException("런타임 오류")

        val response = handler.handleRuntimeException(exception, mockRequest)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.success)
        assertEquals("INTERNAL_ERROR", response.body!!.error?.code)
        assertEquals("런타임 오류", response.body!!.error?.message)
    }

    @Test
    fun `RuntimeException 메시지가 null인 경우 테스트`() {
        val exception = RuntimeException()

        val response = handler.handleRuntimeException(exception, mockRequest)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal server error", response.body!!.error?.message)
    }
}
