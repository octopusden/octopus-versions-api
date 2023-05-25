package utils

import org.octopusden.utils.expandContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringUtilsKtTest {
    @Test
    fun testExpandVariables() {
        assertEquals("bar", "\$foo".expandContext(mapOf("foo" to "bar")))
        assertEquals("bardummy_bizbaz", "\${var1}dummy_\$var2\$var3".expandContext(
                mapOf("var1" to "bar", "var2" to "biz", "var3" to "baz")))
        assertEquals("C_10_11_12", "C_\${major02}_\${minor02}_\${service02}".expandContext(
                mapOf("major02" to "10", "minor02" to "11", "service02" to "12")
        ))
        assertEquals("\${env.PATH}", "\${env.PATH}".expandContext(mapOf("env" to "E", "PATH" to "P", "env.PATH" to "EP", "\${env.PATH}" to "PEP"), false))
        assertEquals(System.getenv("PATH"), "\${env.PATH}".expandContext(mapOf("env" to "E", "PATH" to "P", "env.PATH" to "EP", "\${env.PATH}" to "PEP"), true))
        assertEquals("\${env.PATH}", "\${env.PATH}".expandContext(mapOf("env" to "E", "PATH" to "P", "env.PATH" to "EP", "\${env.PATH}" to "PEP")))
    }
}
