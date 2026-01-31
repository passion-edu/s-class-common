package com.sclass.common.util

import com.github.f4b6a3.ulid.UlidCreator

/**
 * ULID 유틸리티
 *
 * ULID (Universally Unique Lexicographically Sortable Identifier)는
 * UUID의 대안으로 시간순 정렬이 가능한 고유 식별자입니다.
 *
 * @see <a href="https://github.com/ulid/spec">ULID Specification</a>
 */
object Ulid {
    /**
     * 새로운 ULID 생성
     *
     * @return 26자리 Base32 인코딩된 ULID 문자열
     */
    fun generate(): String {
        return UlidCreator.getUlid().toString()
    }

    /**
     * ULID 검증
     * ULID는 26자리 문자열이며, Base32 인코딩된 형식입니다.
     *
     * @param ulid 검증할 ULID 문자열
     * @return 유효한 ULID인 경우 true
     */
    fun isValid(ulid: String): Boolean {
        if (ulid.length != 26) {
            return false
        }
        // Base32 문자셋 검증 (0-9, A-Z 제외 I, L, O, U)
        val base32Pattern = Regex("^[0-9A-HJKMNP-TV-Z]{26}$")
        return base32Pattern.matches(ulid)
    }

    /**
     * String을 ULID로 파싱 (검증 포함)
     *
     * @param ulid 파싱할 ULID 문자열
     * @return 검증된 ULID 문자열
     * @throws IllegalArgumentException 유효하지 않은 ULID인 경우
     */
    fun parse(ulid: String): String {
        if (!isValid(ulid)) {
            throw IllegalArgumentException("Invalid ULID: $ulid")
        }
        return ulid
    }
}
