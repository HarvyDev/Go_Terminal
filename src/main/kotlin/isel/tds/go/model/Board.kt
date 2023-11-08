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
    val turn: Piece = Piece.BLACK,
    val isFinished: Boolean = false,
    val whiteCaptures: Int = 0,
    val blackCaptures: Int = 0,
    val consecutivePasses: Int = 0

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
            else {
                print(" ${boardCells[Position(i, j.toChar())]?.symbol} ")
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

fun Board.isSuicide(pos: Position): Boolean{
    if(pos.isValidPosition() && canPlay(pos)) {
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
    var newBlackCaptures = blackCaptures
    var newWhiteCaptures = whiteCaptures
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            if (this.countLiberties(Position(r, c.toChar())) == 0 && this.boardCells[Position(r, c.toChar())] != null) {
                if(boardCells[Position(r, c.toChar())] == Piece.WHITE){
                    newBlackCaptures = blackCaptures + 1
                }
                else{
                    newWhiteCaptures = whiteCaptures + 1
                }
                newBoardCells[Position(r, c.toChar())] = null
            }
        }
    }
    return Board(newBoardCells, turn, isFinished ,whiteCaptures = newWhiteCaptures, blackCaptures = newBlackCaptures,consecutivePasses )
}


//class Board(
//    val boardCells: Map<Position, Piece?> = emptyMap(),
//    val turn: Piece = Piece.BLACK,
//    val isFinished: Boolean = false,
//    val whiteCaptures: Int = 0,
//    val blackCaptures: Int = 0,
//    val consecutivePasses: Int = 0
//
//)
fun Board.pass(): Board {
    // Check if the game is already finished
    if (isFinished) {
        return this
    }


    if(this.turn == Piece.BLACK){
        if(consecutivePasses == 1){
            return Board(boardCells, turn.other, true)
        }
        else {
            return Board(boardCells,turn.other,consecutivePasses = this.consecutivePasses + 1)
        }
    }
    else if(this.turn == Piece.WHITE){
        if(consecutivePasses == 1){
            return  Board(boardCells, turn.other, true)
        }
        else {
            return Board(boardCells,turn.other,consecutivePasses = this.consecutivePasses + 1)
        }
    }
    return this
}

fun Board?.end(): Board? {
    if(this == null) return null
    else if(isFinished) return null else return this
}

