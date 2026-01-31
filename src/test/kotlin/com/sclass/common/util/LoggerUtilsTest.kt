package com.sclass.common.util

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LoggerUtilsTest {
    @Test
    fun `getLogger should return logger instance`() {
        val logger = LoggerUtils.getLogger(LoggerUtilsTest::class.java)
        assertNotNull(logger)
    }

    @Test
    fun `getLogger with reified type should return logger instance`() {
        val logger = LoggerUtils.getLogger<LoggerUtilsTest>()
        assertNotNull(logger)
    }
}
