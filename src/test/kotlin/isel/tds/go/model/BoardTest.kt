package isel.tds.go.model

import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertFailsWith
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
        assertFalse(sut.canPlay("9a".toPosition()))
        sut = sut.play("1a".toPosition())
        assertFalse(sut.canPlay("1a".toPosition()))
        sut = sut.play("9i".toPosition())
        assertFalse(sut.canPlay("9i".toPosition()))
        sut = sut.play("1i".toPosition())
        assertFalse(sut.canPlay("1i".toPosition()))
    }


    @Test
    fun `can play`() {
        var sut = Board()
        sut = sut.play(Position(1, 'A'))
        sut = sut.play(Position(2, 'A'))
        assertFalse(sut.canPlay(Position(1,'A')))
        assertFalse(sut.canPlay(Position(2, 'A')))
    }
}