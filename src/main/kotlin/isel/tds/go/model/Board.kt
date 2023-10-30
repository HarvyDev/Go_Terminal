package isel.tds.go.model

const val BOARD_SIZE = 9
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE
enum class Piece {
    WHITE, BLACK;
    val other: Piece get() = if (this == WHITE) BLACK else WHITE
}
class Board(
//    val boardCells: Map<Position, Piece?> = (1..BOARD_SIZE * BOARD_SIZE).associate { Position(it, 'A' + (it - 1)) to null },
    val boardCells: Map<Position, Piece?> = emptyMap(),
    val turn: Piece = Piece.WHITE
)

fun Board.canPlay(pos: Position): Boolean {
    return this.boardCells[pos] == null
}

fun Board.play(pos:Position):Board {

    val newBoardCells = boardCells.toMutableMap()
    newBoardCells[pos] = this.turn

    return Board(newBoardCells, turn.other)
}

fun Board.show() {
    var firstLine = " "
    for (i in 0.. BOARD_SIZE) {
        firstLine += " " + ('A' + i) + " "
    }
    println(firstLine)
    for (i in 0..<BOARD_SIZE) {
        print(BOARD_SIZE - i)
        for (j in 65..65 + BOARD_SIZE) {
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
