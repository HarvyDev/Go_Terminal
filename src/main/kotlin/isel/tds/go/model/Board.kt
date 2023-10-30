package isel.tds.go.model

const val BOARD_SIZE = 9
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE
enum class Piece {
    WHITE, BLACK;
    val other: Piece get() = if (this == WHITE) BLACK else WHITE
}
class Board(
    val boardCells: Map<Position, Piece?> = (0..<BOARD_SIZE).flatMap { row ->
        ('A'..<('A' + BOARD_SIZE)).map { col ->
            Position(row, col) to null
        }
    }.toMap(),
    val turn: Piece = Piece.WHITE
)

fun Board.play() {
    TODO()
}

fun Board.show() {
    var colCount = 0
    var rowCount = 0
    var firstLine = "  "
    for (i in 0..< BOARD_SIZE) {
        firstLine += " " + ('A' + i) + " "
    }
    println(firstLine)
    print("$rowCount ")
    this.boardCells.values.forEach { pos ->
        print("${pos ?: " . "}")
        if (colCount == BOARD_SIZE - 1 && rowCount < BOARD_SIZE - 1) {
            rowCount++
            println()
            print("$rowCount ")
            colCount = -1
        }
        colCount++
    }
    println()
}
