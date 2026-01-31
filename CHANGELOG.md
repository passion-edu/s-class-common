# Changelog

모든 주요 변경 사항은 이 파일에 문서화됩니다.

형식은 [Keep a Changelog](https://keepachangelog.com/ko/1.0.0/)를 따르며,
이 프로젝트는 [Semantic Versioning](https://semver.org/lang/ko/)을 준수합니다.

## [Unreleased]

### 추가됨
- ULID 유틸리티 (`Ulid`)
- 비즈니스 예외 처리 (`BusinessException`)
- 공통 API 응답 DTO (`ApiResponse`, `ErrorResponse`, `PageResponse`)
- ULID 기반 Value Object 헬퍼 (`UlidValueObject`)
- GlobalExceptionHandler (구조화된 예외 처리 및 로깅)
- 페이징 유틸리티 (`PaginationUtils`)
- 날짜/시간 유틸리티 (`DateTimeUtils`)
- 검증 유틸리티 (`ValidationUtils`)
- 로깅 유틸리티 (`LoggerUtils`, `LoggerExtensions`)
- AOP 기반 메서드 로깅 (`@Loggable`, `LoggingAspect`, `AsyncLoggingAspect`)
- HTTP 요청/응답 로깅 (`LoggingInterceptor`, `AsyncLoggingInterceptor`)
- 비동기 로깅 지원 (Pino 스타일, 성능 최적화)
- 구조화된 JSON 로깅 지원

### 변경됨
- 최소한의 의존성만 포함하도록 설계
- 선택적 의존성은 `compileOnly`로 선언
- Gradle Version Catalog 사용

### 문서화
- README.md: 사용 가이드
- DEPLOYMENT.md: 배포 가이드
- LOGGING_GUIDE.md: 로깅 가이드
- PERFORMANCE_OPTIMIZATION.md: 성능 최적화 가이드
- ARCHITECTURE.md: 아키텍처 가이드
- OPTIMIZATION.md: 최적화 가이드

---

## [1.0.0-SNAPSHOT] - 2024-12-XX

### 추가됨
- 초기 릴리즈
- 기본 유틸리티 및 공통 모듈

[Unreleased]: https://github.com/passion-edu/s-class-common/compare/v1.0.0...HEAD
[1.0.0-SNAPSHOT]: https://github.com/passion-edu/s-class-common/releases/tag/v1.0.0-SNAPSHOT
