package com.sclass.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UlidTest {
    @Test
    fun `ULID 생성 테스트`() {
        val ulid = Ulid.generate()

        assertNotNull(ulid)
        assertEquals(26, ulid.length)
        assertTrue(Ulid.isValid(ulid))
    }

    @Test
    fun `유효한 ULID 검증 테스트`() {
        val validUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAV"
        assertTrue(Ulid.isValid(validUlid))
    }

    @Test
    fun `유효하지 않은 ULID 검증 테스트 - 길이 부족`() {
        val invalidUlid = "01ARZ3NDEKTSV4RRFFQ69G5FA"
        assertFalse(Ulid.isValid(invalidUlid))
    }

    @Test
    fun `유효하지 않은 ULID 검증 테스트 - 잘못된 문자`() {
        val invalidUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAI" // I는 Base32에 없음
        assertFalse(Ulid.isValid(invalidUlid))
    }

    @Test
    fun `ULID 파싱 테스트 - 유효한 ULID`() {
        val validUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAV"
        val parsed = Ulid.parse(validUlid)
        assertEquals(validUlid, parsed)
    }

    @Test
    fun `ULID 파싱 테스트 - 유효하지 않은 ULID`() {
        val invalidUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAI"

        assertThrows(IllegalArgumentException::class.java) {
            Ulid.parse(invalidUlid)
        }
    }
}
