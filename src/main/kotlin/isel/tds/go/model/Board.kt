package isel.tds.go.model

import java.lang.IllegalArgumentException

const val BOARD_SIZE = 9
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE
enum class Piece {
    WHITE, BLACK;
    val other: Piece get() = if (this == WHITE) BLACK else WHITE
}
class Board(
//    val boardCells: Map<Position, Piece?> = (1..BOARD_SIZE * BOARD_SIZE).associate { Position(it, 'A' + (it - 1)) to null },
    val boardCells: Map<Position, Piece?> = emptyMap(),
    val turn: Piece = Piece.BLACK
)

fun Board.canPlay(pos: Position): Boolean {
    return this.boardCells[pos] == null
}

fun Board.play(pos:Position):Board {

    require(pos.row in 1..BOARD_SIZE) { "Invalid position" }
    require(pos.col in 'A'..<'A' + BOARD_SIZE) { "Invalid position" }


    if (!canPlay(pos)) {
        return this
    }

    val newBoardCells = boardCells.toMutableMap()
    newBoardCells[pos] = this.turn

    return Board(newBoardCells, turn.other)
}

fun Board.show() {
    var firstLine = " "
    for (i in 0..<BOARD_SIZE) {
        firstLine += " " + ('A' + i) + " "
    }
    println(firstLine)
    for (i in BOARD_SIZE downTo 1) {
        print(i)
        for (j in 65..<65 + BOARD_SIZE) {
            if (boardCells[Position(i, j.toChar())] == null) {
                print(" . ")
            }
            else if (boardCells[Position(i, j.toChar())] == Piece.WHITE) {
                print(" O ")
            }
            else {
                print(" # ")
            }
        }
        println()
    }
}



fun Board.countLiberties(pos:Position): Int {
    val visited = mutableSetOf<Position>()

    if (pos.isValidPosition() && boardCells[pos] != null)
        return exploreLiberties(pos, pos, visited)

    return 0
}

private fun Board.exploreLiberties(initialPos: Position, currentPosition: Position, visited: MutableSet<Position>): Int {
    visited.add(currentPosition)

    val adjacentPositions = currentPosition.getAdjacentPositions()
    var liberties = 0

    for (adjacent in adjacentPositions) {
        if (!visited.contains(adjacent) && adjacent.isValidPosition()) {
            if (boardCells[adjacent] == null)
                liberties++
            else if (boardCells[adjacent] == boardCells[initialPos])
                liberties += exploreLiberties(initialPos, adjacent, visited)
        }
    }
    return liberties
}

fun Board.clean(): Board {
    val newBoardCells = boardCells.toMutableMap()
    for (r in 0..<BOARD_SIZE) {
        for (c in 65..65 + BOARD_SIZE) {
            if (this.countLiberties(Position(r, c.toChar())) == 0) {
                newBoardCells[Position(r, c.toChar())] = null
            }
        }
    }
    return Board(newBoardCells, turn)
}
