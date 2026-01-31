package com.sclass.common.util

/**
 * 페이징 유틸리티
 * 
 * 컨트롤러에서 페이징 파라미터를 검증하고 정규화하는 유틸리티입니다.
 */
object PaginationUtils {
    /**
     * 페이지 번호 정규화
     * 
     * 페이지 번호가 1보다 작으면 1로 설정합니다.
     * 
     * @param page 원본 페이지 번호
     * @return 정규화된 페이지 번호 (최소 1)
     */
    fun normalizePage(page: Int): Int {
        return page.coerceAtLeast(1)
    }

    /**
     * 페이지 크기 정규화
     * 
     * 페이지 크기를 지정된 범위 내로 제한합니다.
     * 
     * @param limit 원본 페이지 크기
     * @param min 최소 페이지 크기 (기본값: 1)
     * @param max 최대 페이지 크기 (기본값: 100)
     * @return 정규화된 페이지 크기
     */
    fun normalizeLimit(limit: Int, min: Int = 1, max: Int = 100): Int {
        return limit.coerceIn(min, max)
    }

    /**
     * 페이징 파라미터 정규화
     * 
     * 페이지 번호와 페이지 크기를 한 번에 정규화합니다.
     * 
     * @param page 원본 페이지 번호
     * @param limit 원본 페이지 크기
     * @param minLimit 최소 페이지 크기 (기본값: 1)
     * @param maxLimit 최대 페이지 크기 (기본값: 100)
     * @return 정규화된 페이징 파라미터 (page, limit)
     */
    fun normalize(
        page: Int,
        limit: Int,
        minLimit: Int = 1,
        maxLimit: Int = 100,
    ): Pair<Int, Int> {
        return Pair(
            normalizePage(page),
            normalizeLimit(limit, minLimit, maxLimit),
        )
    }
}
