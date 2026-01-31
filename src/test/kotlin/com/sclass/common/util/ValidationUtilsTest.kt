package com.sclass.common.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidationUtilsTest {
    @Test
    fun `isValidEmail should return true for valid emails`() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user.name@example.co.kr"))
        assertTrue(ValidationUtils.isValidEmail("user+tag@example.com"))
    }

    @Test
    fun `isValidEmail should return false for invalid emails`() {
        assertFalse(ValidationUtils.isValidEmail(null))
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("invalid"))
        assertFalse(ValidationUtils.isValidEmail("@example.com"))
        assertFalse(ValidationUtils.isValidEmail("test@"))
    }

    @Test
    fun `isValidPhoneNumber should return true for valid phone numbers`() {
        assertTrue(ValidationUtils.isValidPhoneNumber("010-1234-5678"))
        assertTrue(ValidationUtils.isValidPhoneNumber("01012345678"))
        assertTrue(ValidationUtils.isValidPhoneNumber("02-123-4567"))
        assertTrue(ValidationUtils.isValidPhoneNumber("02-1234-5678"))
    }

    @Test
    fun `isValidPhoneNumber should return false for invalid phone numbers`() {
        assertFalse(ValidationUtils.isValidPhoneNumber(null))
        assertFalse(ValidationUtils.isValidPhoneNumber(""))
        assertFalse(ValidationUtils.isValidPhoneNumber("123"))
        assertFalse(ValidationUtils.isValidPhoneNumber("999-9999-9999"))
    }

    @Test
    fun `isNotBlank should return true for non-blank strings`() {
        assertTrue(ValidationUtils.isNotBlank("test"))
        assertTrue(ValidationUtils.isNotBlank("  test  "))
    }

    @Test
    fun `isNotBlank should return false for blank strings`() {
        assertFalse(ValidationUtils.isNotBlank(null))
        assertFalse(ValidationUtils.isNotBlank(""))
        assertFalse(ValidationUtils.isNotBlank("   "))
    }

    @Test
    fun `isValidLength should return true when length is in range`() {
        assertTrue(ValidationUtils.isValidLength("test", min = 1, max = 10))
        assertTrue(ValidationUtils.isValidLength("test", min = 4, max = 4))
    }

    @Test
    fun `isValidLength should return false when length is out of range`() {
        assertFalse(ValidationUtils.isValidLength("test", min = 5, max = 10))
        assertFalse(ValidationUtils.isValidLength("test", min = 1, max = 3))
    }

    @Test
    fun `isInRange should return true when value is in range`() {
        assertTrue(ValidationUtils.isInRange(5, 1, 10))
        assertTrue(ValidationUtils.isInRange(1L, 1L, 10L))
        assertTrue(ValidationUtils.isInRange(5.5, 1.0, 10.0))
    }

    @Test
    fun `isInRange should return false when value is out of range`() {
        assertFalse(ValidationUtils.isInRange(0, 1, 10))
        assertFalse(ValidationUtils.isInRange(11, 1, 10))
    }

    @Test
    fun `isValidUrl should return true for valid URLs`() {
        assertTrue(ValidationUtils.isValidUrl("http://example.com"))
        assertTrue(ValidationUtils.isValidUrl("https://example.com"))
        assertTrue(ValidationUtils.isValidUrl("https://www.example.com/path"))
    }

    @Test
    fun `isValidUrl should return false for invalid URLs`() {
        assertFalse(ValidationUtils.isValidUrl(null))
        assertFalse(ValidationUtils.isValidUrl(""))
        assertFalse(ValidationUtils.isValidUrl("invalid"))
        assertFalse(ValidationUtils.isValidUrl("ftp://example.com"))
    }
}
