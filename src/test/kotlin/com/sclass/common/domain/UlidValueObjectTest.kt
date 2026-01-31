package com.sclass.common.domain

import com.sclass.common.util.Ulid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// 테스트용 Value Object
data class TestId(val value: String) {
    companion object : UlidValueObjectCompanion<TestId> {
        override fun create(value: String): TestId = TestId(value)
    }
}

class UlidValueObjectTest {
    @Test
    fun `of 메서드로 유효한 ULID 생성 테스트`() {
        val validUlid = Ulid.generate()
        val testId = TestId.of(validUlid)

        assertEquals(validUlid, testId.value)
    }

    @Test
    fun `of 메서드로 유효하지 않은 ULID 생성 시 예외 발생 테스트`() {
        val invalidUlid = "invalid-ulid"

        assertThrows(IllegalArgumentException::class.java) {
            TestId.of(invalidUlid)
        }
    }

    @Test
    fun `from 메서드로 검증 없이 생성 테스트`() {
        val anyString = "any-string"
        val testId = TestId.from(anyString)

        assertEquals(anyString, testId.value)
    }

    @Test
    fun `generate 메서드로 새 ULID 생성 테스트`() {
        val testId = TestId.generate()

        assertTrue(Ulid.isValid(testId.value))
    }

    @Test
    fun `equals 테스트`() {
        val id1 = TestId("test-id")
        val id2 = TestId("test-id")
        val id3 = TestId("different-id")

        assertEquals(id1, id2)
        assertNotEquals(id1, id3)
    }
}
