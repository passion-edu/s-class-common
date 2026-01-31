package com.sclass.common.util

/**
 * 검증 유틸리티
 *
 * 일반적인 검증 로직을 제공하는 유틸리티입니다.
 */
object ValidationUtils {
    /**
     * 이메일 형식 검증
     *
     * @param email 검증할 이메일 주소
     * @return 유효한 이메일 형식인 경우 true
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            return false
        }
        val emailPattern = Regex(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        )
        return emailPattern.matches(email)
    }

    /**
     * 전화번호 형식 검증 (한국 형식)
     *
     * 010-1234-5678, 01012345678, 02-123-4567 등 형식 지원
     *
     * @param phone 검증할 전화번호
     * @return 유효한 전화번호 형식인 경우 true
     */
    fun isValidPhoneNumber(phone: String?): Boolean {
        if (phone.isNullOrBlank()) {
            return false
        }
        // 하이픈 제거 후 숫자만 검증
        val digitsOnly = phone.replace("-", "").replace(" ", "")
        val phonePattern = Regex(
            "^(010|011|016|017|018|019|02|031|032|033|041|042|043|044|" +
                "051|052|053|054|055|061|062|063|064)\\d{7,8}$",
        )
        return phonePattern.matches(digitsOnly)
    }

    /**
     * 문자열이 비어있지 않은지 검증
     *
     * @param value 검증할 문자열
     * @return null이 아니고 공백이 아닌 경우 true
     */
    fun isNotBlank(value: String?): Boolean {
        return !value.isNullOrBlank()
    }

    /**
     * 문자열 길이 검증
     *
     * @param value 검증할 문자열
     * @param min 최소 길이 (기본값: 0)
     * @param max 최대 길이 (기본값: Int.MAX_VALUE)
     * @return 길이가 범위 내인 경우 true
     */
    fun isValidLength(
        value: String?,
        min: Int = 0,
        max: Int = Int.MAX_VALUE,
    ): Boolean {
        if (value == null) {
            return min == 0
        }
        return value.length in min..max
    }

    /**
     * 숫자 범위 검증
     *
     * @param value 검증할 숫자
     * @param min 최소값
     * @param max 최대값
     * @return 범위 내인 경우 true
     */
    fun isInRange(
        value: Number,
        min: Number,
        max: Number,
    ): Boolean {
        return when (value) {
            is Int -> value in min.toInt()..max.toInt()
            is Long -> value in min.toLong()..max.toLong()
            is Double -> value in min.toDouble()..max.toDouble()
            is Float -> value in min.toFloat()..max.toFloat()
            else -> value.toDouble() in min.toDouble()..max.toDouble()
        }
    }

    /**
     * URL 형식 검증
     *
     * @param url 검증할 URL
     * @return 유효한 URL 형식인 경우 true
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }
        val urlPattern = Regex(
            "^https?://([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$",
            RegexOption.IGNORE_CASE,
        )
        return urlPattern.matches(url)
    }
}
