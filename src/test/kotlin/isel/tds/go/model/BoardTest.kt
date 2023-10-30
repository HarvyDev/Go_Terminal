package isel.tds.go.model

import kotlin.test.Test

class BoardTest {
    @Test
    fun `test show empty board`() {
        val sut = Board()
        sut.show()
    }
}