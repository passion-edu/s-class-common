package com.sclass.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaginationUtilsTest {
    @Test
    fun `normalizePage should return 1 when page is less than 1`() {
        assertEquals(1, PaginationUtils.normalizePage(0))
        assertEquals(1, PaginationUtils.normalizePage(-1))
        assertEquals(1, PaginationUtils.normalizePage(-100))
    }

    @Test
    fun `normalizePage should return original value when page is 1 or greater`() {
        assertEquals(1, PaginationUtils.normalizePage(1))
        assertEquals(5, PaginationUtils.normalizePage(5))
        assertEquals(100, PaginationUtils.normalizePage(100))
    }

    @Test
    fun `normalizeLimit should return min when limit is less than min`() {
        assertEquals(1, PaginationUtils.normalizeLimit(0))
        assertEquals(1, PaginationUtils.normalizeLimit(-1))
        assertEquals(10, PaginationUtils.normalizeLimit(5, min = 10))
    }

    @Test
    fun `normalizeLimit should return max when limit is greater than max`() {
        assertEquals(100, PaginationUtils.normalizeLimit(200))
        assertEquals(100, PaginationUtils.normalizeLimit(1000))
        assertEquals(50, PaginationUtils.normalizeLimit(100, max = 50))
    }

    @Test
    fun `normalizeLimit should return original value when limit is in range`() {
        assertEquals(20, PaginationUtils.normalizeLimit(20))
        assertEquals(50, PaginationUtils.normalizeLimit(50))
        assertEquals(1, PaginationUtils.normalizeLimit(1))
        assertEquals(100, PaginationUtils.normalizeLimit(100))
    }

    @Test
    fun `normalize should return normalized page and limit`() {
        val (page, limit) = PaginationUtils.normalize(0, 200)
        assertEquals(1, page)
        assertEquals(100, limit)
    }

    @Test
    fun `normalize should respect custom min and max limits`() {
        val (page, limit) = PaginationUtils.normalize(1, 50, minLimit = 10, maxLimit = 30)
        assertEquals(1, page)
        assertEquals(30, limit) // 50이 max인 30보다 크므로 30으로 제한
    }
}
