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
    val lastWasPast: Boolean = false,
)

fun Board.canPlay(pos:Position): Boolean =
    pos.row in 1..BOARD_SIZE && pos.col in 'A'..<'A' + BOARD_SIZE &&
    this.boardCells[pos] == null

fun Board.play(pos:Position):Board {
    require(pos.row in 1..BOARD_SIZE) { "Invalid position" }
    require(pos.col in 'A'..<'A' + BOARD_SIZE) { "Invalid position" }

    if (!this.canPlay(pos) || (this.isSuicide(pos) && !this.hasLibertiesAfterPlay(pos))) {
        println("Invalid play, suicide!")
        return this
    }

    else if (this.isSuicide(pos) && this.hasLibertiesAfterPlay(pos)) {
        val newBoardCells = boardCells.toMutableMap()
        newBoardCells[pos] = this.turn
        return Board(
            boardCells = newBoardCells,
            turn = turn.other,
            isFinished = false,
            whiteCaptures = whiteCaptures,
            blackCaptures = blackCaptures,
            lastWasPast = false
        ).clean(pos)
    }

    val newBoardCells = boardCells.toMutableMap()
    newBoardCells[pos] = this.turn

    return Board(
        boardCells = newBoardCells,
        turn = turn.other,
        isFinished = isFinished,
        whiteCaptures = whiteCaptures,
        blackCaptures = blackCaptures,
        lastWasPast = false
    ).clean(null)
}

fun Board.hasLibertiesAfterPlay(pos:Position): Boolean {
    val newBoardCells = boardCells.toMutableMap()
    newBoardCells[pos] = this.turn
    return Board(newBoardCells, turn, isFinished, whiteCaptures, blackCaptures, lastWasPast).clean(null).play(pos).exploreLiberties(pos,pos, mutableSetOf()) != 0
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
fun countLiberties(board:Board, pos:Position): Int {
    val visited = mutableSetOf<Position>()

    if (board.boardCells[pos] != null)
        return board.exploreLiberties(pos, pos, visited)

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
    if(pos.isValidPosition() && this.canPlay(pos)) {
        // Criamos uma nova board
        val newBoard = boardCells.toMutableMap()
        // Definimos a posição que estamos tentar jogar na nova board como o turn atual
        newBoard[pos] = this.turn
        // A partir desta nova board, exploramos as liberdades da peça inserida
        val liberties = Board(
            newBoard,
            turn,
            isFinished,
            whiteCaptures,
            blackCaptures,
            lastWasPast
        ).exploreLiberties(pos,pos, mutableSetOf())
        // Caso esta não tenha liberdades, significará que posicionar uma peça nessa posição resulta em suicidio.
        return liberties == 0
    }
    return false
}

fun Board.clean(except: Position?): Board {
    val newBoardCells = boardCells.toMutableMap()
    var newBlackCaptures = blackCaptures
    var newWhiteCaptures = whiteCaptures
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            if (countLiberties(this, Position(r, c.toChar())) == 0 && this.boardCells[Position(r, c.toChar())] != null && Position(r, c.toChar()) != except) {
                if (this.boardCells[Position(r, c.toChar())] == Piece.WHITE)
                    newBlackCaptures++
                else
                    newWhiteCaptures++

                newBoardCells[Position(r, c.toChar())] = null
            }
        }
    }
    return Board(newBoardCells, turn, isFinished ,whiteCaptures = newWhiteCaptures, blackCaptures = newBlackCaptures, lastWasPast )
}

fun Board.pass(): Board {
    // Check if the game is already finished
    if (isFinished) {
        return this
    }

    return if (lastWasPast) {
        Board(
            boardCells = boardCells,
            turn = turn.other,
            isFinished = true,
            whiteCaptures = whiteCaptures,
            blackCaptures = blackCaptures,
            lastWasPast = true
        )
    } else {
        Board(
            boardCells = boardCells,
            turn = turn.other,
            isFinished = false,
            whiteCaptures = whiteCaptures,
            blackCaptures = blackCaptures,
            lastWasPast = true
        )
    }
}


fun Board?.end(): Board? {
    return if(this == null) null
    else if (isFinished) {
        val (whiteScore, blackScore) = this.score()
        if (whiteScore > blackScore) {
            println("The winner is player 0 (White) with a score of $whiteScore - $blackScore")
        } else if (blackScore > whiteScore) {
            println("The winner is player # (Black) with a score of $blackScore - $whiteScore")
        }
        null
    }
    else this
}
fun Board.resign(): Board =
    Board(
        boardCells = boardCells,
        turn = turn.other,
        isFinished = true,
        whiteCaptures = whiteCaptures,
        blackCaptures = blackCaptures,
        lastWasPast = false
    )


fun Board.score(): Pair<Int, Double> {

    var whiteScore = whiteCaptures
    var blackScore = blackCaptures.toDouble()

    //Vamos precurer todas as peças do tabuleiro
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            //Caso uma seja null iremos chamar a função isSurrounded para verificar se está rodeada
            if (boardCells[Position(r, c.toChar())] == null) {
                val x = isSurrounded(Position(r, c.toChar()))

                //Se esta estiver rodeada, iremos incrementar o score do jogador que a rodeou
                if (x == Piece.WHITE) whiteScore++
                else if (x == Piece.BLACK) blackScore++
            }
        }
       }
    //Dependendo do tamanho do tabuleiro iremos retirar pontos ao score do jogador preto
    when(BOARD_SIZE){
        9 -> blackScore -= 3.5
        13 -> blackScore -= 4.5
        19 -> blackScore -= 5.5
    }
    return Pair(whiteScore, blackScore)
}

fun Board.isSurrounded(pos: Position): Piece? {

    var visited = mutableSetOf<Position>()
    var piece = mutableSetOf<Piece>()


    fun search(p: Position) {
        visited.add(p)
        //Vamos buscar as posições adjacentes
        val adjacentPos = p.getAdjacentPositions()

        //Para cada posição adjacente iremos verificar se esta já foi visitada e se é válida
        for (adjacent in adjacentPos) {
            if (!visited.contains(adjacent) && adjacent.isValidPosition()) {

                //Se esta for null iremos chamar a função search para continuar a verificação
                if (boardCells[adjacent] == null) search(adjacent)
                //Se esta for uma peça iremos adicionar ao set de peças
                else if (boardCells[adjacent] == Piece.BLACK) piece += Piece.BLACK
                else if (boardCells[adjacent] == Piece.WHITE) piece += Piece.WHITE
            }
        }
    }
    search(pos)

    //Vamos verificar se o espaço está redeado por 1 só tipo de peça
    return if(piece.contains(Piece.BLACK) && !piece.contains(Piece.WHITE)) Piece.BLACK
    else if(piece.contains(Piece.WHITE) && !piece.contains(Piece.BLACK)) Piece.WHITE
    else null
}




