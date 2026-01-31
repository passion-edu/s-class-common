package com.sclass.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * 날짜/시간 유틸리티
 * 
 * 날짜와 시간 변환 및 포맷팅을 위한 유틸리티입니다.
 */
object DateTimeUtils {
    /**
     * LocalDate를 해당 날짜의 시작 시간(00:00:00) Instant로 변환
     * 
     * @param date 변환할 LocalDate (null인 경우 EPOCH 반환)
     * @param zoneOffset 타임존 오프셋 (기본값: UTC)
     * @return 해당 날짜의 00:00:00 UTC Instant
     */
    fun toStartOfDayInstant(
        date: LocalDate?,
        zoneOffset: ZoneOffset = ZoneOffset.UTC,
    ): Instant {
        return date?.atStartOfDay()?.toInstant(zoneOffset) ?: Instant.EPOCH
    }

    /**
     * LocalDate를 해당 날짜의 종료 시간(23:59:59.999999999) Instant로 변환
     * 
     * @param date 변환할 LocalDate (null인 경우 2099-12-31 23:59:59 UTC 반환)
     * @param zoneOffset 타임존 오프셋 (기본값: UTC)
     * @return 해당 날짜의 23:59:59.999999999 UTC Instant
     */
    fun toEndOfDayInstant(
        date: LocalDate?,
        zoneOffset: ZoneOffset = ZoneOffset.UTC,
    ): Instant {
        return date?.atTime(23, 59, 59, 999_999_999)?.toInstant(zoneOffset)
            ?: Instant.ofEpochSecond(253402300799L) // 2099-12-31 23:59:59 UTC
    }

    /**
     * 날짜 범위를 Instant 범위로 변환
     * 
     * @param startDate 시작 날짜 (null인 경우 EPOCH)
     * @param endDate 종료 날짜 (null인 경우 2099-12-31 23:59:59 UTC)
     * @param zoneOffset 타임존 오프셋 (기본값: UTC)
     * @return (시작 Instant, 종료 Instant) 쌍
     */
    fun toInstantRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        zoneOffset: ZoneOffset = ZoneOffset.UTC,
    ): Pair<Instant, Instant> {
        return Pair(
            toStartOfDayInstant(startDate, zoneOffset),
            toEndOfDayInstant(endDate, zoneOffset),
        )
    }

    /**
     * LocalDate를 기본값으로 대체
     * 
     * @param date 원본 날짜
     * @param defaultDate 기본값 (기본값: 1900-01-01)
     * @return date가 null이면 defaultDate, 아니면 date
     */
    fun withDefault(
        date: LocalDate?,
        defaultDate: LocalDate = LocalDate.of(1900, 1, 1),
    ): LocalDate {
        return date ?: defaultDate
    }

    /**
     * LocalDate를 최대값으로 대체
     * 
     * @param date 원본 날짜
     * @param maxDate 최대값 (기본값: 2099-12-31)
     * @return date가 null이면 maxDate, 아니면 date
     */
    fun withMax(
        date: LocalDate?,
        maxDate: LocalDate = LocalDate.of(2099, 12, 31),
    ): LocalDate {
        return date ?: maxDate
    }
}
