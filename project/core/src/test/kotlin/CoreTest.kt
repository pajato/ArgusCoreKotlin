package test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BasicJunit5Test {
    companion object;

    @Test
    fun simpleTestCase() {
        println("A simple test case.")

        val numberTwo = 2
        assertEquals(2, numberTwo)
    }
}
