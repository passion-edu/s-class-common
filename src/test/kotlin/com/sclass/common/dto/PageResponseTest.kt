package com.sclass.common.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class PageResponseTest {
    @Test
    fun `PageResponse from Spring Data Page 테스트`() {
        val content = listOf("item1", "item2", "item3")
        val pageable: Pageable = PageRequest.of(0, 10)
        val totalElements = 25L
        val page = PageImpl(content, pageable, totalElements)

        val pageResponse = PageResponse.from(page)

        assertEquals(content, pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(10, pageResponse.size)
        assertEquals(25L, pageResponse.totalElements)
        assertEquals(3, pageResponse.totalPages) // 25 / 10 = 2.5 -> 3
        assertTrue(pageResponse.first)
        assertFalse(pageResponse.last)
        assertEquals(3, pageResponse.numberOfElements)
    }

    @Test
    fun `빈 페이지 테스트`() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(emptyList<String>(), pageable, 0L)

        val pageResponse = PageResponse.from(page)

        assertTrue(pageResponse.content.isEmpty())
        assertEquals(0, pageResponse.page)
        assertEquals(10, pageResponse.size)
        assertEquals(0L, pageResponse.totalElements)
        assertEquals(0, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertTrue(pageResponse.last)
        assertEquals(0, pageResponse.numberOfElements)
    }

    @Test
    fun `마지막 페이지 테스트`() {
        val content = listOf("item1", "item2")
        val pageable: Pageable = PageRequest.of(2, 10) // 3번째 페이지 (0-based)
        val totalElements = 22L
        val page = PageImpl(content, pageable, totalElements)

        val pageResponse = PageResponse.from(page)

        assertEquals(content, pageResponse.content)
        assertEquals(2, pageResponse.page)
        assertFalse(pageResponse.first)
        assertTrue(pageResponse.last)
        assertEquals(2, pageResponse.numberOfElements)
    }

    @Test
    fun `중간 페이지 테스트`() {
        val content = listOf("item1", "item2", "item3", "item4", "item5")
        val pageable: Pageable = PageRequest.of(1, 10) // 2번째 페이지
        val totalElements = 25L
        val page = PageImpl(content, pageable, totalElements)

        val pageResponse = PageResponse.from(page)

        assertEquals(1, pageResponse.page)
        assertFalse(pageResponse.first)
        assertFalse(pageResponse.last)
        assertEquals(5, pageResponse.numberOfElements)
    }
}
