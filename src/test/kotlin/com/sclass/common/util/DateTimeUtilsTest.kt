package com.sclass.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DateTimeUtilsTest {
    @Test
    fun `toStartOfDayInstant should convert LocalDate to start of day Instant`() {
        val date = LocalDate.of(2024, 1, 15)
        val instant = DateTimeUtils.toStartOfDayInstant(date)

        assertNotNull(instant)
        assertEquals("2024-01-15T00:00:00Z", instant.toString())
    }

    @Test
    fun `toStartOfDayInstant should return EPOCH when date is null`() {
        val instant = DateTimeUtils.toStartOfDayInstant(null)
        assertEquals(Instant.EPOCH, instant)
    }

    @Test
    fun `toEndOfDayInstant should convert LocalDate to end of day Instant`() {
        val date = LocalDate.of(2024, 1, 15)
        val instant = DateTimeUtils.toEndOfDayInstant(date)

        assertNotNull(instant)
        // 23:59:59.999999999 UTC
        assertEquals("2024-01-15T23:59:59.999999999Z", instant.toString())
    }

    @Test
    fun `toEndOfDayInstant should return max date when date is null`() {
        val instant = DateTimeUtils.toEndOfDayInstant(null)
        // 2099-12-31 23:59:59 UTC
        assertEquals(Instant.ofEpochSecond(253402300799L), instant)
    }

    @Test
    fun `toInstantRange should convert date range to Instant range`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 31)
        val (start, end) = DateTimeUtils.toInstantRange(startDate, endDate)

        assertEquals("2024-01-01T00:00:00Z", start.toString())
        assertEquals("2024-01-31T23:59:59.999999999Z", end.toString())
    }

    @Test
    fun `toInstantRange should use defaults when dates are null`() {
        val (start, end) = DateTimeUtils.toInstantRange(null, null)

        assertEquals(Instant.EPOCH, start)
        assertEquals(Instant.ofEpochSecond(253402300799L), end)
    }

    @Test
    fun `withDefault should return default when date is null`() {
        val default = LocalDate.of(1900, 1, 1)
        val result = DateTimeUtils.withDefault(null, default)
        assertEquals(default, result)
    }

    @Test
    fun `withDefault should return original date when not null`() {
        val date = LocalDate.of(2024, 1, 15)
        val default = LocalDate.of(1900, 1, 1)
        val result = DateTimeUtils.withDefault(date, default)
        assertEquals(date, result)
    }

    @Test
    fun `withMax should return max when date is null`() {
        val max = LocalDate.of(2099, 12, 31)
        val result = DateTimeUtils.withMax(null, max)
        assertEquals(max, result)
    }

    @Test
    fun `withMax should return original date when not null`() {
        val date = LocalDate.of(2024, 1, 15)
        val max = LocalDate.of(2099, 12, 31)
        val result = DateTimeUtils.withMax(date, max)
        assertEquals(date, result)
    }
}
