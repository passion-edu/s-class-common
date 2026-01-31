package com.sclass.common.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ApiResponseTest {
    @Test
    fun `성공 응답 생성 테스트`() {
        val response = ApiResponse.success("test data")

        assertTrue(response.success)
        assertEquals("test data", response.data)
        assertNull(response.error)
    }

    @Test
    fun `성공 응답 null 데이터 테스트`() {
        val response = ApiResponse.success<String>(null)

        assertTrue(response.success)
        assertNull(response.data)
        assertNull(response.error)
    }

    @Test
    fun `에러 응답 생성 테스트`() {
        val response = ApiResponse.error<String>("ERROR_CODE", "Error message")

        assertFalse(response.success)
        assertNull(response.data)
        assertNotNull(response.error)
        assertEquals("ERROR_CODE", response.error?.code)
        assertEquals("Error message", response.error?.message)
    }

    @Test
    fun `복잡한 데이터 타입 성공 응답 테스트`() {
        data class User(val id: String, val name: String)
        val user = User("123", "John")

        val response = ApiResponse.success(user)

        assertTrue(response.success)
        assertEquals(user, response.data)
        assertNull(response.error)
    }

    @Test
    fun `리스트 데이터 성공 응답 테스트`() {
        val list = listOf("item1", "item2", "item3")
        val response = ApiResponse.success(list)

        assertTrue(response.success)
        assertEquals(list, response.data)
        assertEquals(3, response.data?.size)
    }

    @Test
    fun `ApiResponse equals 테스트`() {
        val response1 = ApiResponse.success("data")
        val response2 = ApiResponse.success("data")
        val response3 = ApiResponse.error<String>("CODE", "Message")

        assertEquals(response1, response2)
        assertNotEquals(response1, response3)
    }
}
