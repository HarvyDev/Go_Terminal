package isel.tds.go.model

import kotlin.test.*

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
    fun `test play edge cases exception`() {
        var sut = Board()
        assertFailsWith<IllegalArgumentException>{
            sut = sut.play(Position(-1, 'A'))
        }
        assertFailsWith<IllegalArgumentException>{
            sut = sut.play(Position(0, 'A' + BOARD_SIZE))
        }
        assertFailsWith<IllegalArgumentException>{
            sut = sut.play(Position(BOARD_SIZE, 'A' + BOARD_SIZE))
        }
        assertFailsWith<IllegalArgumentException>{
            sut = sut.play(Position(0, 'A' - 1))
        }
    }
    @Test
    fun `test play edge cases`() {
        var sut = Board()
        sut = sut.play("9a".toPosition())
        sut = sut.play("1a".toPosition())
        sut = sut.play("9i".toPosition())
        sut = sut.play("1i".toPosition())
        assertEquals(sut.boardCells["9a".toPosition()], Piece.BLACK)
        assertEquals(sut.boardCells["1a".toPosition()], Piece.WHITE)
        assertEquals(sut.boardCells["9i".toPosition()], Piece.BLACK)
        assertEquals(sut.boardCells["1i".toPosition()], Piece.WHITE)
    }


    @Test
    fun `can play`() {
        var sut = Board()
        sut = sut.play(Position(1, 'A'))
        sut = sut.play(Position(2, 'A'))
        assertFalse(sut.canPlay(Position(1,'A')))
        assertFalse(sut.canPlay(Position(2, 'A')))
    }

    @Test
    fun `kill stone in bottom right corner`() {
        var sut = Board()
        sut = sut.play("2i".toPosition())
        sut = sut.play("1i".toPosition())
        sut = sut.play("1h".toPosition())
        sut = sut.clean()
        assertEquals(null, sut.boardCells["1i".toPosition()])
    }

    @Test
    fun `kill stone in bottom left corner`() {
        var sut = Board()
        sut = sut.play("2a".toPosition())
        sut = sut.play("1a".toPosition())
        sut = sut.play("1b".toPosition())
        sut = sut.clean()
        assertEquals(null, sut.boardCells["1a".toPosition()])
    }

    @Test
    fun `kill stone in top right corner`() {
        var sut = Board()
        sut = sut.play("9h".toPosition())
        sut = sut.play("9i".toPosition())
        sut = sut.play("8i".toPosition())
        sut = sut.clean()
        assertEquals(null, sut.boardCells["9i".toPosition()])
    }
    @Test
    fun `kill stone in top left corner`() {
        var sut = Board()
        sut = sut.play("9b".toPosition())
        sut = sut.play("9a".toPosition())
        sut = sut.play("8a".toPosition())
        sut = sut.clean()
        assertEquals(null, sut.boardCells["9a".toPosition()])
    }

    @Test
    fun `simple suicide 1 white piece surrounded by 4 black`(){
        var sut = Board()
        sut = sut.play("9d".toPosition())
        sut = sut.play("1e".toPosition())
        sut = sut.play("8c".toPosition())
        sut = sut.play("1i".toPosition())
        sut = sut.play("7d".toPosition())
        sut = sut.play("1a".toPosition())
        sut = sut.play("8e".toPosition())
        assertTrue(sut.isSuicide("8d".toPosition()))
    }

    @Test
    fun `group suicide`(){
        var sut = Board()
        sut = sut.play("9d".toPosition())
        sut = sut.play("1e".toPosition())
        sut = sut.play("8c".toPosition())
        sut = sut.play("1a".toPosition())
        sut = sut.play("7c".toPosition())
        sut = sut.play("7d".toPosition())
        sut = sut.play("6d".toPosition())
        sut = sut.play("1f".toPosition())
        sut = sut.play("7e".toPosition())
        sut = sut.play("1h".toPosition())
        sut = sut.play("8e".toPosition())
        assertTrue(sut.isSuicide("8d".toPosition()))
    }

    @Test
    fun `corner suicide`(){
        var sut = Board()
        sut = sut.play("9b".toPosition())
        sut = sut.play("1f".toPosition())
        sut = sut.play("8a".toPosition())
        assertTrue(sut.isSuicide("9a".toPosition()))
    }
}