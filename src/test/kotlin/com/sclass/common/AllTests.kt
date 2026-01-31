package com.sclass.common

import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite

/**
 * 모든 테스트를 실행하는 테스트 스위트
 * 
 * 사용법:
 * ```bash
 * ./gradlew test --tests com.sclass.common.AllTests
 * ```
 */
@Suite
@SelectPackages("com.sclass.common")
class AllTests
