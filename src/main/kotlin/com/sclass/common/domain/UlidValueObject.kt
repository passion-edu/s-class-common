package com.sclass.common.domain

import com.sclass.common.util.Ulid

/**
 * ULID 기반 Value Object를 위한 유틸리티 함수들
 * 
 * Value Object의 companion object에서 사용할 수 있는 확장 함수를 제공합니다.
 * 
 * 사용 예시:
 * ```kotlin
 * data class UserId(val value: String) {
 *     companion object {
 *         fun of(value: String): UserId = UserId(Ulid.parse(value))
 *         fun from(value: String): UserId = UserId(value)
 *         fun generate(): UserId = UserId(Ulid.generate())
 *     }
 * }
 * ```
 * 
 * 또는 이 유틸리티 함수를 사용:
 * ```kotlin
 * data class UserId(val value: String) {
 *     companion object : UlidValueObjectCompanion<UserId> {
 *         override fun create(value: String): UserId = UserId(value)
 *     }
 * }
 * ```
 */
interface UlidValueObjectCompanion<T> {
    /**
     * Value Object 인스턴스 생성
     * 
     * 하위 클래스에서 구현해야 합니다.
     */
    fun create(value: String): T

    /**
     * ULID 검증을 수행하여 Value Object 생성
     * 
     * @param value ULID 문자열
     * @return 검증된 Value Object
     * @throws IllegalArgumentException 유효하지 않은 ULID인 경우
     */
    fun of(value: String): T {
        return create(Ulid.parse(value))
    }

    /**
     * 검증 없이 Value Object 생성
     * 
     * 데이터베이스에서 읽어온 값 등 이미 검증된 값에 사용합니다.
     * 
     * @param value ULID 문자열
     * @return Value Object
     */
    fun from(value: String): T {
        return create(value)
    }

    /**
     * 새로운 ULID를 생성하여 Value Object 생성
     * 
     * @return 새로 생성된 Value Object
     */
    fun generate(): T {
        return create(Ulid.generate())
    }
}
