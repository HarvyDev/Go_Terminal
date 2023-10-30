package isel.tds.go.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardTest {
    @Test
    fun `show empty board`() {
        val sut = Board()
        sut.show()
    }


    @Test
    fun `show board with pieces`() {
        var sut = Board()
        sut = sut.play(Position(3,'C'))
        sut = sut.play(Position(3,'D'))
        assertFalse(sut.canPlay(Position(3,'C')))
        assertFalse(sut.canPlay(Position(3, 'D')))
        sut.show()
    }



    @Test
    fun `can play`() {
        val sut = Board()
//        sut.boardCells[Position(0,'A')] = Piece.BLACK
//        sut.boardCells[Position(1, 'A')] to Piece.WHITE
        assertFalse(sut.canPlay(Position(0,'A')))
        assertTrue(sut.canPlay(Position(0, 'A')))
        assertFalse(sut.canPlay(Position(1, 'A')))
    }
}