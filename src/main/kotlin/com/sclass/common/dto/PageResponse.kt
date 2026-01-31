package com.sclass.common.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

/**
 * 페이지네이션 응답
 * 
 * Spring Data의 Page를 표준화된 응답 형식으로 변환합니다.
 * 
 * @param T 페이지 내용 타입
 * @param content 데이터 목록
 * @param page 현재 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @param totalElements 전체 요소 개수
 * @param totalPages 전체 페이지 수
 * @param first 첫 페이지 여부
 * @param last 마지막 페이지 여부
 * @param numberOfElements 현재 페이지의 실제 요소 개수
 */
@Schema(description = "페이지네이션 응답")
data class PageResponse<T>(
    @Schema(description = "데이터 목록")
    val content: List<T>,

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    val page: Int,

    @Schema(description = "페이지 크기", example = "20")
    val size: Int,

    @Schema(description = "전체 요소 개수", example = "100")
    val totalElements: Long,

    @Schema(description = "전체 페이지 수", example = "5")
    val totalPages: Int,

    @Schema(description = "첫 페이지 여부", example = "true")
    val first: Boolean,

    @Schema(description = "마지막 페이지 여부", example = "false")
    val last: Boolean,

    @Schema(description = "현재 페이지의 실제 요소 개수", example = "20")
    val numberOfElements: Int,
) {
    companion object {
        /**
         * Spring Data Page를 PageResponse로 변환
         */
        fun <T> from(page: Page<T>): PageResponse<T> {
            return PageResponse(
                content = page.content,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                first = page.isFirst,
                last = page.isLast,
                numberOfElements = page.numberOfElements,
            )
        }
    }
}
