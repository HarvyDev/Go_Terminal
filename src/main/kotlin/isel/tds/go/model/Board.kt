package isel.tds.go.model

const val BOARD_SIZE = 9
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE
enum class Piece {
    WHITE, BLACK;
    val other: Piece get() = if (this == WHITE) BLACK else WHITE
    val symbol: String
        get() = when (this) {
            WHITE -> "O"
            BLACK -> "#"
        }
}

class Board(
    val boardCells: Map<Position, Piece?> = emptyMap(),
)





