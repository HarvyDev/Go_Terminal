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
    print(firstLine)
    Position.values.forEach { pos ->
        if (pos.col == 'A') {
            println()
            print(pos.row)
        }
        if (boardCells[pos] == null) {
            print(" . ")
        } else {
            print(" ${boardCells[pos]?.symbol} ")
        }
    }
    println()
}
fun Board.countLiberties(pos:Position): Int {
    val visited = mutableSetOf<Position>()

    if (boardCells[pos] != null)
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

fun Board.isSuicide(pos: Position): Boolean{
    if(boardCells[pos] == null) {
        // Criamos uma nova board
        val newBoard = boardCells.toMutableMap()
        // Definimos a posição que estamos tentar jogar na nova board como o turn atual
        newBoard[pos] = this.turn
        // A partir desta nova board, exploramos as liberdades da peça inserida
        val liberties = exploreLiberties(pos,pos, mutableSetOf())
        // Caso esta não tenha liberdades, significará que posicionar uma peça nessa posição resulta em suicidio.
        return liberties == 0

    }
    return false
}

fun Board.clean(): Board {
    val newBoardCells = boardCells.toMutableMap()
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            if (this.countLiberties(Position(r, c.toChar())) == 0) {
                newBoardCells[Position(r, c.toChar())] = null
            }
        }
    }
    return Board(newBoardCells, turn)
}
